/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.testing;

import com.oracle.coherence.common.base.Classes;

import com.oracle.coherence.oci.config.OCINamespaceHandler;

import com.tangosol.coherence.config.ParameterList;
import com.tangosol.coherence.config.ParameterMacroExpressionParser;
import com.tangosol.coherence.config.ResolvableParameterList;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.coherence.config.xml.OperationalConfigNamespaceHandler;

import com.tangosol.config.expression.Parameter;

import com.tangosol.config.xml.DocumentProcessor;

import com.tangosol.run.xml.XmlDocument;
import com.tangosol.run.xml.XmlElement;

import com.tangosol.util.Base;
import com.tangosol.util.SimpleResourceRegistry;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A helper class for test XML configurations.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class ConfigHelper
    {
    /**
     * Add the correct namespace attributes to the specified {@link XmlElement}.
     *
     * @param xml  the {@link XmlElement} to add the namespace attributes to
     */
    public static void addNamespaces(XmlElement xml)
        {
        String sHandler = "class://" + OCINamespaceHandler.class.getName();
        String sSchema  = "http://xmlns.oracle.com/coherence/coherence-operational-config coherence-operational-config.xsd"
                + "\n" + sHandler + " coherence-oci.xsd";

        xml.addAttribute("xmlns:xsi").setString("http://www.w3.org/2001/XMLSchema-instance");
        xml.addAttribute("xmlns").setString("http://xmlns.oracle.com/coherence/coherence-operational-config");
        xml.addAttribute("xmlns:oci").setString(sHandler);
        xml.addAttribute("xsi:schemaLocation").setString(sSchema);
        }

    /**
     * Process the specified xml.
     *
     * @param xml         the xml to process
     * @param parameters  optional {@link Parameter parameters} to add to the
     *                    {@link com.tangosol.config.expression.ParameterResolver}
     *
     * @return the {@link Object} produced by processing the xml
     */
    public static Object process(XmlDocument xml, Parameter... parameters)
        {
        ConfigHelper.addNamespaces(xml);

        DocumentProcessor.DefaultDependencies deps = new DocumentProcessor.DefaultDependencies();
        deps.setClassLoader(Base.getContextClassLoader());
        deps.setDefaultNamespaceHandler(new OperationalConfigNamespaceHandler());
        deps.setExpressionParser(ParameterMacroExpressionParser.INSTANCE);
        deps.setResourceRegistry(new SimpleResourceRegistry());

        DocumentProcessor processor = new DocumentProcessor(deps);
        Object            oBuilder  = processor.process(new XmlDocumentReferenceStub(xml));

        assertThat(oBuilder, is(instanceOf(ParameterizedBuilder.class)));
        ParameterizedBuilder<?> builder = (ParameterizedBuilder<?>) oBuilder;

        ClassLoader             loader        = Classes.getContextClassLoader();
        ParameterList           list          = new ResolvableParameterList();
        ResolvableParameterList resolver      = new ResolvableParameterList();

        for (Parameter parameter : parameters)
            {
            resolver.add(parameter);
            }

        return builder.realize(resolver, loader, list);
        }
    }
