/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret;

import com.oracle.bedrock.OptionsByType;
import com.oracle.bedrock.runtime.LocalPlatform;
import com.oracle.bedrock.runtime.coherence.CoherenceCluster;
import com.oracle.bedrock.runtime.coherence.CoherenceClusterBuilder;
import com.oracle.bedrock.runtime.coherence.CoherenceClusterMember;
import com.oracle.bedrock.runtime.coherence.callables.IsServiceRunning;
import com.oracle.bedrock.runtime.coherence.options.CacheConfig;
import com.oracle.bedrock.runtime.coherence.options.ClusterName;
import com.oracle.bedrock.runtime.coherence.options.LocalHost;
import com.oracle.bedrock.runtime.coherence.options.Logging;
import com.oracle.bedrock.runtime.coherence.options.OperationalOverride;
import com.oracle.bedrock.runtime.coherence.options.WellKnownAddress;
import com.oracle.bedrock.runtime.java.JavaApplication;
import com.oracle.bedrock.runtime.java.options.ClassName;
import com.oracle.bedrock.runtime.java.options.SystemProperty;
import com.oracle.bedrock.runtime.options.DisplayName;
import com.oracle.bedrock.runtime.options.StabilityPredicate;
import com.oracle.bedrock.testsupport.MavenProjectFileUtils;
import com.oracle.bedrock.testsupport.deferred.Eventually;
import com.oracle.bedrock.testsupport.junit.TestLogsExtension;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.OCID;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.coherence.oci.CoherenceOCI;
import com.oracle.coherence.oci.config.AuthenticationBuilder;
import com.oracle.coherence.oci.secret.testing.CoherenceClient;
import com.oracle.coherence.oci.secret.testing.SecretsUtils;
import com.oracle.coherence.oci.testing.KeyTool;
import com.tangosol.net.Coherence;
import com.tangosol.net.NamedCache;
import com.tangosol.net.Session;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * An OCI Secret Service integration test.
 * <p>
 * This test actually uses the OCI Secret Service, so needs certain prerequisites
 * that are checked in the _setup() method.
 * <p>
 * The following System properties should be set:
 * <pre>
 * test.oci.region      the name of the OCI region
 * test.oci.compartment the OCID of the OCI compartment
 * test.oci.vault       the OCID of the OCI vault
 * test.oci.key         the OCID of the OCI encryption key
 * </pre>
 * Optionally set the following system properties:
 * <pre>
 * test.oci.config   the name of the OCI config file to use (default to ~/.oci/config)
 * test.oci.profile  the name of the profile within the OCI config file to use (defaults to DEFAULT)
 * </pre>
 *
 * @author Jonathan Knight  2022.03.17
 */
public class SecretsIT
    {
    @BeforeAll
    static void _setup() throws Exception
        {
        s_sCompartmentId = System.getProperty("test.oci.compartment");
        s_sRegion        = System.getProperty("test.oci.region");
        s_sVaultId       = System.getProperty("test.oci.vault");
        s_sKeyId         = System.getProperty("test.oci.key");

        Assumptions.assumeTrue(s_sRegion != null && !s_sRegion.isEmpty(),
                               "System property test.oci.region is not set");

        Assumptions.assumeTrue(() ->
            {
            try
                {
                Region.valueOf(s_sRegion);
                return true;
                }
            catch (Exception e)
                {
                return false;
                }
            }, "The test.oci.region property '" + s_sRegion + "' is not a valid OCI region");

        Assumptions.assumeTrue(OCID.isValid(s_sCompartmentId), "The test.oci.compartment property is not a valid Compartment OCID");
        Assumptions.assumeTrue(OCID.isValid(s_sVaultId), "The test.oci.vault property is not a valid Vault OCID");
        Assumptions.assumeTrue(OCID.isValid(s_sKeyId), "The test.oci.key property is not a valid Key OCID");

        ensureKeys();

        String                      sConfigFile = System.getProperty("test.oci.config", "~/.oci/config");
        String                      sProfile    = System.getProperty("test.oci.profile", "DEFAULT");
        ConfigFileReader.ConfigFile configFile  = ConfigFileReader.parse(sConfigFile, sProfile);

        s_secretUtils = new SecretsUtils(new ConfigFileAuthenticationDetailsProvider(configFile), s_sRegion);

        s_commonOptions = OptionsByType.empty();
        loadSecrets(s_secretUtils, s_commonOptions);
        }

    @Test
    public void shouldStartSecureCluster()
        {
        String sConfigFile = System.getProperty("test.oci.config", "~/.oci/config");
        String sProfile    = System.getProperty("test.oci.profile", "DEFAULT");

        OptionsByType options = OptionsByType.of(s_commonOptions.asArray())
                .addAll(ClusterName.of("OCI"),
                        LocalHost.only(),
                        SystemProperty.of(CoherenceOCI.PROP_OCI_CONFIG_FILE, sConfigFile),
                        SystemProperty.of(CoherenceOCI.PROP_OCI_CONFIG_PROFILE, sProfile),
                        SystemProperty.of("coherence.socketprovider", "oci-server"),
                        SystemProperty.of("coherence.extend.socketprovider", "oci-server"),
                        SystemProperty.of("coherence.extend.client.socketprovider", "oci-client"),
                        SystemProperty.of("javax.net.debug", "all"),
                        OperationalOverride.of("secure-coherence-override.xml"),
                        CacheConfig.of("secure-cache-config.xml"),
                        WellKnownAddress.of("127.0.0.1"),
                        Logging.at(9),
                        StabilityPredicate.none(),
                        s_testLogs.builder());

        OptionsByType serverOptions = OptionsByType.of(options.asArray())
                .addAll(DisplayName.of("storage"));

        OptionsByType clientOptions = OptionsByType.of(options.asArray())
                .addAll(DisplayName.of("client"),
                        ClassName.of(CoherenceClient.class),
                        SystemProperty.of("coherence.client", "remote"));

        CoherenceClusterBuilder builder = new CoherenceClusterBuilder();

        builder.include(2, CoherenceClusterMember.class, serverOptions.asArray());

        LocalPlatform platform = LocalPlatform.get();

        try (CoherenceCluster cluster = builder.build(platform))
            {
            Eventually.assertDeferred(cluster::getClusterSize, is(2));

            for (CoherenceClusterMember member : cluster)
                {
                Eventually.assertDeferred(() -> member.invoke(new IsServiceRunning("Proxy")), is(true));
                }

            try (JavaApplication client = platform.launch(JavaApplication.class, clientOptions.asArray()))
                {
                Eventually.assertDeferred(() -> client.invoke(CoherenceClient.IS_RUNNING), is(true));

                Boolean fResult = client.invoke(() ->
                    {
                    Coherence coherence = CoherenceClient.getCoherence();
                    Session   session   = coherence.getSession();
                    NamedCache<String, String> cache = session.getCache("test");
                    cache.put("key-1", "value-1");
                    return true;
                    });

                assertThat(fResult, is(true));
                }
            }
        }


    // ----- helper methods -------------------------------------------------

    protected static void ensureKeys() throws Exception
        {
        KeyTool.assertCanCreateKeys();

        File fileBuild = MavenProjectFileUtils.locateBuildFolder(SecretsIT.class);

        s_clientCaCert      = KeyTool.createCACert(fileBuild, "client", "JKS");
        s_clientKeyCertPair = KeyTool.createKeyCertPair(fileBuild, s_clientCaCert, "client");
        s_serverCaCert      = KeyTool.createCACert(fileBuild, "server", "JKS");
        s_serverKeyCertPair = KeyTool.createKeyCertPair(fileBuild, s_serverCaCert, "server");
        }

    protected static void loadSecrets(SecretsUtils secretsUtils, OptionsByType options)
        {
        loadSecret(secretsUtils, SECRET_CLIENT_CA, PROP_CLIENT_CA_CERT, s_clientCaCert.m_fileCert, options);
        loadSecret(secretsUtils, SECRET_CLIENT_KEY, PROP_CLIENT_KEY, s_clientKeyCertPair.m_fileKeyPEMNoPass, options);
        loadSecret(secretsUtils, SECRET_CLIENT_CERT, PROP_CLIENT_CERT, s_clientKeyCertPair.m_fileCert, options);
        loadSecret(secretsUtils, SECRET_CLIENT_ENCRYPTED_PEM, PROP_CLIENT_ENCRYPTED_KEY, s_clientKeyCertPair.m_fileKeyPEM, options);
        loadSecret(secretsUtils, SECRET_CLIENT_PASS, PROP_CLIENT_PASS, s_clientKeyCertPair.m_sKeyPass.getBytes(StandardCharsets.UTF_8), options);

        loadSecret(secretsUtils, SECRET_SERVER_CA, PROP_SERVER_CA_CERT, s_serverCaCert.m_fileCert, options);
        loadSecret(secretsUtils, SECRET_SERVER_KEY, PROP_SERVER_KEY, s_serverKeyCertPair.m_fileKeyPEMNoPass, options);
        loadSecret(secretsUtils, SECRET_SERVER_CERT, PROP_SERVER_CERT, s_serverKeyCertPair.m_fileCert, options);
        loadSecret(secretsUtils, SECRET_SERVER_ENCRYPTED_PEM, PROP_SERVER_ENCRYPTED_KEY, s_serverKeyCertPair.m_fileKeyPEM, options);
        loadSecret(secretsUtils, SECRET_SERVER_PASS, PROP_SERVER_PASS, s_serverKeyCertPair.m_sKeyPass.getBytes(StandardCharsets.UTF_8), options);
        }

    protected static void loadSecret(SecretsUtils secretsUtils, String sSecretName, String sOCIDProperty, File file, OptionsByType options)
        {
        String sOCID = secretsUtils.ensureSecret(s_sCompartmentId, s_sVaultId, s_sKeyId, sSecretName, file);
        System.setProperty(sOCIDProperty, sOCID);
        options.add(SystemProperty.of(sOCIDProperty, sOCID));
        }

    protected static void loadSecret(SecretsUtils secretsUtils, String sSecretName, String sOCIDProperty, byte[] abData, OptionsByType options)
        {
        String sOCID = secretsUtils.ensureSecret(s_sCompartmentId, s_sVaultId, s_sKeyId, sSecretName, abData);
        System.setProperty(sOCIDProperty, sOCID);
        options.add(SystemProperty.of(sOCIDProperty, sOCID));
        }


    // ----- constants ------------------------------------------------------

    public static final String SECRET_CLIENT_CA = "client-ca.cert";
    public static final String SECRET_CLIENT_KEY = "client.pem";
    public static final String SECRET_CLIENT_CERT = "client.cert";
    public static final String SECRET_CLIENT_ENCRYPTED_PEM = "client-encrypted.pem";
    public static final String SECRET_CLIENT_PASS = "client-pass.txt";

    public static final String SECRET_SERVER_CA = "server-ca.cert";
    public static final String SECRET_SERVER_KEY = "server.pem";
    public static final String SECRET_SERVER_CERT = "server.cert";
    public static final String SECRET_SERVER_ENCRYPTED_PEM = "server-encrypted.pem";
    public static final String SECRET_SERVER_PASS = "server-pass.txt";

    public static final String PROP_CLIENT_CA_CERT = "test.client.ca.ocid";
    public static final String PROP_CLIENT_CERT = "test.client.cert.ocid";
    public static final String PROP_CLIENT_KEY = "test.client.key.ocid";
    public static final String PROP_CLIENT_ENCRYPTED_KEY = "test.client.encrypted.key.ocid";
    public static final String PROP_CLIENT_PASS = "test.client.pass.ocid";

    public static final String PROP_SERVER_CA_CERT = "test.server.ca.ocid";
    public static final String PROP_SERVER_CERT = "test.server.cert.ocid";
    public static final String PROP_SERVER_KEY = "test.server.key.ocid";
    public static final String PROP_SERVER_ENCRYPTED_KEY = "test.server.encrypted.key.ocid";
    public static final String PROP_SERVER_PASS = "test.server.pass.ocid";

    // ----- data members ---------------------------------------------------

    /**
     * The OCI compartment OCID.
     */
    protected static String s_sCompartmentId;

    /**
     * The OCI region.
     */
    protected static String s_sRegion;

    /**
     * The OCI Vault OCID.
     */
    protected static String s_sVaultId;

    /**
     * The OCI encryption key OCID.
     */
    protected static String s_sKeyId;

    /**
     * The secret handling utilities.
     */
    protected static SecretsUtils s_secretUtils;

    /**
     * The test client key and cert.
     */
    protected static KeyTool.KeyAndCert s_clientKeyCertPair;

    /**
     * The test client CA cert.
     */
    protected static KeyTool.KeyAndCert s_clientCaCert;

    /**
     * The test server key and cert.
     */
    protected static KeyTool.KeyAndCert s_serverKeyCertPair;

    /**
     * The test server CA cert.
     */
    protected static KeyTool.KeyAndCert s_serverCaCert;

    /**
     * Common options for running Coherence.
     */
    protected static OptionsByType s_commonOptions;

    @RegisterExtension
    protected static TestLogsExtension s_testLogs = new TestLogsExtension();
    }
