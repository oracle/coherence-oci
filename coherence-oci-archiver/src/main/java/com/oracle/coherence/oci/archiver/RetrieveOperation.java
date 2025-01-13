/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import java.io.InputStream;
import com.oracle.coherence.persistence.PersistenceManager;
import com.tangosol.io.ReadBuffer;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.GuardSupport;
import com.tangosol.persistence.CachePersistenceHelper;
import com.tangosol.persistence.GUIDHelper;
import com.tangosol.util.Base;

/**
 * Implementation of an {@link AbstractOperation} to retrieve a store for the given snapshot.
 */
class RetrieveOperation
        extends AbstractOperation
    {

    // ----- constructors ---------------------------------------------------

    public RetrieveOperation(ObjectStorageSnapshotArchiver archiver, PersistenceManager<ReadBuffer> mgr, String sSnapshot, String sStore)
        {
        super(archiver, mgr, sSnapshot);
        f_sStore = sStore;
        }

    @Override
    public Void call()
        {
        String sSnapshotDir = ObjectStorageSnapshotArchiver.getSnapshotDirectory(f_archiver.getFulLPrefix(), f_sSnapshot);

        try
            {
            long ldtStart = Base.getSafeTimeMillis();

            CacheFactory.log("Retrieving store " + f_sStore + " for snapshot " + f_sSnapshot, CacheFactory.LOG_QUIET);
            if (CachePersistenceHelper.isGlobalPartitioningSchemePID(GUIDHelper.getPartition(f_sStore)))
                {
                // validate that the metadata file exists for partition 0
                if (f_archiver.getMetadata(sSnapshotDir) == null)
                    {
                    throw new IllegalArgumentException("Cannot load properties file "
                                                       + CachePersistenceHelper.META_FILENAME + " for snapshot "
                                                       + f_sSnapshot);
                    }
                }

            InputStream is = f_archiver.getObjectStorageManager().getFileAsStream(
                    sSnapshotDir + ObjectStorageSnapshotArchiver.SEP + f_sStore);

            f_mgr.read(f_sStore, is);  // instruct the mgr to read the store from the stream

            is.close();
            
            // issue heartbeat as operations could take a relatively long time
            GuardSupport.heartbeat();

            f_archiver.registerRetrieve(Base.getSafeTimeMillis() - ldtStart);
            }
        catch (Exception e)
            {
            f_archiver.registerRetrieveFailed();
            throw CachePersistenceHelper.ensurePersistenceException(e, "Error in RRetrieveOperation");
            }
        return null;
        }
    
    // ----- data members ---------------------------------------------------

    private final String f_sStore;
    }
