/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import com.oracle.bedrock.junit.CoherenceClusterExtension;
import com.oracle.bedrock.runtime.coherence.CoherenceClusterMember;
import com.oracle.bedrock.runtime.coherence.JMXManagementMode;
import com.oracle.bedrock.runtime.coherence.options.*;
import com.oracle.bedrock.runtime.java.options.IPv4Preferred;
import com.oracle.bedrock.runtime.java.options.SystemProperty;
import com.oracle.bedrock.runtime.java.profiles.JmxProfile;
import com.oracle.bedrock.runtime.options.DisplayName;
import com.oracle.bedrock.testsupport.junit.TestLogsExtension;
import com.tangosol.io.FileHelper;
import com.tangosol.net.Coherence;
import com.tangosol.net.NamedMap;
import com.tangosol.net.Session;
import com.tangosol.util.Base;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.management.JMX;
import javax.management.MBeanException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@EnabledIfSystemProperty(named = "runOCIArchiverIT", matches = "true")
public class OCIArchiverIT
    {

    // ---- test methods ----------------------------------------------------

    @BeforeAll
    static void setup() throws Exception
        {
        System.setProperty("coherence.cluster", CLUSTER_NAME);
        System.setProperty("coherence.wka", "127.0.0.1");
        System.setProperty("coherence.role", "client");
        System.setProperty("coherence.cacheconfig", CACHE_CONIFG);
        System.setProperty("coherence.override", OVERRIDE_CONIFG);
        System.setProperty("coherence.management", "all");
        System.setProperty("coherence.distributed.localstorage", "false");
        System.setProperty("coherence.distributed.persistence.base.dir", s_persistenceBase.getAbsolutePath());

        s_coherence = Coherence.client().startAndWait();
        s_session = s_coherence.getSession();
        }

    @AfterAll
    static void teardown()
        {
        FileHelper.deleteDirSilent(s_persistenceBase);
        }

    // ---- tests -----------------------------------------------------------

    @Test
    void shouldRunBasicTest()
        {
        final int MAX = 10_000;
        NamedMap<Integer, String> cache = s_session.getCache("test");
        Map<Integer, String> buffer = new HashMap<>();

        for (int i = 0; i < MAX; i++)
            {
            buffer.put(i, String.valueOf(i));
            if (i % 1000 == 0)
                {
                cache.putAll(buffer);
                buffer.clear();
                }
            }

        if (!buffer.isEmpty())
            {
            cache.putAll(buffer);
            }

        assertThat(cache.size(), is(MAX));
        PersistenceTestHelper helper = new PersistenceTestHelper();

        Base.sleep(10_000L);

        try
            {
            // cleanup any archived snapshot
            removeArchivedSnapshot(helper);

            String []asSnapshots = helper.listSnapshots(SERVICE_NAME);
            assertThat(asSnapshots.length, is(0));

            // create snapshot
            helper.createSnapshot(SERVICE_NAME, SNAPSHOT_NAME);
            cache.clear();

            asSnapshots = helper.listSnapshots(SERVICE_NAME);
            assertThat(asSnapshots.length, is(1));
            assertThat(asSnapshots[0], is(SNAPSHOT_NAME));

            // archive snapshot
            helper.archiveSnapshot(SERVICE_NAME, SNAPSHOT_NAME);

            // remove local snapshot
            helper.removeSnapshot(SERVICE_NAME, SNAPSHOT_NAME);

            asSnapshots = helper.listSnapshots(SERVICE_NAME);
            assertThat(asSnapshots.length, is(0));

            // retrieve archived snapshot
            helper.retrieveArchivedSnapshot(SERVICE_NAME, SNAPSHOT_NAME);

            // recover snapshot
            asSnapshots = helper.listSnapshots(SERVICE_NAME);
            assertThat(asSnapshots.length, is(1));
            assertThat(asSnapshots[0], is(SNAPSHOT_NAME));

            helper.recoverSnapshot(SERVICE_NAME, SNAPSHOT_NAME);

            assertThat(cache.size(), is(MAX));
            }
        catch (Exception e)
           {
           fail(e.getMessage());
           }
        finally
            {
            removeArchivedSnapshot(helper);
            }
        }

    private void removeArchivedSnapshot(PersistenceTestHelper helper)
        {
        try
            {
            helper.removeArchivedSnapshot(SERVICE_NAME, SNAPSHOT_NAME);
            }
        catch (Exception ex)
            {
            // ignore
            }
        }

    // ---- constants -------------------------------------------------------

    /**
     * Coherence instance.
     */
    static Coherence s_coherence;

    /**
     * Coherence session.
     */
    static Session s_session;

    /**
     * Persistence base directory.
     */
    static File s_persistenceBase;

    /**
     * Prefix for the archiver.
     */
    static String s_archiverPrefix;

    static {
        try
            {
            s_persistenceBase = FileHelper.createTempDir();
            s_archiverPrefix  = "test" + System.currentTimeMillis();
            }
        catch (IOException e)
            {
            throw new RuntimeException(e);
            }
        }

    /**
     * The test cluster name.
     */
    public static final String CLUSTER_NAME = "oci-archiver-test";

    /**
     * Cache configuration.
     */
    public static final String CACHE_CONIFG = "oci-archiver-cache-config.xml";

    /**
     * Override configuration.
     */
    public static final String OVERRIDE_CONIFG = "oci-archiver-override.xml";

    private static final String SERVICE_NAME = "PartitionedCache";
    private static final String SNAPSHOT_NAME = "test-snapshot";


        /**
     * A JUnit 5 extension to capture cluster member logs.
     */
    @RegisterExtension
    static final TestLogsExtension testLogs = new TestLogsExtension(OCIArchiverIT.class);

    /**
     * A JUnit 5 extension that runs a Coherence cluster.
     */
    @RegisterExtension
    static final CoherenceClusterExtension clusterRunner = new CoherenceClusterExtension()
            .with(WellKnownAddress.of("127.0.0.1"),
                    ClusterName.of(CLUSTER_NAME),
                    IPv4Preferred.autoDetect(),
                    LocalHost.only(),
                    CacheConfig.of(CACHE_CONIFG),
                    OperationalOverride.of(OVERRIDE_CONIFG))
            .include(3, CoherenceClusterMember.class,
                    LocalStorage.enabled(),
                    JmxProfile.enabled(),
                    testLogs,
                    JMXManagementMode.ALL,
                    RoleName.of("storage"),
                    DisplayName.of("storage"),
                    SystemProperty.of("coherence.distributed.persistence.base.dir", s_persistenceBase.getAbsolutePath()),
                    SystemProperty.of("oci.archive.prefix", s_archiverPrefix),
                    SystemProperty.of("test.archiver.class", ObjectStorageSnapshotArchiver.class.getCanonicalName()));
    }
