/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret;

import com.oracle.coherence.oci.AbstractKeysAndCertsTest;

import com.oracle.coherence.oci.secret.testing.SecretsClientStub;

import com.oracle.coherence.oci.secret.util.SecretsFetcherTest;

import com.oracle.coherence.oci.testing.BasicAuthenticationStub;

import org.junit.jupiter.api.BeforeAll;

/**
 * A base class for testing OCI secrets service integration.
 */
public class AbstractSecretsTest
        extends AbstractKeysAndCertsTest
    {
    @BeforeAll
    static void setup()
        {
        System.setProperty("test.oci.secrets.client", ClientStub.class.getName());
        }

    // ----- inner class: ClientStub ----------------------------------------

    /**
     * A stub of the OCI secrets client.
     */
    public static class ClientStub
            extends SecretsClientStub
        {
        public ClientStub()
            {
            super(s_clientKeyCertPair.m_fileKeyPEMNoPass);
            addSecret("client-pem-file",            s_clientKeyCertPair.m_fileKeyPEMNoPass);
            addSecret("client-encoded-key-file",    s_clientKeyCertPair.m_fileKeyPEM);
            addSecret("client-key-password",        s_clientKeyCertPair.keyPasswordString());
            addSecret("client-cert-file",           s_clientKeyCertPair.m_fileCert);
            addSecret("client-keystore-file",       s_clientKeyCertPair.m_fileKeystore);
            addSecret("client-keystore-password",   s_clientKeyCertPair.storePasswordString());
            addSecret("client-ca-cert",             s_clientCaCert.m_fileCert);
            addSecret("client-truststore-file",     s_clientCaCert.m_fileKeystore);
            addSecret("client-truststore-password", s_clientCaCert.storePasswordString());
            addSecret("server-pem-file",            s_serverKeyCertPair.m_fileKeyPEMNoPass);
            addSecret("server-encoded-key-file",    s_serverKeyCertPair.m_fileKeyPEM);
            addSecret("server-key-password",        s_serverKeyCertPair.keyPasswordString());
            addSecret("server-cert-file",           s_serverKeyCertPair.m_fileCert);
            addSecret("server-keystore-file",       s_serverKeyCertPair.m_fileKeystore);
            addSecret("server-keystore-password",   s_serverKeyCertPair.storePasswordString());
            addSecret("server-ca-cert",             s_serverCaCert.m_fileCert);
            addSecret("server-truststore-file",     s_serverCaCert.m_fileKeystore);
            addSecret("server-truststore-password", s_serverCaCert.storePasswordString());
            }
        }
    }
