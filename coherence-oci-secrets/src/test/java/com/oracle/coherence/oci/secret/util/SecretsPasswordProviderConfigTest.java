/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.util;

import com.oracle.coherence.oci.secret.AbstractSecretsTest;
import com.oracle.coherence.oci.secret.config.SecretsPasswordProviderBuilder;
import com.oracle.coherence.oci.testing.BasicAuthenticationStub;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilderRegistry;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.OperationalContext;
import com.tangosol.net.PasswordProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SecretsPasswordProviderConfigTest
        extends AbstractSecretsTest
    {
    @BeforeEach
    void setup()
        {
        System.setProperty(BasicAuthenticationStub.PROP_PEM_FILE, s_serverKeyCertPair.m_fileKeyPEMNoPass.getAbsolutePath());
        }

    @AfterEach
    void cleanup()
        {
        CacheFactory.shutdown();
        }

    @Test
    public void shouldConfigureProvider() throws Exception
        {
        ParameterizedBuilder<?> builder = getParameterizedBuilder("test-pass");
        assertThat(builder, is(instanceOf(SecretsPasswordProviderBuilder.class)));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId(), is("foo"));

        SecretsPasswordProvider provider = (SecretsPasswordProvider) builder.realize(null, null, null);
        assertThat(provider, is(notNullValue()));
        assertThat(provider.getSecretId(), is("foo"));
        }

    private ParameterizedBuilder<?> getParameterizedBuilder(String sResourceName)
        {
        CacheFactory.shutdown();
        System.setProperty("coherence.override", "test-override.xml");

        Cluster cluster  = CacheFactory.getCluster();
        ParameterizedBuilderRegistry registry = ((OperationalContext) cluster).getBuilderRegistry();
        return registry.getBuilder(PasswordProvider.class, sResourceName);
        }
    }
