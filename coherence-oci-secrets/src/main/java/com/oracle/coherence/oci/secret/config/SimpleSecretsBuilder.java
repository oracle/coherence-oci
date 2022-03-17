/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.bmc.secrets.SecretsClient;

import com.oracle.coherence.oci.config.SimpleBuilderProcessor;
import com.tangosol.coherence.config.ParameterList;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.annotation.Injectable;

import com.tangosol.config.expression.ParameterResolver;

import java.util.Objects;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link ParameterizedBuilder} that builds a simple resource that uses
 * a {@link SecretsClient}.
 *
 * @param <T>  the type of the resource to build
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class SimpleSecretsBuilder<T>
        extends BaseSecretsBuilder<T>
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link SimpleSecretsBuilder}.
     *
     * @param function  the {@link Function} that builds the resource using a specified {@link SecretsClient}
     *
     * @throws NullPointerException if the {@code function} parameter is {@code null}
     */
    public SimpleSecretsBuilder(Function<SecretsClient, T> function)
        {
        f_function = Objects.requireNonNull(function);
        }

    // ----- BaseSecretsBuilder methods -------------------------------------

    @Override
    public T realize(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        SecretsClient client = realizeClient(resolver, loader, parameterList);
        return f_function.apply(client);
        }

    // ----- BaseSecretsBuilder methods -------------------------------------

    @Override
    @Injectable("authentication")
    public void setAuthentication(ParameterizedBuilder<AbstractAuthenticationDetailsProvider> builder)
        {
        super.setAuthentication(builder);
        }

    @Override
    @Injectable("secrets-client")
    public void setClientBuilder(ParameterizedBuilder<SecretsClient> builder)
        {
        super.setClientBuilder(builder);
        }

    // ----- helper methods -------------------------------------------------

    public static <T> Supplier<ParameterizedBuilder<T>> asSupplier(Function<SecretsClient, T> function)
        {
        return () -> new SimpleSecretsBuilder<>(function);
        }

    public static <T> SimpleBuilderProcessor<T> asProcessor(Function<SecretsClient, T> function)
        {
        return new SimpleBuilderProcessor<>(asSupplier(function));
        }

    // ----- data members ---------------------------------------------------

    private final Function<SecretsClient, T> f_function;
    }
