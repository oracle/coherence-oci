/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci;

/**
 * Common constants used by Coherence OCI integration.
 *
 * @author Jonathan Knight  2022.01.25
 */
public abstract class CoherenceOCI
    {
    /**
     * Private constructor for utilities class.
     */
    private CoherenceOCI()
        {
        }

    /**
     * The system property to override the OCI configuration file used by Coherence.
     */
    public static String PROP_OCI_CONFIG_FILE = "coherence.oci.config.file";

    /**
     * The system property to override the OCI profile used by Coherence.
     */
    public static String PROP_OCI_CONFIG_PROFILE = "coherence.oci.config.profile";

    /**
     * The system property to override the OCI compartment used by Coherence.
     */
    public static String PROP_OCI_COMPARTMENT = "coherence.oci.compartment";

    /**
     * The default authentication builder resource name.
     * <p>
     * If a cluster resource is defined in the Coherece operational configuration with this name,
     * it will be used as the {@link com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider}
     * for all OCI integrations.
     */
    public static String AUTHENTICATION_BUILDER = "oci-authentication";
    }
