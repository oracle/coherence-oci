/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;

import com.tangosol.coherence.config.ParameterList;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.expression.Parameter;
import com.tangosol.config.expression.ParameterResolver;

/**
 * A base {@link ParameterizedBuilder} that builds resources that
 * require a {@link SecretsClient OCI Secret Service client}.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public abstract class BaseSecretsBuilder<T>
        implements ParameterizedBuilder<T>
    {
    // ----- setters --------------------------------------------------------

    /**
     * Inject the OCI secrets service client.
     *
     * @param builder  an {@link ParameterizedBuilder} that builds an
     *                 OCI secrets service client
     */
    public void setClientBuilder(ParameterizedBuilder<SecretsClient> builder)
        {
        m_clientBuilder = builder;
        }

    /**
     * Inject the OCI {@link AbstractAuthenticationDetailsProvider} to use to create
     * a secrets service client.
     *
     * @param builder  an {@link ParameterizedBuilder} that builds an {@link AbstractAuthenticationDetailsProvider}
     */
    public void setAuthentication(ParameterizedBuilder<AbstractAuthenticationDetailsProvider> builder)
        {
        m_authBuilder = builder;
        }

    // ----- helper methods -------------------------------------------------

    /**
     * Realize the configured {@link SecretsClient} to use to connect to the OCI Secrets Service.
     *
     * @param resolver       an optional {@link ParameterResolver}
     * @param loader         an optional {@link ClassLoader} to use
     * @param parameterList  an optional {@link ParameterList}
     *
     * @return the configured {@link SecretsClient} to use to connect to the OCI Secrets Service
     */
    protected SecretsClient realizeClient(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        if (m_clientBuilder != null)
            {
            return m_clientBuilder.realize(resolver, loader, parameterList);
            }

        if (m_authBuilder != null)
            {
            AbstractAuthenticationDetailsProvider auth = m_authBuilder.realize(resolver, loader, parameterList);
            return SecretsClient.builder().build(auth);
            }

        Parameter parameter = resolver.resolve(CLIENT_BUILDER);
        if (parameter != null)
            {
            Object o = parameter.evaluate(resolver).get();
            if (o instanceof SecretsClientBuilder)
                {
                return ((SecretsClientBuilder) o).realize(resolver, loader, parameterList);
                }
            }

        return SecretsClientBuilder.DEFAULT.realize(resolver, loader, parameterList);
        }

    // ----- constants ------------------------------------------------------

    /**
     * The {@link SecretsClientBuilder} parameter name.
     */
    public static final String CLIENT_BUILDER = "SecretsClientBuilder";

    // ----- data members ---------------------------------------------------

    /**
     * The {@link SecretsClient} builder.
     */
    private ParameterizedBuilder<SecretsClient> m_clientBuilder;

    /**
     * The {@link AbstractAuthenticationDetailsProvider} builder.
     */
    private ParameterizedBuilder<AbstractAuthenticationDetailsProvider> m_authBuilder;
    }
