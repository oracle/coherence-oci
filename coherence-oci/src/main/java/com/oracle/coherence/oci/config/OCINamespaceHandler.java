/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.tangosol.coherence.config.xml.processor.InstanceProcessor;

import com.tangosol.config.xml.AbstractNamespaceHandler;

import java.util.ServiceLoader;

/**
 * The custom Coherence {@link com.tangosol.config.xml.NamespaceHandler} for the
 * Coherence OCI extensions.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class OCINamespaceHandler
        extends AbstractNamespaceHandler
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Construct and configure an OCI namespace handler.
     */
    public OCINamespaceHandler()
        {
        registerProcessor(ELEMENT_AUTHENTICATION, new InstanceProcessor());
        registerProcessor(ELEMENT_OCI_CONFIG_FILE, new SimpleBuilderProcessor<>(AuthenticationBuilder::new));

        ServiceLoader<NamespaceHandlerExtension> serviceLoader = ServiceLoader.load(NamespaceHandlerExtension.class);
        for (NamespaceHandlerExtension extension : serviceLoader)
            {
            extension.extend(this);
            }
        }

    // ----- constants ------------------------------------------------------

    /**
     * The name of the XML element containing a custom OCI authentication configuration.
     */
    public static final String ELEMENT_AUTHENTICATION = "authentication";

    /**
     * The name of the XML element containing a custom OCI compartment OCID.
     */
    public static final String ELEMENT_COMPARTMENT = "compartment-id";

    /**
     * The name of the XML element containing a custom OCI configuration file.
     */
    public static final String ELEMENT_OCI_CONFIG_FILE = "oci-config-file";
    }
