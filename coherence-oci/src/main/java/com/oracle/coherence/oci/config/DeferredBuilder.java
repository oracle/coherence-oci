/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.tangosol.coherence.config.ParameterList;

import com.tangosol.coherence.config.builder.NamedResourceBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilderRegistry;

import com.tangosol.config.ConfigurationException;

import com.tangosol.config.expression.ParameterResolver;

import com.tangosol.config.xml.ProcessingContext;

import com.tangosol.net.OperationalContext;

import com.tangosol.run.xml.XmlElement;

/**
 * A {@link ParameterizedBuilder} that uses a named cluster resource that may be registered later.
 *
 * @param <T> the type of the resource built by th ebuilder
 *
 * @author Jonathan Knight  2022.03.20
 */
public class DeferredBuilder<T>
        implements ParameterizedBuilder<T>
    {
    // ----- constructors ---------------------------------------------------

    /**
     *
     * @param registry  the {@link ParameterizedBuilderRegistry} that will contain the
     *                  {@link NamedResourceBuilder} to defer to
     * @param sName     the name of the builder to defer to
     */
    public DeferredBuilder(ParameterizedBuilderRegistry registry, String sName)
        {
        m_registry = registry;
        m_sName    = sName;
        }

    // ----- ParameterizedBuilder methods -----------------------------------

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public T realize(ParameterResolver resolver, ClassLoader loader, ParameterList list)
        {
        ParameterizedBuilder<NamedResourceBuilder> builder = m_registry.getBuilder(NamedResourceBuilder.class, m_sName);
        if (builder == null)
            {
            return null;
            }
        return (T) ((NamedResourceBuilder) builder).getDelegate().realize(resolver, loader, list);
        }

    // ----- helper methods -------------------------------------------------

    public static <T> DeferredBuilder<T> createDeferredBuilder(ProcessingContext context, String sName, XmlElement element)
        {
        ParameterizedBuilderRegistry registry = context.getCookie(ParameterizedBuilderRegistry.class);

        if (registry == null)
            {
            // grab the operational context from which we can look up the resource
            OperationalContext ctxOperational = context.getCookie(OperationalContext.class);
            if (ctxOperational == null)
                {
                throw new ConfigurationException("Attempted to resolve the OperationalContext in [" + element
                    + "] but it was not defined", "The registered ElementHandler for the <cluster-resource> element "
                    + "is not operating in an OperationalContext");
                }
            registry = ctxOperational.getBuilderRegistry();
            }
        
        return new DeferredBuilder<>(registry, sName);
        }

    // ----- data members ---------------------------------------------------

    /**
     * The {@link ParameterizedBuilderRegistry} that will contain the {@link NamedResourceBuilder}
     * to defer to.
     */
    private final ParameterizedBuilderRegistry m_registry;

    /**
     * The name of the builder to defer to.
     */
    private final String m_sName;
    }
