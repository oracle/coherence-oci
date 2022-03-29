/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;

import com.oracle.bmc.vault.VaultsClient;
import com.oracle.coherence.oci.config.AuthenticationAwareBuilder;
import com.oracle.coherence.oci.config.AuthenticationBuilder;
import com.tangosol.coherence.config.ParameterList;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.LiteralExpression;
import com.tangosol.config.expression.ParameterResolver;

/**
 * A base {@link ParameterizedBuilder} that builds resources that
 * require a {@link SecretsClient OCI Secret Service client}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public abstract class BaseSecretsBuilder<T>
        implements ParameterizedBuilder<T>, AuthenticationAwareBuilder<T>
    {
    // ----- setters --------------------------------------------------------

    /**
     * Inject the OCI secrets service client.
     *
     * @param builder  a {@link ParameterizedBuilder} that builds an
     *                 OCI secrets service client
     */
    public void setSecretsClientBuilder(ParameterizedBuilder<SecretsClient> builder)
        {
        m_secretsClientBuilder = builder;
        }

    /**
     * Return the OCI secrets service client.
     *
     * @return a {@link ParameterizedBuilder} that builds an
     *         OCI secrets service client
     */
    public ParameterizedBuilder<SecretsClient> getSecretsClientBuilder()
        {
        return m_secretsClientBuilder;
        }

    /**
     * Inject the OCI vault service client.
     *
     * @param builder  a {@link ParameterizedBuilder} that builds an
     *                 OCI vault service client
     */
    public void setVaultsClientBuilder(ParameterizedBuilder<VaultsClient> builder)
        {
        m_vaultsClientBuilder = builder;
        }

    /**
     * Return the OCI vault service client.
     *
     * @return a {@link ParameterizedBuilder} that builds an
     *         OCI vault service client
     */
    public ParameterizedBuilder<VaultsClient> getVaultsClientBuilder()
        {
        return m_vaultsClientBuilder;
        }

    /**
     * Set the secret OCID.
     *
     * @param sId  the secret OCID
     */
    public void setSecretId(Expression<String> sId)
        {
        m_exprSecretId = sId == null ? new LiteralExpression<>(null) : sId;
        }

    /**
     * Set the secret name.
     *
     * @param sName  the secret name
     */
    public void setSecretName(Expression<String> sName)
        {
        m_exprSecretId = sName == null ? new LiteralExpression<>(null) : sName;
        }

    /**
     * Returns the secret identifier or name.
     *
     * @return the secret identifier or name
     */
    public Expression<String> getSecretId()
        {
        return m_exprSecretId;
        }

    /**
     * Set the compartment OCID.
     *
     * @param sId  the compartment OCID
     *
     * @throws IllegalArgumentException if the id is not a valid OCID
     */
    public void setCompartmentId(Expression<String> sId)
        {
        m_exprCompartmentId = sId == null ? new LiteralExpression<>(null) : sId;
        }

    /**
     * Returns the secret compartment OCID.
     *
     * @return the secret compartment OCID
     */
    public Expression<String> getCompartmentId()
        {
        return m_exprCompartmentId;
        }

    // ----- AuthenticationAwareBuilder methods -----------------------------

    @Override
    public void setAuthentication(ParameterizedBuilder<AbstractAuthenticationDetailsProvider> builder)
        {
        m_authBuilder = builder == null ? AuthenticationBuilder.INSTANCE : builder;
        }

    @Override
    public ParameterizedBuilder<AbstractAuthenticationDetailsProvider> getAuthentication()
        {
        return m_authBuilder;
        }

    // ----- helper methods -------------------------------------------------

    /**
     * Realize the configured {@link AbstractAuthenticationDetailsProvider OCI authentication provider} to use to
     * connect to the OCI Secrets Service.
     *
     * @param resolver       an optional {@link ParameterResolver}
     * @param loader         an optional {@link ClassLoader} to use
     * @param parameterList  an optional {@link ParameterList}
     *
     * @return the configured {@link AbstractAuthenticationDetailsProvider OCI authentication provider}
     *         or {@code null} if no authentication was configured
     */
    protected AbstractAuthenticationDetailsProvider realizeAuthentication(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        ParameterizedBuilder<AbstractAuthenticationDetailsProvider> builder = m_authBuilder;
        if (builder == null)
            {
            builder = AuthenticationBuilder.INSTANCE;
            }
        return builder.realize(resolver, loader, parameterList);
        }

    /**
     * Realize the configured {@link SecretsClient} to use to connect to the OCI Secrets Service.
     *
     * @param resolver       an optional {@link ParameterResolver}
     * @param loader         an optional {@link ClassLoader} to use
     * @param parameterList  an optional {@link ParameterList}
     *
     * @return the configured {@link SecretsClient} to use to connect to the OCI Secrets Service
     */
    protected SecretsClient realizeSecretsClient(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        if (m_secretsClientBuilder != null)
            {
            return m_secretsClientBuilder.realize(resolver, loader, parameterList);
            }
        return null;
        }

    /**
     * Realize the configured {@link VaultsClient} to use to connect to the OCI Vault Service.
     *
     * @param resolver       an optional {@link ParameterResolver}
     * @param loader         an optional {@link ClassLoader} to use
     * @param parameterList  an optional {@link ParameterList}
     *
     * @return the configured {@link VaultsClient} to use to connect to the OCI Vault Service
     */
    protected VaultsClient realizeVaultsClient(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        if (m_vaultsClientBuilder != null)
            {
            return m_vaultsClientBuilder.realize(resolver, loader, parameterList);
            }
        return null;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The {@link SecretsClient} builder.
     */
    private ParameterizedBuilder<SecretsClient> m_secretsClientBuilder;

    /**
     * The {@link VaultsClient} builder.
     */
    private ParameterizedBuilder<VaultsClient> m_vaultsClientBuilder;

    /**
     * The {@link AbstractAuthenticationDetailsProvider} builder.
     */
    private ParameterizedBuilder<AbstractAuthenticationDetailsProvider> m_authBuilder;

    /**
     * The Secret identifier.
     */
    private Expression<String> m_exprSecretId = new LiteralExpression<>(null);

    /**
     * The OCI compartment identifier.
     */
    private Expression<String> m_exprCompartmentId = new LiteralExpression<>(null);
    }
