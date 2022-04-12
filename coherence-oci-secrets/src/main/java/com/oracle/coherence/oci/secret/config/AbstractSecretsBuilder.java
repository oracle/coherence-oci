/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.OCID;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.bmc.secrets.SecretsClient;

import com.oracle.bmc.vault.VaultsClient;
import com.oracle.coherence.oci.CoherenceOCI;

import com.oracle.coherence.oci.config.OCINamespaceHandler;
import com.oracle.coherence.oci.config.SimpleBuilderProcessor;

import com.tangosol.coherence.config.Config;
import com.tangosol.coherence.config.ParameterList;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.ConfigurationException;

import com.tangosol.config.annotation.Injectable;

import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.ParameterResolver;

/**
 * A base class for secrets {@link ParameterizedBuilder} implementations.
 *
 * @param <T>  the type of the resource to build
 *
 * @author Jonathan Knight  2022.01.25
 */
public abstract class AbstractSecretsBuilder<T>
        extends BaseSecretsBuilder<T>
        implements SimpleBuilderProcessor.SimpleContent
    {
    // ----- AbstractSecretsBuilder methods ---------------------------------

    @Override
    public T realize(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        AbstractAuthenticationDetailsProvider auth           = realizeAuthentication(resolver, loader, parameterList);
        String                                sSecretId      = getSecretId().evaluate(resolver);
        String                                sCompartmentId = getCompartmentId().evaluate(resolver);

        if (sSecretId != null && !OCID.isValid(sSecretId))
            {
            if (sCompartmentId == null)
                {
                // the secret id is not an OCID, so assume it is a name.
                // no compartment OCID was configured, so try the default from System properties
                sCompartmentId = Config.getProperty(CoherenceOCI.PROP_OCI_COMPARTMENT);
                }

            if (!OCID.isValid(sCompartmentId))
                {
                throw new ConfigurationException("The secret id ('" + sSecretId + " is not an OCID, assuming it is "
                        + "a secret name in which case a valid Compartment OCID is also required",
                        "Make the secret id a valid OCID or supply a compartment OCID, either by configuring the "
                        + " <compartment-id> element, or setting the " + CoherenceOCI.PROP_OCI_COMPARTMENT + " property");
                }
            }

        return realize(auth, sSecretId, sCompartmentId);
        }

    /**
     * Realize an instance of an object of type {@code T}.
     *
     * @param auth            the {@link AbstractAuthenticationDetailsProvider OCI authentication} to use
     * @param sSecret         the secret OCID or name
     * @param sCompartmentId  an optional OCI compartment OCID if {@code sSecret} is a secret name
     *
     * @return an instance of an object of type {@code T}
     */
    protected abstract T realize(AbstractAuthenticationDetailsProvider auth, String sSecret, String sCompartmentId);

    // ----- BaseSecretsBuilder methods -------------------------------------

    @Override
    public void setSimpleContent(Expression<String> expr)
        {
        setSecretId(expr);
        }

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
    @Injectable(SecretsNamespaceHandlerExtension.ELEMENT_VAULTS_CLIENT)
    public void setVaultsClientBuilder(ParameterizedBuilder<VaultsClient> builder)
        {
        super.setVaultsClientBuilder(builder);
        }

    @Override
    @Injectable(SecretsNamespaceHandlerExtension.ELEMENT_SECRET_ID)
    public void setSecretId(Expression<String> sId)
        {
        super.setSecretId(sId);
        }

    @Override
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
