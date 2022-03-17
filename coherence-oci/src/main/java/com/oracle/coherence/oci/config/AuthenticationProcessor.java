/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.ConfigurationException;

import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;

import com.tangosol.run.xml.XmlElement;

import static com.oracle.coherence.oci.config.OCINamespaceHandler.ELEMENT_AUTHENTICATION;

/**
 * An {@link ElementProcessor} that produces a {@link ParameterizedBuilder}
 * that builds {@link AbstractAuthenticationDetailsProvider} instances.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
@XmlSimpleName(ELEMENT_AUTHENTICATION)
public class AuthenticationProcessor
        implements ElementProcessor<ParameterizedBuilder<AbstractAuthenticationDetailsProvider>>
    {
    @Override
    public ParameterizedBuilder<AbstractAuthenticationDetailsProvider> process(ProcessingContext ctx, XmlElement xmlElement) throws ConfigurationException
        {
        return ctx.processOnlyElementOf(xmlElement);
        }
    }
