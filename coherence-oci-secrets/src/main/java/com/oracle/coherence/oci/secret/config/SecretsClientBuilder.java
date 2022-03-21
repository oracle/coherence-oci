/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.bmc.secrets.SecretsClient;

import com.oracle.coherence.oci.config.OCINamespaceHandler;

import com.tangosol.coherence.config.ParameterList;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.expression.ParameterResolver;

/**
 * A class that provides a {@link SecretsClient}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public interface SecretsClientBuilder
        extends ParameterizedBuilder<SecretsClient>
    {
    /**
     * Create a {@link SecretsClient}.
     *
     * @param auth  the authentication details to use
     *
     * @return  a {@link SecretsClient}
     */
    SecretsClient getClient(AbstractAuthenticationDetailsProvider auth);

    @Override
    default SecretsClient realize(ParameterResolver resolver, ClassLoader loader, ParameterList list)
        {
        AbstractAuthenticationDetailsProvider auth = (AbstractAuthenticationDetailsProvider)
                resolver.resolve(OCINamespaceHandler.ELEMENT_AUTHENTICATION).evaluate(resolver).get();

        return getClient(auth);
        }

    /**
     * The default implementation of {@link SecretsClientBuilder}.
     */
    SecretsClientBuilder DEFAULT = auth -> SecretsClient.builder().build(auth);
    }
