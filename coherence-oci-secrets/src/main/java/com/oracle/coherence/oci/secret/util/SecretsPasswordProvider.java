/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.util;

import com.oracle.bmc.secrets.SecretsClient;

import com.tangosol.net.InputStreamPasswordProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link com.tangosol.net.PasswordProvider} that loads the pass
 * phrase from an OCI secret.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class SecretsPasswordProvider
        extends InputStreamPasswordProvider
    {
    /**
     * Create a {@link SecretsPasswordProvider}.
     *
     * @param client  the {@link SecretsClient} to use to retrieve secrets
     * @param sId     the identifier of the secret to retrieve
     */
    public SecretsPasswordProvider(SecretsClient client, String sId)
        {
        this(new SecretsFetcher(client), sId);
        }

    /**
     * Create a {@link SecretsPasswordProvider}.
     *
     * @param fetcher  the {@link SecretsFetcher} to use to retrieve secrets
     * @param sId      the identifier of the secret to retrieve
     */
    public SecretsPasswordProvider(SecretsFetcher fetcher, String sId)
        {
        f_fetcher = fetcher;
        f_sId     = sId;
        }

    @Override
    protected InputStream getInputStream() throws IOException
        {
        return f_fetcher.get(f_sId);
        }

    protected String getSecretId()
        {
        return f_sId;
        }

    /**
     * The Secret Service client.
     */
    private final SecretsFetcher f_fetcher;

    /**
     * The Id of the secret to retrieve.
     */
    private final String f_sId;
    }
