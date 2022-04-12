/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.util;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SecretsPasswordProviderConfigTest
        extends AbstractSecretsTest
    {
    @BeforeEach
    void setup()
        {
        System.setProperty(BasicAuthenticationStub.PROP_PEM_FILE, s_serverKeyCertPair.m_fileKeyPEMNoPass.getAbsolutePath());
        System.setProperty("test.compartment.ocid", OCID_COMPARTMENT);
        System.setProperty("test.secret.ocid", OCID_SECRET);
        }

    @AfterEach
    void cleanup()
        {
        CacheFactory.shutdown();
        }

    @Test
    public void shouldConfigureProviderWithSecret()
        {
        ParameterizedBuilder<?> builder = getParameterizedBuilder("test-simple");
        assertThat(builder, is(instanceOf(SecretsPasswordProviderBuilder.class)));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId(), is(notNullValue()));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId().evaluate(null), is(OCID_SECRET));
        assertThat(((SecretsPasswordProviderBuilder) builder).getCompartmentId(), is(notNullValue()));
        assertThat(((SecretsPasswordProviderBuilder) builder).getCompartmentId().evaluate(null), is(nullValue()));
//
//        SecretsPasswordProvider provider = (SecretsPasswordProvider) builder.realize(null, null, null);
//        assertThat(provider, is(notNullValue()));
//        assertThat(provider.getSecretId(), is(OCID_SECRET));
//        assertThat(provider.getCompartmentId(), is(nullValue()));
        }

    @Test
    public void shouldConfigureProviderWithSecretNameAndCompartment()
        {
        ParameterizedBuilder<?> builder = getParameterizedBuilder("test-pass");
        assertThat(builder, is(instanceOf(SecretsPasswordProviderBuilder.class)));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId(), is(notNullValue()));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId().evaluate(null), is("foo"));
        assertThat(((SecretsPasswordProviderBuilder) builder).getCompartmentId(), is(notNullValue()));
        assertThat(((SecretsPasswordProviderBuilder) builder).getCompartmentId().evaluate(null), is(OCID_COMPARTMENT));

        SecretsPasswordProvider provider = (SecretsPasswordProvider) builder.realize(null, null, null);
        assertThat(provider, is(notNullValue()));
        assertThat(provider.getSecretId(), is("foo"));
        assertThat(provider.getCompartmentId(), is(OCID_COMPARTMENT));
        }

    @Test
    public void shouldConfigureProviderWithNoSecretId()
        {
        ParameterizedBuilder<?> builder = getParameterizedBuilder("test-no-pass");
        assertThat(builder, is(instanceOf(SecretsPasswordProviderBuilder.class)));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId(), is(notNullValue()));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId().evaluate(null), is(nullValue()));

        PasswordProvider provider = (PasswordProvider) builder.realize(null, null, null);
        assertThat(provider, is(notNullValue()));
        assertThat(provider.get(), is(new char[0]));
        }

    @Test
    public void shouldConfigureProviderWithCustomAuthentication()
        {
        ParameterizedBuilder<?> builder = getParameterizedBuilder("test-with-auth");
        assertThat(builder, is(instanceOf(SecretsPasswordProviderBuilder.class)));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId(), is(notNullValue()));
        assertThat(((SecretsPasswordProviderBuilder) builder).getSecretId().evaluate(null), is(OCID_SECRET));

        SecretsPasswordProvider provider = (SecretsPasswordProvider) builder.realize(null, null, null);
        assertThat(provider, is(notNullValue()));
        SecretsFetcher fetcher = provider.getSecretsFetcher();
        assertThat(fetcher, is(notNullValue()));
        AbstractAuthenticationDetailsProvider auth  = fetcher.getAuth();
        assertThat(auth, is(instanceOf(BasicAuthenticationStub.class)));
        assertThat(((BasicAuthenticationStub) auth).getKeyId(), is("oci-custom-authentication"));
        }

    private ParameterizedBuilder<?> getParameterizedBuilder(String sResourceName)
        {
        CacheFactory.shutdown();
        System.setProperty("coherence.override", "test-override.xml");

        Cluster cluster  = CacheFactory.getCluster();
        ParameterizedBuilderRegistry registry = ((OperationalContext) cluster).getBuilderRegistry();
        return registry.getBuilder(PasswordProvider.class, sResourceName);
        }

    // ----- data members ---------------------------------------------------

    public static final String OCID_COMPARTMENT = "ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a";

    public static final String OCID_SECRET = "ocid1.vaultsecret.oc1.uk-london-1.ambbcdaan7oqdzaascbykhgduwmr4mr4ml3ver6yq62secretfakeicqb3na";
    }
