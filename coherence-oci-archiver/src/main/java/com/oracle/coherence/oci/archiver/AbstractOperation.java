/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import java.util.concurrent.Callable;
import com.oracle.coherence.persistence.PersistenceManager;
import com.tangosol.io.ReadBuffer;

/**
 * Abstract implementation of an operation.
 */
public abstract class AbstractOperation
        implements Callable<Void>
    {

    // ----- constructors ---------------------------------------------------

    /**
     * Construct an {@link AbstractOperation}.
     *
     * @param archiver  {@link ObjectStorageSnapshotArchiver} being used
     * @param mgr       {@link PersistenceManager} that is managed the snapshot
     * @param sSnapshot snapshot being operated on
     */
    public AbstractOperation(ObjectStorageSnapshotArchiver archiver, PersistenceManager<ReadBuffer> mgr, String sSnapshot)
        {
        f_archiver = archiver;
        f_mgr = mgr;
        f_sSnapshot = sSnapshot;
        }

    // ----- data members ---------------------------------------------------

    /**
     * {@link ObjectStorageSnapshotArchiver} for this operation.
     */
    protected final ObjectStorageSnapshotArchiver  f_archiver;

    /**
     * {@link PersistenceManager} for this operation.
     */
    protected final PersistenceManager<ReadBuffer> f_mgr;

    /**
     * Snapshot for this operation.
     */
    protected final String f_sSnapshot;
    }
