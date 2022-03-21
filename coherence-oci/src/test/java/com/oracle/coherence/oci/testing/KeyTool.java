/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.testing;

import com.oracle.bedrock.runtime.Application;
import com.oracle.bedrock.runtime.LocalPlatform;
import com.oracle.bedrock.runtime.SimpleApplication;
import com.oracle.bedrock.runtime.options.Arguments;
import com.oracle.bedrock.runtime.options.Console;
import com.oracle.bedrock.runtime.options.Executable;
import com.oracle.bedrock.testsupport.MavenProjectFileUtils;
import com.tangosol.run.xml.XmlElement;
import org.junit.jupiter.api.Assumptions;
import org.opentest4j.TestAbortedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A simple utility to create test keystores.
 */
public class KeyTool
    {
    /**
     * Determine whether keys and certs can be created.
     * <p>
     * Checks for the presence of openssl and the Java keytool cli and if not found
     * throws a JUnit 5 {@link TestAbortedException}.
     */
    public static void assertCanCreateKeys()
        {
        try (Application application = LocalPlatform.get().launch(SimpleApplication.class,
                                                                  Executable.named(OPENSSL),
                                                                  Arguments.of("version"),
                                                                  Console.none()))
            {
            Assumptions.assumeTrue(application.waitFor() == 0, "Could not execute OpenSSL");
            }

        try (Application application = LocalPlatform.get().launch(SimpleApplication.class,
                                                                  Executable.named(KEY_TOOL),
                                                                  Console.none()))
            {
            Assumptions.assumeTrue(application.waitFor() == 0, "Could not execute Java keytool");
            }
        }

    /**
     * Create a CA certificate.
     *
     * @param sName       the name to use for the cert, which must also be valid for use as a cert CN
     * @param sStoreType  they type of the keystore, e,g, JKS, or PKCS12
     *
     * @return a {@link KeyAndCert} holder containing details of the generated files
     *
     * @throws IOException if cert generation fails
     */
    public static KeyAndCert createCACert(String sName, String sStoreType) throws IOException
        {
        File   fileBuild    = MavenProjectFileUtils.locateBuildFolder(KeyTool.class);
        File   fileClasses  = new File(fileBuild, "test-classes");
        File   fileCerts    = new File(fileClasses, "certs");
        File   fileKey      = new File(fileCerts, sName + "-ca.key");
        File   fileCert     = new File(fileCerts, sName + "-ca.cert");
        File   fileKeystore = new File(fileCerts, sName + "-ca.jks");
        String sKeyPass    = "s3cr37";
        String sStorePass  = "s3cr37";

        if (fileCerts.exists())
            {
            Files.deleteIfExists(fileKey.toPath());
            Files.deleteIfExists(fileCert.toPath());
            Files.deleteIfExists(fileKeystore.toPath());
            }
        else
            {
            assertThat(fileCerts.mkdirs(), is(true));
            }

        runOpenSSL(Arguments.of("genrsa", "-passout", "pass:" + sKeyPass,
                                "-aes256", "-out", fileKey.getAbsolutePath(),  "4096"));

        runOpenSSL(Arguments.of("req", "-passin", "pass:" + sKeyPass, "-new", "-x509",
                                "-days", "3650", "-subj", "/CN=" + sName,
                                "-key", fileKey.getAbsolutePath(),
                                "-out", fileCert.getAbsolutePath()));

        runKeytool(Arguments.of("-import", "-storepass", sStorePass, "-noprompt", "-trustcacerts",
            "-alias", sName, "-file", fileCert.getAbsolutePath(),
            "-keystore", fileKeystore.getAbsolutePath(),
            "-deststoretype", sStoreType));

        return new KeyAndCert(fileKey, null, null, fileCert, sKeyPass, null, fileKeystore, sStorePass);
        }

    /**
     * Create a private key and certificate pair in different formats.
     *
     * @param keyAndCertCA  the previously created CA cert to use to sign the key and cert
     * @param sName         the name to use for the key and cert, which must also be valid for use as a cert CN
     *
     * @return a {@link KeyAndCert} holder containing details of the generated files
     *
     * @throws IOException if cert generation fails
     */
    public static KeyAndCert createKeyCertPair(KeyAndCert keyAndCertCA, String sName) throws IOException
        {
        File  fileBuild     = MavenProjectFileUtils.locateBuildFolder(KeyTool.class);
        File  fileClasses   = new File(fileBuild, "test-classes");
        File  fileCerts     = new File(fileClasses, "certs");
        File  fileSign      = new File(fileCerts, sName + "-signing.key");
        File  fileKey       = new File(fileCerts, sName + ".key");
        File  filePEM       = new File(fileCerts, sName + ".pem");
        File  filePEMNoPwd  = new File(fileCerts, sName + "-nopass.pem");
        File  fileCert      = new File(fileCerts, sName + ".cert");
        File  fileP12       = new File(fileCerts, sName + ".p12");
        File  fileKeystore  = new File(fileCerts, sName + ".jks");
        String sKeyPass     = "pa55w0rd";
        String sStorePass   = "s3cr37";

        if (fileCerts.exists())
            {
            Files.deleteIfExists(fileSign.toPath());
            Files.deleteIfExists(fileKey.toPath());
            Files.deleteIfExists(filePEM.toPath());
            Files.deleteIfExists(filePEMNoPwd.toPath());
            Files.deleteIfExists(fileCert.toPath());
            Files.deleteIfExists(fileP12.toPath());
            Files.deleteIfExists(fileKeystore.toPath());
            }
        else
            {
            assertThat(fileCerts.mkdirs(), is(true));
            }

        // Generate key:
        runOpenSSL(Arguments.of("genrsa", "-passout", "pass:" + sKeyPass, "-aes256",
            "-out", fileKey.getAbsolutePath(), "4096"));

        // Generate signing request:
        runOpenSSL(Arguments.of("req", "-passin", "pass:" + sKeyPass, "-new", "-subj", "/CN=" + sName,
                                "-key", fileKey.getAbsolutePath(),
                                "-out", fileSign.getAbsolutePath()));

        // Generate Self-signed certificate:
        runOpenSSL(Arguments.of("x509", "-req", "-passin", "pass:" + keyAndCertCA.m_sKeyPass, "-days", "3650",
                    "-in", fileSign.getAbsolutePath(),
                    "-CA", keyAndCertCA.m_fileCert.getAbsolutePath(),
                    "-CAkey", keyAndCertCA.m_fileKey.getAbsolutePath(),
                    "-set_serial", "01",
                    "-out", fileCert.getAbsolutePath()));

        // convert the key to PEM format
        toPem(fileKey, filePEM, sKeyPass);

        // un-encrypt the key (remove its credentials):
        runOpenSSL(Arguments.of("rsa", "-passin", "pass:" + sKeyPass,
                "-in", fileKey.getAbsolutePath(),
                "-out", fileKey.getAbsolutePath()));

        // convert the plain key to PEM format
        toPem(fileKey, filePEMNoPwd, null);

        // Create PKCS12 keystore
        runOpenSSL(Arguments.of("pkcs12", "-export", "-passout", "pass:" + sStorePass, "-name", sName,
                "-inkey", filePEMNoPwd.getAbsolutePath(),
                "-in", fileCert.getAbsolutePath(),
                "-out", fileP12.getAbsolutePath()));

        // Create Java keystore
        runKeytool(Arguments.of("-importkeystore", "-storepass", sStorePass, "-noprompt",
                "-srcstoretype", "jks", "-destkeypass", sStorePass,
                "-srcstorepass", sStorePass,
                "-srckeystore", fileP12.getAbsolutePath(),
                "-destkeystore", fileKeystore.getAbsolutePath()));

        return new KeyAndCert(fileKey, filePEM, filePEMNoPwd, fileCert, sKeyPass, fileP12, fileKeystore, sStorePass);
        }

    // ----- helper methods -------------------------------------------------

    private static void toPem(File fileKey, File filePEM, String sKeyPass)
        {
        Arguments arguments = Arguments.of("pkcs8", "-topk8", "-outform", "pem",
                                           "-in", fileKey.getAbsolutePath(),
                                           "-out", filePEM.getAbsolutePath());

        if (sKeyPass != null)
            {
            arguments = arguments.with("-passin", "pass:" + sKeyPass, "-passout", "pass:" + sKeyPass);
            }
        else
            {
            arguments = arguments.with("-nocrypt");
            }

        runOpenSSL(arguments);
        }

    private static void runKeytool(Arguments arguments)
        {
        try (Application application = LocalPlatform.get().launch(SimpleApplication.class,
                                                                  Executable.named(KEY_TOOL),
                                                                  arguments,
                                                                  Console.system()))
            {
            assertThat(application.waitFor(), is(0));
            }
        }

    private static void runOpenSSL(Arguments arguments)
        {
        try (Application application = LocalPlatform.get().launch(SimpleApplication.class,
                                                                  Executable.named(OPENSSL),
                                                                  arguments,
                                                                  Console.system()))
            {
            assertThat(application.waitFor(), is(0));
            }
        }

    // ----- inner class: KeyAndCert ----------------------------------------

    /**
     * A holder for the files generated by {@link KeyTool}.
     */
    public static class KeyAndCert
        {
        /**
         * Create a {@link KeyAndCert} holder.
         *
         * @param fileKey           the generated key file
         * @param fileKeyPEM        the generated encrypted key in PEM format
         * @param fileKeyPEMNoPass  the generated key (unencrypted) in PEM format
         * @param fileCert          the generated certificate
         * @param sKeyPass          the credential for the encrypted key
         * @param fileP12           the PKCS12 keystore containing the key and cert
         * @param fileKeystore      the JKS keystore containing the key and cert
         * @param sStorePass        the credential for the keystore
         */
        public KeyAndCert(File fileKey, File fileKeyPEM, File fileKeyPEMNoPass, File fileCert, String sKeyPass,
                          File fileP12, File fileKeystore, String sStorePass)
            {
            m_fileKey          = fileKey;
            m_fileKeyPEM       = fileKeyPEM;
            m_fileKeyPEMNoPass = fileKeyPEMNoPass;
            m_fileCert         = fileCert;
            m_sKeyPass         = sKeyPass;
            m_fileKeystore     = fileKeystore;
            m_fileP12          = fileP12;
            m_sStorePass       = sStorePass;
            }

        /**
         * Returns the credentials to open the generated keystore.
         *
         * @return the credentials to open the generated keystore
         */
        public char[] storePassword()
            {
            return m_sStorePass == null ? null : m_sStorePass.toCharArray();
            }

        /**
         * Returns the credentials to open the generated keystore.
         *
         * @return the credentials to open the generated keystore
         */
        public String storePasswordString()
            {
            return m_sStorePass;
            }

        /**
         * Returns the credentials to open the encrypted private key.
         *
         * @return the credentials to open the generated encrypted private key
         */
        public char[] keyPassword()
            {
            return m_sKeyPass == null ? null : m_sKeyPass.toCharArray();
            }

        /**
         * Returns the credentials to open the encrypted private key.
         *
         * @return the credentials to open the generated encrypted private key
         */
        public String keyPasswordString()
            {
            return m_sKeyPass;
            }

        /**
         * Add an XML instance configuration to the parent {@link XmlElement}
         * that will create an instance of a {@link BasicAuthenticationStub}
         * using the key PEM file from this {@link KeyAndCert}.
         */
        public void addInstance(XmlElement xmlParent)
            {
            XmlElement xmlInstance = xmlParent.addElement("instance");

            xmlInstance.addElement("class-name").setString(BasicAuthenticationStub.class.getName());

            XmlElement  xmlParams    = xmlInstance.addElement("init-params");
            XmlElement  xmlParamPEM  = xmlParams.addElement("init-param");
            XmlElement  xmlParamPass = xmlParams.addElement("init-param");

            xmlParamPEM.addElement("param-type").setString("String");
            xmlParamPEM.addElement("param-value").setString(m_fileKeyPEM.getAbsolutePath());
            xmlParamPass.addElement("param-type").setString("String");
            xmlParamPass.addElement("param-value").setString(m_sKeyPass);
            }

        public BasicAuthenticationStub createBasicAuthenticationStub()
            {
            return new BasicAuthenticationStub("test", m_fileKeyPEM, m_sKeyPass);
            }

        // ----- data members ---------------------------------------------------

        /**
         * The generated private key file.
         */
        public final File m_fileKey;

        /**
         * The generated private key file (in encrypted PEM format).
         */
        public final File m_fileKeyPEM;

        /**
         * The generated private key file (in unencrypted PEM format).
         */
        public final File m_fileKeyPEMNoPass;

        /**
         * The generated certificate file.
         */
        public final File m_fileCert;

        /**
         * The encrypted private key credential.
         */
        public final String m_sKeyPass;

        /**
         * The JKS format keystore containing the key and cert.
         */
        public final File m_fileKeystore;

        /**
         * The PKCS12 format keystore containing the key and cert.
         */
        public final File m_fileP12;

        /**
         * The credentials to open the keystores.
         */
        public final String m_sStorePass;
        }

    // ----- constants ---------------------------------------------------------------

    /**
     * The path to use to run openssl.
     */
    private static final String OPENSSL = System.getProperty("test.openssl.path", "openssl");

    /**
     * The path to use to run the Java keytool.
     */
    private static final String KEY_TOOL = System.getProperty("java.home") + File.separator + "bin" + File.separator + "keytool";
    }
