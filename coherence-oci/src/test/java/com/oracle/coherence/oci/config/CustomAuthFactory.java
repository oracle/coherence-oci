/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

/**
 * A stub {@link AbstractAuthenticationDetailsProvider} factory.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class CustomAuthFactory
    {
    public static AbstractAuthenticationDetailsProvider build()
        {
        return new Auth();
        }

    public static class Auth
            implements AbstractAuthenticationDetailsProvider
        {
        }
    }
