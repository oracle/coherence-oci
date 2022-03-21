/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

/**
 * A class that can add extensions to an {@link OCINamespaceHandler}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public interface NamespaceHandlerExtension
    {
    /**
     * Add additional functionality to the specified {@link OCINamespaceHandler}.
     *
     * @param handler  the {@link OCINamespaceHandler} to extend
     */
    void extend(OCINamespaceHandler handler);
    }
