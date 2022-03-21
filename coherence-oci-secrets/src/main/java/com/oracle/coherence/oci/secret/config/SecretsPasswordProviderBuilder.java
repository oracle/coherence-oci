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

import com.oracle.coherence.oci.config.OCINamespaceHandler;

import com.oracle.coherence.oci.secret.util.SecretsFetcher;
import com.oracle.coherence.oci.secret.util.SecretsPasswordProvider;

import com.tangosol.coherence.config.ParameterList;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.annotation.Injectable;

import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.ParameterResolver;

import com.tangosol.net.PasswordProvider;

/**
 * A {@link ParameterizedBuilder} that builds a {@link PasswordProvider} that
 * retrieves a pass phrase from a named secret in the OCI Secrets Service.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class SecretsPasswordProviderBuilder
        extends BaseSecretsBuilder<PasswordProvider>
    {
    // ----- ParameterizedBuilder methods -----------------------------------

    @Override
    public PasswordProvider realize(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        String sSecretId      = getSecretId().evaluate(resolver);
        String sCompartmentId = getCompartmentId().evaluate(resolver);

        if (sSecretId != null && !sSecretId.isEmpty())
            {
            AbstractAuthenticationDetailsProvider auth = realizeAuthentication(resolver, loader, parameterList);

            SecretsClient secretsClient = realizeSecretsClient(resolver, loader, parameterList);
            VaultsClient  vaultsClient  = realizeVaultsClient(resolver, loader, parameterList);

            SecretsFetcher fetcher = new SecretsFetcher(auth, secretsClient, vaultsClient);

            return new SecretsPasswordProvider(fetcher, sSecretId, sCompartmentId);
            }

        return PasswordProvider.NullImplementation;
        }

    // ----- BaseSecretsBuilder methods -------------------------------------

    @Override
    @Injectable(OCINamespaceHandler.ELEMENT_AUTHENTICATION)
    public void setAuthentication(ParameterizedBuilder<AbstractAuthenticationDetailsProvider> builder)
        {
        super.setAuthentication(builder);
        }

    @Override
    @Injectable(SecretsNamespaceHandlerExtension.ELEMENT_SECRETS_CLIENT)
    public void setSecretsClientBuilder(ParameterizedBuilder<SecretsClient> builder)
        {
        super.setSecretsClientBuilder(builder);
        }

    @Override
    @Injectable(SecretsNamespaceHandlerExtension.ELEMENT_SECRET_ID)
    public void setSecretId(Expression<String> sId)
        {
        super.setSecretId(sId);
        }

    @Injectable(SecretsNamespaceHandlerExtension.ELEMENT_SECRET_NAME)
    public void setSecretName(Expression<String> sName)
        {
        super.setSecretName(sName);
        }

    @Override
    @Injectable(OCINamespaceHandler.ELEMENT_COMPARTMENT)
    public void setCompartmentId(Expression<String> sId)
        {
        super.setCompartmentId(sId);
        }
    }
