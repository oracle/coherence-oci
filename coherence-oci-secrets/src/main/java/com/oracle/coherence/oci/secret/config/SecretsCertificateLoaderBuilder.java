/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.coherence.oci.secret.tls.SecretsCertificateLoader;

/**
 * A builder for {@link SecretsCertificateLoader} instances.
 *
 * @author Jonathan Knight  2022.03.18
 */
public class SecretsCertificateLoaderBuilder
        extends AbstractSecretsBuilder<SecretsCertificateLoader>
    {
    @Override
    protected SecretsCertificateLoader realize(AbstractAuthenticationDetailsProvider auth, String sSecret, String sCompartmentId)
        {
        return new SecretsCertificateLoader(auth, sSecret, sCompartmentId);
        }
    }
