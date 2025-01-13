/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import com.oracle.coherence.persistence.PersistenceManager;

import com.tangosol.io.ReadBuffer;

import com.tangosol.net.GuardSupport;

import com.tangosol.persistence.CachePersistenceHelper;

import com.tangosol.util.Base;

/**
 * Implementation of an {@link AbstractOperation} to delete a store for the given snapshot.
 */
class DeleteOperation
        extends AbstractOperation
    {

    // ----- constructors ---------------------------------------------------

    public DeleteOperation(ObjectStorageSnapshotArchiver archiver, PersistenceManager<ReadBuffer> mgr, String sSnapshot, String sStore)
        {
        super(archiver, mgr, sSnapshot);
        f_sStore = sStore;
        }

    // ---- Callable interface ----------------------------------------------
           
    @Override
    public Void call()
        {
        try
            {
            long ldtStart = Base.getSafeTimeMillis();
            
            f_archiver.getObjectStorageManager().deleteFile(f_sStore);

            // issue heartbeat as operations could take a relatively long time
            GuardSupport.heartbeat();

            f_archiver.registerDelete(Base.getSafeTimeMillis() - ldtStart);
            }
        catch (Exception e)
            {
            f_archiver.registerDeleteFailed();
            throw CachePersistenceHelper.ensurePersistenceException(e, "Error in DeleteFileOperation");
            }

        return null;
        }

    // ----- data members ---------------------------------------------------

    private final String f_sStore;
    }
