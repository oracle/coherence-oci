/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import com.oracle.coherence.persistence.PersistenceManager;
import com.tangosol.coherence.config.Config;
import com.tangosol.io.FileHelper;
import com.tangosol.io.ReadBuffer;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.management.AnnotatedStandardEmitterMBean;
import com.tangosol.net.management.Registry;
import com.tangosol.persistence.AbstractSnapshotArchiver;
import com.tangosol.persistence.CachePersistenceHelper;
import com.tangosol.persistence.Snapshot;

/**
 * An implementation of a {@link AbstractSnapshotArchiver} that archives partitions to OCI ObjectStore.
 */
@SuppressWarnings("deprecated")
public class ObjectStorageSnapshotArchiver
        extends AbstractSnapshotArchiver implements SnapshotArchiverMBean
    {

    // ----- constructors ---------------------------------------------------

    /**
     * Constructs a {@link ObjectStorageSnapshotArchiver} to archive persistent snapshots to OCI Object storage.
     * This constructor uses a profile.
     * The base directory will be under the specified bucket with the prefix.
     * prefix/{cluster}/{service}
     *
     * @param sClusterName cluster name
     * @param sServiceName service name
     * @param sBucket      bucket to store
     * @param sPrefix      prefix to use
     * @param sOciProfile  profile to use from .oci/config
     */
    public ObjectStorageSnapshotArchiver(String sClusterName, String sServiceName, String sBucket, String sPrefix, String sOciProfile)
        {
        super(sClusterName, sServiceName);
        f_sBucket = sBucket;
        f_sPrefix = sPrefix;

        try
            {
            init();

            CacheFactory.log(String.format("ObjectStorageSnapshotArchiver{profile=%s, bucket=%s, prefix=%s, fullPrefix=%s}", sOciProfile, f_sBucket, f_sPrefix,
                    m_sFulLPrefix), CacheFactory.LOG_INFO);

            f_manager = new ObjectStorageManager(sOciProfile, f_sBucket);
            ensureDirectory(m_sFulLPrefix);
            }
        catch (Exception e)
            {
            throw CachePersistenceHelper.ensurePersistenceException(e, "Unable to instantiate ObjectStorageSnapshotArchiver");
            }
        }

    /**
     * Constructs a {@link ObjectStorageSnapshotArchiver} to archive persistent snapshots to OCI Object storage.
     * This constructor does not use a profile, and assumes the environment variables will contain the required arguments.
     *
     * The base directory will be under the specified bucket with the prefix.
     * prefix/{cluster}/{service}
     *
     * @param sClusterName cluster name
     * @param sServiceName service name
     * @param sBucket      bucket to store
     * @param sPrefix      prefix to use
     */
    public ObjectStorageSnapshotArchiver(String sClusterName, String sServiceName, String sBucket, String sPrefix)
        {
        super(sClusterName, sServiceName);
        f_sBucket = sBucket;
        f_sPrefix = sPrefix;

        try
            {
            init();

            String sTenancyOCID    = System.getenv("OCI_ARCHIVER_TENANCY_OCID");
            String sRegion         = System.getenv("OCI_ARCHIVER_REGION");
            String sUserOCID       = System.getenv("OCI_ARCHIVER_USER_OCID");
            String sFingerPrint    = System.getenv("OCI_ARCHIVER_FINGERPRINT");
            String sPrivateKeyPath = System.getenv("OCI_ARCHIVER_PRIVATE_KEY_PATH");

            CacheFactory.log(String.format("ObjectStorageSnapshotArchiver{from env, tenancyOCID=%s, region=%s, userOCID=%s, fingerPrint=**, privateKey=**, bucket=%s, prefix=%s, fullPrefix=%s}",
                    sTenancyOCID, sRegion, sUserOCID, f_sBucket, f_sPrefix, m_sFulLPrefix), CacheFactory.LOG_INFO);

            f_manager = new ObjectStorageManager(f_sBucket, sTenancyOCID, sRegion, sUserOCID, sFingerPrint, sPrivateKeyPath);
            ensureDirectory(m_sFulLPrefix);
            }
        catch (Exception e)
            {
            throw CachePersistenceHelper.ensurePersistenceException(e, "Unable to instantiate ObjectStorageSnapshotArchiver");
            }
        }

    // ---- AbstractSnapshotArchiver methods --------------------------------

    @Override
    protected String[] listInternal()
        {
        try
            {
           return stripSpecialFiles(f_manager.listDirectory(m_sFulLPrefix, true));
            }
        catch (ObjectStorageManagerException e)
            {
            throw CachePersistenceHelper.ensurePersistenceException(e, "listInternal");
            }
        }

    @Override
    protected void archiveInternal(Snapshot snapshot, PersistenceManager<ReadBuffer> mgr)
        {
        String sSnapshot = snapshot.getName();
        CacheFactory.log(ensureMessage("Archiving snapshot " + sSnapshot), CacheFactory.LOG_INFO);

        ensureDirectory(m_sFulLPrefix);

        recordStartTime();
        List<Callable<Void>> listTasks = new ArrayList<>();
        for (String sStore : snapshot.listStores())
            {
            listTasks.add(new ArchiveOperation(this, mgr, sSnapshot, sStore));
            }

        invokeAllTasks(listTasks, ARCHIVE_SNAPSHOT);
        recordEndTime();
        }

    @Override
    protected void retrieveInternal(Snapshot snapshot, PersistenceManager<ReadBuffer> mgr)
        {
        String sSnapshot = snapshot.getName();
        CacheFactory.log(ensureMessage("Retrieving snapshot " + sSnapshot));

        ensureDirectory(m_sFulLPrefix);

        recordStartTime();
        List<Callable<Void>> listTasks = new ArrayList<>();
        for (String sStore : snapshot.listStores())
            {
            listTasks.add(new RetrieveOperation(this, mgr, sSnapshot, sStore));
            }

        invokeAllTasks(listTasks, RETRIEVE_SNAPSHOT);
        recordEndTime();
        }

    @Override
    protected boolean removeInternal(String sSnapshot)
        {
        String sSnapshotDir  = getSnapshotDirectory(m_sFulLPrefix, sSnapshot);
        CacheFactory.log(ensureMessage("Removing snapshot " + sSnapshotDir), CacheFactory.LOG_INFO);

        // we get the list of stores and then delete them in parallel as it is quicker
        try
            {
            List<Callable<Void>> listTasks = new ArrayList<>();
            for (String sStore : f_manager.listObjects(sSnapshotDir))
                {
                listTasks.add(new DeleteOperation(this, null, sSnapshot, sStore));
                }

            invokeAllTasks(listTasks, DELETE_SNAPSHOT);
            }
        catch (ObjectStorageManagerException e)
            {
            CacheFactory.log(ensureMessage("Failed to delete snapshot " + sSnapshotDir + " " + e.getMessage()),CacheFactory.LOG_WARN);
            return false;
            }
        CacheFactory.log(ensureMessage("Removed snapshot directory " + sSnapshotDir), CacheFactory.LOG_INFO);
        return true;
        }

    @Override
    protected String[] listStoresInternal(String sSnapshot)
        {
        String sSnapshotDir = getSnapshotDirectory(m_sFulLPrefix, sSnapshot);

        try
            {
            return stripSpecialFiles(f_manager.listDirectory(sSnapshotDir + SEP, false));
            }
        catch (ObjectStorageManagerException e)
            {
            throw CachePersistenceHelper.ensurePersistenceException(e, "Unable to execute listStoresInternal()");
            }
        }

    @Override
    protected Properties getMetadata(String sSnapshotDir) throws IOException
        {
        File fileTemp = null;

        try (InputStream is = f_manager.getFileAsStream(sSnapshotDir + SEP + CachePersistenceHelper.META_FILENAME))
            {
            fileTemp = FileHelper.createTempDir();
            Files.copy(is, new File(fileTemp, CachePersistenceHelper.META_FILENAME).toPath());

            return CachePersistenceHelper.readMetadata(fileTemp);
            }
        catch (ObjectStorageManagerException e)
            {
            throw new IOException("Unable to read metadata", e);
            }
        finally
            {
            if (fileTemp != null)
                {
                FileHelper.deleteDirSilent(fileTemp);
                }
            }
        }

    /**
     * Returns if the store is empty. Returns false as it is not possible to easily determine this.
     * @param sSnapshot  snapshot
     * @param sStore     store
     * @return if the store is empty
     * @throws IOException if any errors
     */
    protected boolean isEmpty(String sSnapshot, String sStore) throws IOException
        {
        return false;
        }

    // ---- SnapshotArchiverMBean interface ---------------------------------

    @Override
    public long getArchiverThreadCount()
        {
        return f_nArchiverThreads;
        }

    @Override
    public long getArchivedStoresCount()
        {
        return f_archivedStoresCount.get();
        }

    @Override
    public long getArchivedStoresTotalMillis()
        {
        return f_archivedStoresTotalMillis.get();
        }

    @Override
    public long getArchivedStoresFailures()
        {
        return f_archivedStoresFailures.get();
        }

    @Override
    public float getArchivedStoresAverageMillis()
        {
        long nCount = f_archivedStoresCount.get();
        return nCount == 0 ? 0 : (f_archivedStoresTotalMillis.get() * 1.0f) / nCount;
        }

    @Override
    public long getArchivedStoresMaxMillis()
        {
        return f_archivedStoresMaxMillis.get();
        }

    @Override
    public long getArchivedStoresLastDurationMillis()
        {
        return f_archivedStoresLastDurationMillis.get();
        }

    @Override
    public long getRetrievedStoresCount()
        {
        return f_retrievedStoresCount.get();
        }

    @Override
    public long getRetrievedStoresTotalMillis()
        {
        return f_retrievedStoresTotalMillis.get();
        }

    @Override
    public long getRetrievedStoresFailures()
        {
        return f_retrievedStoresFailures.get();
        }

    @Override
    public float getRetrievedStoresAverageMillis()
        {
        long nCount = f_retrievedStoresCount.get();
        return nCount == 0 ? 0 : (f_retrievedStoresTotalMillis.get() * 1.0f) / nCount ;
        }

    @Override
    public float getRetrievedStoresMaxMillis()
        {
        return f_retrievedStoresMaxMillis.get();
        }

    @Override
    public long getRetrievedStoresLastDurationMillis()
        {
        return f_retrievedStoresLastDurationMillis.get();
        }

    @Override
    public long getDeletedStoresCount()
        {
        return f_deletedStoresCount.get();
        }

    @Override
    public long getDeletedStoresTotalMillis()
        {
        return f_deletedStoresTotalMillis.get();
        }

    @Override
    public long getDeletedStoresFailures()
            {
        return f_deletedStoresFailures.get();
    }

    @Override
    public float getDeletedStoresAverageMillis()
        {
        long nCount = f_deletedStoresCount.get();
        return nCount == 0 ? 0 : (f_deletedStoresTotalMillis.get() * 1.0f) / nCount;
        }

    @Override
    public float getDeletedStoresMaxMillis()
        {
        return f_deletedStoresMaxMillis.get();
         }

    @Override
    public long getDeleteStoresLastDurationMillis()
        {
        return f_deletedStoresLastDurationMillis.get();
        }

    @Override
    public void resetArchiverStatistics()
        {
         f_archivedStoresCount.set(0L);
         f_archivedStoresFailures.set(0L);
         f_archivedStoresTotalMillis.set(0L);
         f_archivedStoresMaxMillis.set(0L);

         f_retrievedStoresFailures.set(0L);
         f_retrievedStoresTotalMillis.set(0L);
         f_retrievedStoresMaxMillis.set(0L);
         f_retrievedStoresFailures.set(0L);

         f_deletedStoresFailures.set(0L);
         f_deletedStoresTotalMillis.set(0L);
         f_deletedStoresMaxMillis.set(0L);
         f_deletedStoresFailures.set(0L);
        }

    // ----- helpers --------------------------------------------------------

    /**
     * Register an archive operation.
     * @param ltdDurationMillis duration of the operation.
     */
    public void registerArchive(long ltdDurationMillis)
        {
        f_archivedStoresTotalMillis.addAndGet(ltdDurationMillis);
        f_archivedStoresCount.incrementAndGet();
        if (ltdDurationMillis > f_archivedStoresMaxMillis.get())
            {
            f_archivedStoresMaxMillis.set(ltdDurationMillis);
            }
        }

    /**
     * Register an archive failed.
     */
    public void registerArchiveFailed()
        {
        f_archivedStoresFailures.incrementAndGet();
        }

    /**
     * Register a retrieve operation.
     * @param ltdDurationMillis duration of the operation.
     */
    public void registerRetrieve(long ltdDurationMillis)
        {
        f_retrievedStoresTotalMillis.addAndGet(ltdDurationMillis);
        f_retrievedStoresCount.incrementAndGet();
        if (ltdDurationMillis > f_retrievedStoresMaxMillis.get())
            {
            f_retrievedStoresMaxMillis.set(ltdDurationMillis);
            }
        }

    /**
     * Register a retrieve failed.
     */
    public void registerRetrieveFailed()
        {
        f_retrievedStoresFailures.incrementAndGet();
        }

    /**
     * Register a delete operation.
     * @param ltdDurationMillis duration of the operation.
     */
    public void registerDelete(long ltdDurationMillis)
        {
        f_deletedStoresTotalMillis.addAndGet(ltdDurationMillis);
        f_deletedStoresCount.incrementAndGet();
        if (ltdDurationMillis > f_deletedStoresMaxMillis.get())
            {
            f_deletedStoresMaxMillis.set(ltdDurationMillis);
            }
        }

    /**
     * Register a delete failed.
     */
    public void registerDeleteFailed()
        {
        f_deletedStoresFailures.incrementAndGet();
        }

    /**
     * Common initialization.
     */
    private void init()
        {
        if (f_sPrefix.startsWith(SEP))
            {
            throw new IllegalArgumentException("Prefix must not start with '" + SEP + "'");
            }

        m_sFulLPrefix = ensureDirectoryName(FileHelper.toFilename(f_sPrefix) + SEP + FileHelper.toFilename(f_sClusterName) + SEP +
                                            FileHelper.toFilename(f_sServiceName));
        
        CacheFactory.log("ObjectStorageSnapshotArchiver: Archiver threads: " + f_nArchiverThreads, CacheFactory.LOG_INFO);
        m_executor = Executors.newFixedThreadPool(f_nArchiverThreads);
        m_executorWriter = Executors.newFixedThreadPool(f_nArchiverThreads);

        // register MBean Async
        if (f_fMBeanEnabled)
            {
            m_executor.submit(this::registerMBean);
            }
        }

    /**
     * Register the ObjectSnapshotArchiver MBean.
     */
    private void registerMBean()
        {
        try
            {
            Registry registry = CacheFactory.ensureCluster().getManagement();
            if (registry != null)
                {
                String sName = registry.ensureGlobalName(getMBeanName());
                CacheFactory.log("Registering MBean: " + sName, CacheFactory.LOG_INFO);
                registry.register(sName, new AnnotatedStandardEmitterMBean(this, SnapshotArchiverMBean.class));
                }
            }
        catch (Throwable e)
            {
            CacheFactory.log("Failed to register archiver MBean; " + e, CacheFactory.LOG_WARN);
            }
        }

    /**
     * Returns the name to register the MBean with.
     * @return the name to register the MBean with
     */
    private String getMBeanName()
        {
        return "type=" + this.getClass().getSimpleName() + "," + Registry.KEY_SERVICE + f_sServiceName;
        }

    /**
     * Invokes all the tasks using the configured executor.
     * @param listTasks         tasks to invoke
     * @param sTaskDescription  description of the tasks
     */
    private void invokeAllTasks(List<Callable<Void>> listTasks, String sTaskDescription)
        {
        try
            {
            long ltdStart = System.currentTimeMillis();

            List<Future<Void>> listFutures = m_executor.invokeAll(listTasks);

            int nFutureCount  = listFutures.size();
            int nSuccessCount = 0;
            for (Future<Void> future : listFutures)
                {
                try
                    {
                    future.get();
                    nSuccessCount++;
                    }
                catch (ExecutionException e)
                    {
                    CacheFactory.log(ensureMessage(sTaskDescription + " failed " + e.getCause()), CacheFactory.LOG_WARN);
                  }
                catch (InterruptedException e)
                    {
                    Thread.currentThread().interrupt();
                    CacheFactory.log(ensureMessage(sTaskDescription + " interrupted " +  e.getCause()), CacheFactory.LOG_WARN);
                    }
                }

            long nTotalDuration = System.currentTimeMillis() - ltdStart;
            if (nSuccessCount == nFutureCount)
                {
                CacheFactory.log(ensureMessage(String.format("%d '%s' tasks succeeded, archiver threads: %d", nSuccessCount, sTaskDescription, f_nArchiverThreads)), CacheFactory.LOG_INFO);
                CacheFactory.log(ensureMessage(String.format("overall duration: %,dms", nTotalDuration)), CacheFactory.LOG_INFO);

                if (DELETE_SNAPSHOT.equals(sTaskDescription))
                    {
                    f_deletedStoresLastDurationMillis.set(nTotalDuration);
                    }
                else if (ARCHIVE_SNAPSHOT.equals(sTaskDescription))
                    {
                    f_archivedStoresLastDurationMillis.set(nTotalDuration);
                    }
                else
                    {
                    f_retrievedStoresLastDurationMillis.set(nTotalDuration);
                    }
                }
            else
                {
                String sError =  String.format("%d %s out of %d tasks failed", nFutureCount - nSuccessCount, sTaskDescription, nFutureCount);
                throw CachePersistenceHelper.ensurePersistenceException(new RuntimeException(sError));
                }
            }
        catch (InterruptedException e)
            {
            CacheFactory.log("ObjectStorageSnapshotArchiver: Unable to submit archiver tasks " + e.getMessage(), CacheFactory.LOG_WARN);
            throw CachePersistenceHelper.ensurePersistenceException(e);
            }
        }

    /**
     * Ensures a directory exists on the OCI bucket. This is achieved by writing a ser length file.
     * @param sDirectory directory to ensure
     */
    private void ensureDirectory(String sDirectory)
        {
        CacheFactory.log(ensureMessage("ensuring directory " + sDirectory), CacheFactory.LOG_QUIET);
        try
            {
            f_manager.ensureDirectory(sDirectory);
            }
        catch (ObjectStorageManagerException e)
            {
            throw CachePersistenceHelper.ensurePersistenceException(e, "Unable to ensure directory " + sDirectory);
            }
        }

    /**
     * Ensures a directory ends and ends with "/".
     * @return ensured directory
     */
    private String ensureDirectoryName(String sDirectory)
        {
        return sDirectory.endsWith(SEP) ? sDirectory : sDirectory + SEP;
        }

    /**
     * Ensures a message to be displayed.
     * @param sMessage message
     * @return the formatted message
     */
    private String ensureMessage(String sMessage)
        {
        return this.getClass().getSimpleName() + " - " + sMessage;
        }

    /**
     * Strips out the files that are special such as the META_FILE
     * @param asFiles array
     * @return the array of files
     */
    private String[] stripSpecialFiles(String[] asFiles)
        {
        List<String> arrayList = new ArrayList<>();

        for (String sFile : asFiles)
            {
            if (!"".equals(sFile) && !CachePersistenceHelper.META_FILENAME.equals(sFile))
                {
                arrayList.add(sFile);
                }
            }

        return arrayList.toArray(new String[0]);
        }

    /**
     * Return a snapshot directory fully qualified path.
     *
     * @param sFullPrefix full prefix
     * @param sSnapshot   the snapshot name to get path for
     *
     * @return a snapshot directory fully qualified path
     */
    public static String getSnapshotDirectory(String sFullPrefix, String sSnapshot)
        {
        return sFullPrefix + FileHelper.toFilename(sSnapshot);
        }

    // ----- accessors ------------------------------------------------------

    /**
     * Returns the {@link ExecutorService} to be used for writing the store.
     * @return {@link ExecutorService} to be used for writing the store
     */
    protected ExecutorService getExecutorWriter()
        {
        return m_executorWriter;
        }

    /**
     * Returns the {@link ObjectStorageManager} used for this archiver.
     * @return the {@link ObjectStorageManager} used for this archiver
     */
    public ObjectStorageManager getObjectStorageManager()
        {
        return f_manager;
        }

    /**
     * Returns the full archiver prefix.
     * @return the full archiver prefix
     */
    protected String getFulLPrefix()
        {
        return m_sFulLPrefix;
        }

    /**
     * Write the metadata file.
     * @param fileDir     file to write to
     * @param mgr         {@link PersistenceManager} to use to write
     * @param sStore      Store to write
     * @throws IOException if any errors
     */
    public void writeMetadataFile(File fileDir, PersistenceManager<ReadBuffer> mgr, String sStore) throws IOException
        {
        writeMetadata(fileDir, mgr, sStore);
        }

    // ----- constants ------------------------------------------------------

    /**
     * Separator used for OCI Object Storage directories.
     */
    public static final String SEP = "/";

    /**
     * The number of threads in the {@link ExecutorService} to be used to perform operations.
     */
    private static final int     f_nArchiverThreads = Config.getInteger("coherence.distributed.persistence.oci.archiver.threads", 4);

    /**
     * Indicates if MBean is enabled.
     */
    private static final boolean f_fMBeanEnabled    = Config.getBoolean("coherence.distributed.persistence.oci.archiver.mbean.enabled", true);

    /**
     * Archive snapshot operations.
     */
    private static final String ARCHIVE_SNAPSHOT  = "Archive Snapshot";

    /**
     * Delete snapshot operations.
     */
    private static final String DELETE_SNAPSHOT   = "Delete Archived Snapshot";

    /**
     * Retrieve snapshot operations.
     */
    private static final String RETRIEVE_SNAPSHOT = "Retrieve Archived Snapshot";

    // ----- data members ---------------------------------------------------

    /**
     * OCI Bucket name to write to.
     */
    private final String f_sBucket;

    /**
     * Prefix to be used for writing.
     */
    private final String f_sPrefix;

    /**
     * {@link ObjectStorageManager} to be used.
     */
    private final ObjectStorageManager f_manager;

    /**
     * The full "directory" prefix.
     */
    private String m_sFulLPrefix;

    /**
     * {@link ExecutorService} to be used to perform operations.
     */
    private ExecutorService m_executor;

    /**
     * {@link ExecutorService} to be used to perform interim piped operations..
     */
    private ExecutorService m_executorWriter;

    // metrics for the MBean.

    private final AtomicLong f_archivedStoresCount              = new AtomicLong();
    private final AtomicLong f_archivedStoresTotalMillis        = new AtomicLong();
    private final AtomicLong f_archivedStoresMaxMillis          = new AtomicLong();
    private final AtomicLong f_archivedStoresLastDurationMillis = new AtomicLong();
    private final AtomicLong f_archivedStoresFailures           = new AtomicLong();

    private final AtomicLong f_retrievedStoresCount              = new AtomicLong();
    private final AtomicLong f_retrievedStoresTotalMillis        = new AtomicLong();
    private final AtomicLong f_retrievedStoresMaxMillis          = new AtomicLong();
    private final AtomicLong f_retrievedStoresLastDurationMillis = new AtomicLong();
    private final AtomicLong f_retrievedStoresFailures           = new AtomicLong();

    private final AtomicLong f_deletedStoresCount              = new AtomicLong();
    private final AtomicLong f_deletedStoresTotalMillis        = new AtomicLong();
    private final AtomicLong f_deletedStoresMaxMillis          = new AtomicLong();
    private final AtomicLong f_deletedStoresLastDurationMillis = new AtomicLong();
    private final AtomicLong f_deletedStoresFailures           = new AtomicLong();
    }
