/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;

import com.oracle.coherence.oci.secret.util.SecretsPasswordProvider;

import com.tangosol.coherence.config.ParameterList;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.annotation.Injectable;

import com.tangosol.config.expression.ParameterResolver;

import com.tangosol.net.PasswordProvider;

/**
 * A {@link ParameterizedBuilder} that builds a {@link PasswordProvider} that
 * retrieves a pass phrase from a named secret in the OCI Secrets Service.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class SecretsPasswordProviderBuilder
        extends BaseSecretsBuilder<PasswordProvider>
    {
    // ----- ParameterizedBuilder methods -----------------------------------

    @Override
    public PasswordProvider realize(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        if (m_sSecretId == null || m_sSecretId.isEmpty())
            {
            return PasswordProvider.NullImplementation;
            }

        SecretsClient client = realizeClient(resolver, loader, parameterList);
        return new SecretsPasswordProvider(client, m_sSecretId);
        }

    // ----- BaseSecretsBuilder methods -------------------------------------

    @Override
    @Injectable("authentication")
    public void setAuthentication(ParameterizedBuilder<AbstractAuthenticationDetailsProvider> builder)
        {
        super.setAuthentication(builder);
        }

    @Override
    @Injectable("secrets-client")
    public void setClientBuilder(ParameterizedBuilder<SecretsClient> builder)
        {
        super.setClientBuilder(builder);
        }

    @Injectable("secret-id")
    public void setSecretId(String sId)
        {
        m_sSecretId = sId;
        }

    // ----- accessors ------------------------------------------------------

    public String getSecretId()
        {
        return m_sSecretId;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The identifier of the secret to fetch.
     */
    private String m_sSecretId;
    }
