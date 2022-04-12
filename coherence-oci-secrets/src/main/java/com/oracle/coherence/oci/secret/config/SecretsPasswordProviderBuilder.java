/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.coherence.oci.secret.util.SecretsFetcher;
import com.oracle.coherence.oci.secret.util.SecretsPasswordProvider;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.net.PasswordProvider;

/**
 * A {@link ParameterizedBuilder} that builds a {@link PasswordProvider} that
 * retrieves a pass phrase from a named secret in the OCI Secrets Service.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class SecretsPasswordProviderBuilder
        extends AbstractSecretsBuilder<PasswordProvider>
    {
    // ----- ParameterizedBuilder methods -----------------------------------

    @Override
    protected PasswordProvider realize(AbstractAuthenticationDetailsProvider auth, String sSecret, String sCompartmentId)
        {
        if (sSecret != null && !sSecret.isEmpty())
            {
            return new SecretsPasswordProvider(new SecretsFetcher(auth), sSecret, sCompartmentId);
            }
        return PasswordProvider.NullImplementation;
        }
    }
