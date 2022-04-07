/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.util;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import java.io.InputStream;

/**
 * A {@link SecretsFetcher} that fetches a fixed secret name or OCID.
 *
 * @author Jonathan Knight  2022.04.07
 */
public class FixedSecretsFetcher
        extends SecretsFetcher
    {
    /**
     * Create a {@link FixedSecretsFetcher}.
     *
     * @param auth              the OCI authentication to use
     * @param sSecret           the secret name or OCID
     * @param sCompartmentOCID  an optional compartment OCID it the {@code sSecret} parameter is not an OCID
     */
    public FixedSecretsFetcher(AbstractAuthenticationDetailsProvider auth, String sSecret, String sCompartmentOCID)
        {
        super(auth);
        f_sSecret          = sSecret;
        f_sCompartmentOCID = sCompartmentOCID;
        }

    // ----- FixedSecretsFetcher methods ------------------------------------

    /**
     * Returns the secret data as an {@link InputStream}.
     *
     * @return the secret data as an {@link InputStream}
     */
    public InputStream getSecret()
        {
        return getSecret(f_sSecret, f_sCompartmentOCID);
        }

    /**
     * Return the secret name or OCID.
     *
     * @return the secret name or OCID
     */
    public String getSecretId()
        {
        return f_sSecret;
        }

    /**
     * Return the optional compartment OCID.
     *
     * @return the optional compartment OCID
     */
    public String getCompartmentOCID()
        {
        return f_sCompartmentOCID;
        }

    public String getDescription(String sPrefix)
        {
        if (f_sCompartmentOCID == null)
            {
            return sPrefix + "(" + f_sSecret + "}";
            }
        return sPrefix + "(" +
                "secret='" + f_sSecret + '\'' +
                ", compartment='" + f_sCompartmentOCID + "'}";
        }

    // ----- data members ---------------------------------------------------

    /**
     * A secret OCID or name.
     */
    private final String f_sSecret;

    /**
     * An optional OCI compartment OCID if the secret is a name instead of an OCID.
     */
    private final String f_sCompartmentOCID;
    }
