/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.ConfigurationException;

import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.LiteralExpression;

import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;

import com.tangosol.run.xml.XmlElement;

import java.text.ParseException;

import java.util.function.Supplier;

import static com.oracle.coherence.oci.CoherenceOCI.AUTHENTICATION_BUILDER;

/**
 * An {@link ElementProcessor} that produces a {@link ParameterizedBuilder}.
 *
 * @param <T> the type produced by the {@link ParameterizedBuilder}
 *
 * @author Jonathan Knight  2022.01.25
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

        if (xmlElement.getElementList().isEmpty() && builder instanceof SimpleContent)
            {
            try
                {
                // element has no children, it could just contain a secret OCID
                String sValue = xmlElement.getString();

                Expression<String> expr = sValue == null || sValue.isEmpty()
                        ? new LiteralExpression<>(null)
                        : ctx.getExpressionParser().parse(sValue, String.class);

                ((SimpleContent) builder).setSimpleContent(expr);
                }
            catch (ParseException e)
                {
                throw new ConfigurationException("Cannot parse expression in " + xmlElement, "Use a valid expression", e);
                }
            }
        else
            {
            ctx.inject(builder, xmlElement);
            }

        if (builder instanceof AuthenticationAwareBuilder)
            {
            AuthenticationAwareBuilder<T> authenticationAware = (AuthenticationAwareBuilder<T>) builder;
            if (authenticationAware.getAuthentication() == null)
                {
                authenticationAware.setAuthentication(DeferredBuilder.createDeferredBuilder(ctx, AUTHENTICATION_BUILDER, xmlElement));
                }
            }

        return builder;
        }

    // ----- SimpleContent --------------------------------------------------

    /**
     * Implemented by classes that can take a simple element content.
     */
    public interface SimpleContent
        {
        /**
         * Set the expression parsed from a simple XML element content.
         *
         * @param expr the expression parsed from a simple XML element content
         */
        void setSimpleContent(Expression<String> expr);
        }

    // ----- data members ---------------------------------------------------

    /**
     * The {@link Supplier} that supplies a {@link ParameterizedBuilder} instance.
     */
    private final Supplier<ParameterizedBuilder<T>> f_supplier;
    }
