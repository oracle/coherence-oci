/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.tls;

import com.oracle.bmc.secrets.SecretsClient;

import com.oracle.coherence.oci.secret.util.SecretsFetcher;

import com.tangosol.net.ssl.AbstractKeyStoreLoader;

import java.io.IOException;
import java.io.InputStream;

import java.util.Objects;

/**
 * A {@link com.tangosol.net.ssl.KeyStoreLoader} that loads a keystore
 * from an OCI secret.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class SecretsKeyStoreLoader
        extends AbstractKeyStoreLoader
    {
    // ----- constructors ---------------------------------------------------

    public SecretsKeyStoreLoader(SecretsClient client)
        {
        f_fetcher = new SecretsFetcher(Objects.requireNonNull(client));
        }

    // ----- AbstractPrivateKeyLoader methods -------------------------------

    @Override
    protected InputStream getInputStream(String sId) throws IOException
        {
        return f_fetcher.get(sId);
        }

    // ----- helper methods -------------------------------------------------

    /**
     * Returns the {@link SecretsFetcher} used by this {@link SecretsKeyStoreLoader}.
     *
     * @return the {@link SecretsFetcher} used by this {@link SecretsKeyStoreLoader}
     */
    public SecretsFetcher getSecretsFetcher()
        {
        return f_fetcher;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The Secret Service client.
     */
    private final SecretsFetcher f_fetcher;
    }
