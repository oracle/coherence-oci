/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.coherence.oci.secret.tls.SecretsPrivateKeyLoader;

/**
 * A builder for {@link SecretsPrivateKeyLoader} instances.
 *
 * @author Jonathan Knight  2022.03.18
 */
public class SecretsPrivateKeyLoaderBuilder
        extends AbstractSecretsBuilder<SecretsPrivateKeyLoader>
    {
    @Override
    protected SecretsPrivateKeyLoader realize(AbstractAuthenticationDetailsProvider auth, String sSecret, String sCompartmentId)
        {
        return new SecretsPrivateKeyLoader(auth, sSecret, sCompartmentId);
        }
    }
