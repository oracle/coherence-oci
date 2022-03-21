/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.tangosol.coherence.config.builder.NamedResourceBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilderRegistry;

import com.tangosol.config.expression.Expression;
import com.tangosol.config.xml.DefaultProcessingContext;

import com.tangosol.internal.net.cluster.DefaultClusterDependencies;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.OperationalContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AuthenticationConfigTest
    {
    @AfterEach
    void cleanup()
        {
        CacheFactory.shutdown();
        }

    @Test
    public void shouldConfigureDefaultAuthentication()
        {
        AuthenticationBuilder builder        = getAuthenticationBuilder("default-auth");
        Expression<String>    expFileName    = builder.getFileName();
        Expression<String>    expProfileName = builder.getProfileName();

        assertThat(expFileName, is(nullValue()));
        assertThat(expProfileName, is(nullValue()));
        }

    @Test
    public void shouldConfigureAuthentication()
        {
        AuthenticationBuilder builder        = getAuthenticationBuilder("auth-one");
        Expression<String>    expFileName    = builder.getFileName();
        Expression<String>    expProfileName = builder.getProfileName();

        assertThat(expFileName, is(notNullValue()));
        assertThat(expFileName.evaluate(null), is("/oci/my-config-one"));
        assertThat(expProfileName, is(notNullValue()));
        assertThat(expProfileName.evaluate(null), is("testing-one"));
        }

    @Test
    public void shouldConfigureAuthenticationWithJustFilename()
        {
        AuthenticationBuilder builder        = getAuthenticationBuilder("auth-two");
        Expression<String>    expFileName    = builder.getFileName();
        Expression<String>    expProfileName = builder.getProfileName();

        assertThat(expFileName, is(notNullValue()));
        assertThat(expFileName.evaluate(null), is("/oci/my-config-two"));
        assertThat(expProfileName, is(nullValue()));
        }

    @Test
    public void shouldConfigureAuthenticationWithJustProfile()
        {
        AuthenticationBuilder builder        = getAuthenticationBuilder("auth-three");
        Expression<String>    expFileName    = builder.getFileName();
        Expression<String>    expProfileName = builder.getProfileName();

        assertThat(expFileName, is(nullValue()));
        assertThat(expProfileName, is(notNullValue()));
        assertThat(expProfileName.evaluate(null), is("testing-three"));
        }

    @Test
    public void shouldConfigureCustomAuthentication()
        {
        ParameterizedBuilder<?> builder = getParameterizedBuilder("auth-four");
        assertThat(builder, is(notNullValue()));
        Object oAuth = builder.realize(null, null, null);
        assertThat(oAuth, is(instanceOf(CustomAuthFactory.Auth.class)));
        }

    // ----- helper methods -------------------------------------------------

    private AuthenticationBuilder getAuthenticationBuilder(String sResourceName)
        {
        ParameterizedBuilder<?> delegate = getParameterizedBuilder(sResourceName);
        assertThat(delegate, is(instanceOf(AuthenticationBuilder.class)));

        return (AuthenticationBuilder) delegate;
        }

    @SuppressWarnings("rawtypes")
    private ParameterizedBuilder<?> getParameterizedBuilder(String sResourceName)
        {
        CacheFactory.shutdown();
        System.setProperty("coherence.override", "test-auth-override.xml");

        Cluster                             cluster  = CacheFactory.getCluster();
        ParameterizedBuilderRegistry        registry = ((OperationalContext) cluster).getBuilderRegistry();
        ParameterizedBuilder<?>             builder  = registry.getBuilder(NamedResourceBuilder.class, sResourceName);

        assertThat(builder, is(instanceOf(NamedResourceBuilder.class)));
        assertThat(((NamedResourceBuilder) builder).getName(), is(sResourceName));

        return ((NamedResourceBuilder) builder).getDelegate();
        }

    // ----- data members ---------------------------------------------------

    private DefaultClusterDependencies m_deps;

    private DefaultProcessingContext m_ctxClusterConfig;
    }
