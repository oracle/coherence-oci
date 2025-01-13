/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.ListObjects;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.DeleteObjectResponse;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.oracle.bmc.responses.BmcResponse;
import com.tangosol.net.CacheFactory;

/**
 * A class used by the {@link ObjectStorageSnapshotArchiver} to perform operations against
 * an OCI object storage bucket to store, retrieve and delete archived snapshots.
 */
@SuppressWarnings("deprecated")
public class ObjectStorageManager
    {

    // ----- constructors ---------------------------------------------------

    /**
     * Construct an {@link ObjectStorageManager} that uses an OCI profile in ~/.oci/config to authenticate to
     * an object storage instance.
     *
     * @param sProfile to use to authenticate.
     * @param sBucket  bucket to use
     *
     * @throws ObjectStorageManagerException if any initialization errors
     */
    public ObjectStorageManager(String sProfile, String sBucket)
            throws ObjectStorageManagerException
        {
        f_sBucket = sBucket;

        try
            {
            final ConfigFileReader.ConfigFile   configFile   = ConfigFileReader.parseDefault(sProfile);
            final AuthenticationDetailsProvider authProvider = new ConfigFileAuthenticationDetailsProvider(configFile);

            f_ociClient  = ObjectStorageClient.builder().build(authProvider);
            f_sNamespace = f_ociClient.getNamespace(GetNamespaceRequest.builder().build()).getValue();
            }
        catch (Exception e)
            {
            throw new ObjectStorageManagerException("Failed to parse config file.", e);
            }
        }

    /**
     * Construct an {@link ObjectStorageManager} that uses individual elements to connect to
     * an object storage instance.
     * @param sBucket          bucket to store into
     * @param sTenancyOCID     tenancy OCID
     * @param sRegion          region code
     * @param sUserOCID        OCID of the user ID
     * @param sFingerPrint     API key fingerprint
     * @param sPrivateKeyPath  path to the private key path
     *
     * @throws ObjectStorageManagerException if any initialization errors
     */
    public ObjectStorageManager(String sBucket, String sTenancyOCID, String sRegion, String sUserOCID, String sFingerPrint, String sPrivateKeyPath)
            throws ObjectStorageManagerException
        {
        f_sBucket = sBucket;

        try {
            byte[] abPrivateKey = Files.readAllBytes(Paths.get(sPrivateKeyPath));

            SimpleAuthenticationDetailsProvider provider =
                    SimpleAuthenticationDetailsProvider.builder()
                                               .tenantId(sTenancyOCID)
                                               .userId(sUserOCID)
                                               .fingerprint(sFingerPrint)
                                               .privateKeySupplier(() -> new ByteArrayInputStream(abPrivateKey))
                                               //.passPhrase("my_key_passphrase".toCharArray())  // TODO: pass phrase if required
                                               .region(Region.fromRegionCode(sRegion))
                                               .build();
            
            f_ociClient  = ObjectStorageClient.builder().build(provider);
            f_sNamespace = f_ociClient.getNamespace(GetNamespaceRequest.builder().build()).getValue();
            }
        catch (Exception e)
            {
            throw new ObjectStorageManagerException("Failed load from environment", e);
            }
        }

    /**
     * Deletes a file. If this is the last file in a directory, the directory will also be deleted.
     *
     * @param sFullFileName file to delete
     *
     * @throws ObjectStorageManagerException if any errors
     */
    public void deleteFile(String sFullFileName) throws ObjectStorageManagerException
        {
        CacheFactory.log("deleteFile " + sFullFileName, CacheFactory.LOG_QUIET);
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                                                                         .namespaceName(f_sNamespace)
                                                                         .bucketName(f_sBucket)
                                                                         .objectName(sFullFileName)
                                                                         .build();

            DeleteObjectResponse response = f_ociClient.deleteObject(deleteObjectRequest);
            ensureResponse(response);
            }
        catch (Exception e)
            {
             throw new ObjectStorageManagerException("Unable to delete file " + sFullFileName + " from bucket " + f_sBucket, e);
            }
        }

    /**
     * Returns the file content as an {@link InputStream}.
     *
     * @param sFullFileName full path to file
     * @return  {@link InputStream} to be read from containing the data
     *
     * @throws ObjectStorageManagerException if any errors
     */
    public InputStream getFileAsStream(String sFullFileName) throws ObjectStorageManagerException {
        try
           {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                                                                .namespaceName(f_sNamespace)
                                                                .bucketName(f_sBucket)
                                                                .objectName(sFullFileName)
                                                                .build();

            GetObjectResponse response = f_ociClient.getObject(getObjectRequest);
            ensureResponse(response);

            return response.getInputStream();
            }
        catch (Exception e)
            {
            throw new ObjectStorageManagerException("Unable to get file " + sFullFileName + "  as stream from bucket " + f_sBucket, e);
           }
        }

    /**
     * List the objects at the file location.
     *
     * @param sFullFileName file path to list
     *
     * @return a {@link List} of file names
     *
     * @throws ObjectStorageManagerException if any errors
     */
    public List<String> listObjects(String sFullFileName)
            throws ObjectStorageManagerException
        {
        try
            {
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                                                                      .namespaceName(f_sNamespace)
                                                                      .bucketName(f_sBucket)
                                                                      .prefix(sFullFileName)
                                                                      .build();

           ListObjectsResponse response = f_ociClient.listObjects(listObjectsRequest);
           ensureResponse(response);
           
           return response.getListObjects().getObjects().stream().map(ObjectSummary::getName).collect(Collectors.toList());
            }
        catch (Exception e)
            {
            throw new ObjectStorageManagerException("Unable to list file " + sFullFileName + " from bucket " + f_sBucket, e);
            }
        }

    /**
     * Lists only the "directories" at the path name.
     *
     * @param sFullFileName path to list
     * @param fSubDirsOnly true to include sub directories
     * @return a {@link String} array containing the directories
     *
     * @throws ObjectStorageManagerException if any errors
     */
    public String[] listDirectory(String sFullFileName, boolean fSubDirsOnly) throws ObjectStorageManagerException
        {
        CacheFactory.log("listDirectory " + sFullFileName, CacheFactory.LOG_QUIET);
        Set<String> setDirectories  = new HashSet<>();
        try
            {
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                                                                      .namespaceName(f_sNamespace)
                                                                      .bucketName(f_sBucket)
                                                                      .prefix(sFullFileName)
                                                                      .delimiter("/")
                                                                      .build();

            ListObjectsResponse response = f_ociClient.listObjects(listObjectsRequest);
            ensureResponse(response);

            ListObjects listObjects = response.getListObjects();

            if (fSubDirsOnly)
                {
                listObjects.getPrefixes().forEach(o -> setDirectories.add(basename(o.endsWith("/") ? o.substring(0, o.length() - 1) : o)));
                }
            else
                {
                listObjects.getObjects().forEach(o -> setDirectories.add(basename(o.getName())));
                }
             }
        catch (Exception e)
            {
            throw new ObjectStorageManagerException("Unable to list directory " + sFullFileName + " from bucket " + f_sBucket, e);
            }

        return setDirectories.toArray(new String[0]);
        }

    /**
     * Uploads the given file to the path specified.
     *
     * @param fileUpload  {@link File} to upload
     * @param sFullFileName full path to store the file in
     *
     * @throws ObjectStorageManagerException if any errors
     */
    public void uploadFile(File fileUpload, String sFullFileName) throws ObjectStorageManagerException
        {
        CacheFactory.log("uploadFile file=" + fileUpload + ", fullFileName " + sFullFileName, CacheFactory.LOG_QUIET);
        try (FileInputStream fis = new FileInputStream(fileUpload))
            {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .namespaceName(f_sNamespace)
                                                                .bucketName(f_sBucket)
                                                                .objectName(sFullFileName)
                                                                .putObjectBody(fis)
                                                                .build();
            PutObjectResponse response  = f_ociClient.putObject(putObjectRequest);
            ensureResponse(response);
           }
        catch (Exception e)
            {
            throw new ObjectStorageManagerException("Unable to upload file " + sFullFileName + " to bucket " + f_sBucket, e);
            }
        }

    /**
     * Upload the contents of the {@link InputStream} to the file path specified.
     * @param fis {@link InputStream} to upload
     * @param sFullFileName full path to store the file in
     *
     * @throws ObjectStorageManagerException if any errors
     */
    public void uploadFile(InputStream fis, String sFullFileName) throws ObjectStorageManagerException
        {
        CacheFactory.log("uploadFile FileInputStream, fullFileName " + sFullFileName, CacheFactory.LOG_QUIET);
        try
            {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .namespaceName(f_sNamespace)
                                                                .bucketName(f_sBucket)
                                                                .objectName(sFullFileName)
                                                                .putObjectBody(fis)
                                                                .build();
            PutObjectResponse response  = f_ociClient.putObject(putObjectRequest);
            ensureResponse(response);
            }
        catch (Exception e)
            {
            throw new ObjectStorageManagerException("Unable to upload file " + sFullFileName + " to bucket " + f_sBucket, e);
            }
        }

    /**
     * Ensures a directory exists by uploading zero bytes of data.
     * 
     * @param sDirectory directory to ensure
     * @throws ObjectStorageManagerException is any errors
     */
    public void ensureDirectory(String sDirectory) throws ObjectStorageManagerException
        {
        CacheFactory.log("ensureDirectory" + sDirectory, CacheFactory.LOG_QUIET);

        try
            {
            uploadFile(EMPTY_DATA, sDirectory);
            }
        catch (Exception e)
            {
            throw new ObjectStorageManagerException("Unable to ensure directory " + sDirectory + " in bucket " + f_sBucket, e);
            }
        }

    /**
     * Ensures a response succeeded otherwise throws a {@link ObjectStorageManagerException}.
     *
     * @param response {@link BmcResponse} received from operation
     */
    private void ensureResponse(BmcResponse response) throws ObjectStorageManagerException
         {
        int nStatusCode = response.get__httpStatusCode__();
        if (nStatusCode >= 200 && nStatusCode < 300)
            {
            CacheFactory.log("Operation succeeded with HTTP status code: " + response.get__httpStatusCode__(), CacheFactory.LOG_QUIET);
            String sRequestId = null;
            if (response instanceof PutObjectResponse)
                {
                sRequestId = ((PutObjectResponse) response).getOpcRequestId();
                }
            else if (response instanceof DeleteObjectResponse)
                {
                sRequestId = ((DeleteObjectResponse) response).getOpcRequestId();
                }
            else if (response instanceof ListObjectsResponse)
                {
                sRequestId = ((ListObjectsResponse) response).getOpcRequestId();
                }
            else if (response instanceof GetObjectResponse)
                {
                sRequestId = ((GetObjectResponse) response).getOpcRequestId();
                }
            else
                {
                sRequestId = response.toString();
                }
            CacheFactory.log(response.getClass().getSimpleName() + " OPC-Request-ID: " + sRequestId, CacheFactory.LOG_QUIET);
            }
        else
            {
            throw new ObjectStorageManagerException("Failed to perform operation " + response +
                                                        ", received HTTP status code " + nStatusCode);
            }
        }

    // ----- helpers --------------------------------------------------------

    /**
     * Return the file name or basename of a full path.
     *
     * @param sFullPath  the full path
     * @return file name component
     */
    private String basename(String sFullPath)
        {
        int i = sFullPath.lastIndexOf('/');
        return i == -1 ? sFullPath : sFullPath.substring(i + 1);
        }

    // ----- constants ------------------------------------------------------

    private static final ByteArrayInputStream EMPTY_DATA = new ByteArrayInputStream(new byte[0]);

    // ----- data members ---------------------------------------------------

    /**
     * {@link ObjectStorage} client to use for bucket operations.
     */
    private final ObjectStorage f_ociClient;

    /**
     * Bucket to upload to.
     */
    private final String  f_sBucket;

    /**
     * Namespace associated with the bucket.
     */
    private final String f_sNamespace;
    }
