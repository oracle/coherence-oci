/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

/**
 * A {@link ParameterizedBuilder} that uses an {@link AbstractAuthenticationDetailsProvider}.
 *
 * @param <T> they type the builder builds
 *
 * @author Jonathan Knight  2022.01.25
 */
public interface AuthenticationAwareBuilder<T>
        extends ParameterizedBuilder<T>
    {
    /**
     * Inject the OCI {@link AbstractAuthenticationDetailsProvider} to use to create
     * an OCI client.
     *
     * @param builder  an {@link ParameterizedBuilder} that builds an {@link AbstractAuthenticationDetailsProvider}
     */
    void setAuthentication(ParameterizedBuilder<AbstractAuthenticationDetailsProvider> builder);

    /**
     * Return the OCI {@link AbstractAuthenticationDetailsProvider} to use to create
     * an OCI client.
     *
     * @return  the OCI {@link AbstractAuthenticationDetailsProvider} to use to create
     * an OCI client.
     */
    ParameterizedBuilder<AbstractAuthenticationDetailsProvider> getAuthentication();
    }
