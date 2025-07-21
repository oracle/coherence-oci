/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import com.oracle.bedrock.junit.CoherenceClusterExtension;
import com.oracle.bedrock.runtime.coherence.CoherenceClusterMember;
import com.oracle.bedrock.runtime.coherence.options.*;
import com.oracle.bedrock.runtime.java.options.IPv4Preferred;
import com.oracle.bedrock.runtime.options.DisplayName;
import com.oracle.bedrock.testsupport.junit.TestLogsExtension;
import com.tangosol.net.Coherence;
import com.tangosol.net.NamedMap;
import com.tangosol.net.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class OCIArchiverIT
    {

    @BeforeAll
    static void setup() throws Exception
        {
        System.setProperty("coherence.cluster", CLUSTER_NAME);
        System.setProperty("coherence.wka", "127.0.0.1");
        System.setProperty("coherence.role", "client");

        coherence = Coherence.client().startAndWait();
        session   = coherence.getSession();
        }

    // ---- tests --

    @Test
    void shouldRunBasicTest()
        {
        NamedMap<Integer, String> cache = session.getCache("test");
        cache.put(1, "test");
        System.out.println(cache.get(1));
        }

    // ---- data members ----------------------------------------------------

    static Coherence coherence;

    static Session session;

    // ---- constants -------------------------------------------------------

    /**
     * The test cluster name.
     */
    public static final String CLUSTER_NAME = "oci-archiver-test";

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
                    LocalHost.only())
            .include(3, CoherenceClusterMember.class,
                    LocalStorage.enabled(),
                    testLogs,
                    RoleName.of("storage"),
                    DisplayName.of("storage"));
    }
