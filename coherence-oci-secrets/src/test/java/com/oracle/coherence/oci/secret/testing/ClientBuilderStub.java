/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.testing;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.coherence.oci.secret.config.SecretsClientBuilder;

/**
 * A mock {@link SecretsClientBuilder}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class ClientBuilderStub
        implements SecretsClientBuilder
    {
    // ----- constructors ---------------------------------------------------

    public ClientBuilderStub(SecretsClient client)
        {
        f_client = client;
        }

    // ----- SecretsClientBuilder methods -----------------------------------

    @Override
    public SecretsClient getClient(AbstractAuthenticationDetailsProvider auth)
        {
        return f_client;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The {@link SecretsClient} this builder builds.
     */
    private final SecretsClient f_client;
    }
