/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret;

import com.oracle.bedrock.testsupport.MavenProjectFileUtils;
import com.oracle.coherence.oci.testing.KeyTool;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;

/**
 * A base class for tests needing self-signed keys and certs
 *
 * @author Jonathan Knight  2022.01.25
 */
public abstract class AbstractKeysAndCertsTest
    {
    @BeforeAll
    static void _createKeysAndCerts() throws IOException
        {
        KeyTool.assertCanCreateKeys();

        File fileBuild = MavenProjectFileUtils.locateBuildFolder(AbstractKeysAndCertsTest.class);

        s_clientCaCert      = KeyTool.createCACert(fileBuild,"client", "JKS");
        s_clientKeyCertPair = KeyTool.createKeyCertPair(fileBuild, s_clientCaCert, "client");
        s_serverCaCert      = KeyTool.createCACert(fileBuild, "server", "JKS");
        s_serverKeyCertPair = KeyTool.createKeyCertPair(fileBuild, s_serverCaCert, "server");
        }

    // ----- data members ---------------------------------------------------

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
    }
