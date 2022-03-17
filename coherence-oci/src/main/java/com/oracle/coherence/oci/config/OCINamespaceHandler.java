/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.tangosol.coherence.config.xml.processor.PasswordProviderBuilderProcessor;

import com.tangosol.config.xml.AbstractNamespaceHandler;

import java.util.ServiceLoader;

/**
 * The custom Coherence {@link com.tangosol.config.xml.NamespaceHandler} for the
 * Coherence OCI extensions.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class OCINamespaceHandler
        extends AbstractNamespaceHandler
    {
    // ----- constructors ---------------------------------------------------

    public OCINamespaceHandler()
        {
        registerProcessor(AuthenticationProcessor.class);

        registerProcessor(ELEMENT_OCI_CONFIG_FILE, new SimpleBuilderProcessor<>(AuthenticationBuilder::new));

        ServiceLoader<NamespaceHandlerExtension> serviceLoader = ServiceLoader.load(NamespaceHandlerExtension.class);
        for (NamespaceHandlerExtension extension : serviceLoader)
            {
            extension.extend(this);
            }
        }

    // ----- constants ------------------------------------------------------

    public static final String ELEMENT_AUTHENTICATION = "authentication";

    public static final String ELEMENT_OCI_CONFIG_FILE = "oci-config-file";
    }
