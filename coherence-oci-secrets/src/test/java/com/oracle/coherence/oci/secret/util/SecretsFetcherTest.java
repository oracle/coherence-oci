/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.util;

import com.oracle.bmc.vault.VaultsClient;
import com.oracle.coherence.oci.secret.AbstractSecretsTest;

import com.oracle.coherence.oci.secret.testing.SecretsClientStub;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class SecretsFetcherTest
        extends AbstractSecretsTest
    {
    @Test
    public void shouldFetchSecret()
        {
        SecretsClientStub client = new SecretsClientStub(s_clientKeyCertPair.m_fileKeyPEMNoPass);
        client.addSecret("foo", "foo-secret");

        SecretsFetcher fetcher = new SecretsFetcher(client, mock(VaultsClient.class));
        byte[]         abData  = fetcher.get("foo");

        assertThat(abData, is("foo-secret".getBytes(StandardCharsets.UTF_8)));
        }

    @Test
    public void shouldFailToFetchSecret()
        {
        SecretsClientStub client  = new SecretsClientStub(s_clientKeyCertPair.m_fileKeyPEMNoPass);
        SecretsFetcher    fetcher = new SecretsFetcher(client, mock(VaultsClient.class));

        assertThrows(IllegalArgumentException.class, () -> fetcher.get("foo"));
        }
    }
