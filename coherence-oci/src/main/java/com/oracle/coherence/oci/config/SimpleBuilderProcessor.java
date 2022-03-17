/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.ConfigurationException;

import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;

import com.tangosol.run.xml.XmlElement;

import java.util.function.Supplier;

/**
 * An {@link ElementProcessor} that produces a {@link ParameterizedBuilder}.
 *
 * @param <T> the type produced by the {@link ParameterizedBuilder}
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class SimpleBuilderProcessor<T>
        implements ElementProcessor<ParameterizedBuilder<T>>
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link SimpleBuilderProcessor} that produces instances of
     * a {@link ParameterizedBuilder} from the specified supplier.
     *
     * @param supplier  the supplier that supplies a {@link ParameterizedBuilder} instance
     */
    public SimpleBuilderProcessor(Supplier<ParameterizedBuilder<T>> supplier)
        {
        f_supplier = supplier;
        }

    // ----- ElementProcessor methods ---------------------------------------

    @Override
    public ParameterizedBuilder<T> process(ProcessingContext ctx, XmlElement xmlElement) throws ConfigurationException
        {
        ParameterizedBuilder<T> builder = f_supplier.get();
        ctx.inject(builder, xmlElement);
        return builder;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The {@link Supplier} that supplies a {@link ParameterizedBuilder} instance.
     */
    private final Supplier<ParameterizedBuilder<T>> f_supplier;
    }
