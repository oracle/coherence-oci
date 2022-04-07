/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.tls;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.coherence.common.base.Logger;

import com.oracle.coherence.oci.secret.util.FixedSecretsFetcher;

import com.tangosol.net.ssl.AbstractCertificateLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link com.tangosol.net.ssl.CertificateLoader} that loads a certificate
 * from an OCI secret.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class SecretsCertificateLoader
        extends AbstractCertificateLoader
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link SecretsCertificateLoader}.
     *
     * @param auth              the {@link AbstractAuthenticationDetailsProvider OCI authentication} to use
     * @param sSecret           a secret OCID or name
     * @param sCompartmentOCID  an optional OCI compartment OCID if the secret is a name instead of an OCID
     */
    public SecretsCertificateLoader(AbstractAuthenticationDetailsProvider auth, String sSecret, String sCompartmentOCID)
        {
        super(sSecret);
        f_fetcher = new FixedSecretsFetcher(auth, sSecret, sCompartmentOCID);
        }

    // ----- AbstractPrivateKeyLoader methods -------------------------------

    @Override
    protected InputStream getInputStream() throws IOException
        {
        Logger.finest("Loading certificate from " + this);
        return f_fetcher.getSecret();
        }

    // ----- Object methods -------------------------------------------------

    @Override
    public String toString()
        {
        return f_fetcher.getDescription("OCISecretCert");
        }

    // ----- data members ---------------------------------------------------

    /**
     * The secret fetcher.
     */
    private final FixedSecretsFetcher f_fetcher;
    }
