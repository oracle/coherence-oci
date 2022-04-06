/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.util;

import com.oracle.bmc.OCID;

import com.tangosol.net.InputStreamPasswordProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link com.tangosol.net.PasswordProvider} that loads the pass
 * phrase from an OCI secret.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class SecretsPasswordProvider
        extends InputStreamPasswordProvider
    {
    /**
     * Create a {@link SecretsPasswordProvider}.
     *
     * @param fetcher         the {@link SecretsFetcher} to fetch secrets
     * @param sSecretId       the OCID or name of the secret to retrieve
     * @param sCompartmentId  the optional OCI compartment OCID if the secret id is a name instead of an OCID
     */
    public SecretsPasswordProvider(SecretsFetcher fetcher, String sSecretId, String sCompartmentId)
        {
        f_fetcher        = fetcher;
        f_sSecretId      = sSecretId;
        f_sCompartmentId = sCompartmentId;
        }

    @Override
    protected InputStream getInputStream() throws IOException
        {
        return f_fetcher.getSecret(f_sSecretId, f_sCompartmentId);
        }

    /**
     * Return the OCID or name of the secret to retrieve.
     *
     * @return the OCID or name of the secret to retrieve
     */
    protected String getSecretId()
        {
        return f_sSecretId;
        }

    /**
     * Return the optional OCI compartment OCID that must be supplied
     * if the {@link #f_sSecretId} field is a name instead of an OCID
     *
     * @return the optional OCI compartment OCID that must be supplied
     *         if the {@link #f_sSecretId} field is a name instead of
     *         an OCID
     */
    protected String getCompartmentId()
        {
        return f_sCompartmentId;
        }

    /**
     * Return the {@link SecretsFetcher} to use to retrieve secrets.
     *
     * @return the {@link SecretsFetcher} to use to retrieve secrets
     */
    protected SecretsFetcher getSecretsFetcher()
        {
        return f_fetcher;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The Secret Service client.
     */
    private final SecretsFetcher f_fetcher;

    /**
     * The Id of the secret to retrieve.
     */
    private final String f_sSecretId;

    /**
     * The OCI compartment identifier.
     */
    private final String f_sCompartmentId;
    }
