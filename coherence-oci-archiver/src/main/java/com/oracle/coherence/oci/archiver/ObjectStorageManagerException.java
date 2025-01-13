/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

/**
 * An {@link Exception} caused by an ObjectStorage operation.
 */
public class ObjectStorageManagerException
        extends Exception
    {

    // ----- constructors ---------------------------------------------------

    /**
     * Construct a {@link ObjectStorageManagerException} with a message.
     *
     * @param message message indicating the error
     */
    public ObjectStorageManagerException(String message)
        {
        super(message);
        }

    /**
     * Construct a {@link ObjectStorageManagerException} with a message and {@link Throwable}.
     *
     * @param message message indicating the error
     * @param cause   the cause of the error
     */
    public ObjectStorageManagerException(String message, Throwable cause)
        {
        super(message, cause);
        }
    }
