/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import com.oracle.coherence.persistence.PersistenceManager;
import com.tangosol.io.FileHelper;
import com.tangosol.io.ReadBuffer;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.GuardSupport;
import com.tangosol.persistence.CachePersistenceHelper;
import com.tangosol.persistence.GUIDHelper;
import com.tangosol.util.Base;

/**
 * Implementation of an {@link AbstractOperation} to archive a store for the given snapshot.
 */
class ArchiveOperation
        extends AbstractOperation
    {

    // ----- constructors ---------------------------------------------------

    /**
     * Construct an {@link ArchiveOperation}.
     *
     * @param archiver  {@link ObjectStorageSnapshotArchiver} being used
     * @param mgr       {@link PersistenceManager} that is managed the snapshot
     * @param sSnapshot snapshot being operated on
     * @param sStore    the store to archive
     */
    public ArchiveOperation(ObjectStorageSnapshotArchiver archiver, PersistenceManager<ReadBuffer> mgr, String sSnapshot, String sStore)
        {
        super(archiver, mgr, sSnapshot);
        f_sStore = sStore;
        f_writerExecutor = archiver.getExecutorWriter();
        }

    @Override
    public Void call()
        {
        File   fileMetaTempDir = null;
        String sSnapshotDir    = ObjectStorageSnapshotArchiver.getSnapshotDirectory(f_archiver.getFulLPrefix(), f_sSnapshot);

        try
            {
            long ldtStart = Base.getSafeTimeMillis();

            CacheFactory.log("Archiving store " + f_sStore + " for snapshot " + f_sSnapshot, CacheFactory.LOG_QUIET);
            if (CachePersistenceHelper.isGlobalPartitioningSchemePID(GUIDHelper.getPartition(f_sStore)))
                {
                // Create a temporary directory to write archived snapshot metadata properties
                fileMetaTempDir = FileHelper.createTempDir();

                f_archiver.writeMetadataFile(fileMetaTempDir, f_mgr, f_sStore);

                Path pathMetadata = new File(fileMetaTempDir, CachePersistenceHelper.META_FILENAME).toPath();

                f_archiver.getObjectStorageManager().uploadFile(pathMetadata.toFile(),
                        sSnapshotDir + ObjectStorageSnapshotArchiver.SEP + CachePersistenceHelper.META_FILENAME);
               }

            PipedInputStream pis = new PipedInputStream();

            Future<?> writerTask = f_writerExecutor.submit(()->
                {
                try (PipedOutputStream pos = new PipedOutputStream(pis))
                    {
                    f_mgr.write(f_sStore, pos);
                    }
                catch (Exception e)
                    {
                    throw CachePersistenceHelper.ensurePersistenceException(e, "Unable to write store " + f_sStore);
                    }
                });

            f_archiver.getObjectStorageManager().uploadFile(pis, sSnapshotDir + ObjectStorageSnapshotArchiver.SEP + f_sStore);
            writerTask.get();

            // issue heartbeat as operations could take a relatively long time
            GuardSupport.heartbeat();
            f_archiver.registerArchive(Base.getSafeTimeMillis() - ldtStart);
            }
        catch (Exception e)
            {
            f_archiver.registerArchiveFailed();
            throw CachePersistenceHelper.ensurePersistenceException(e, "Error in archiveInternal()");
            }
        finally
            {
            if (fileMetaTempDir != null)
                {
                FileHelper.deleteDirSilent(fileMetaTempDir);
                }
            }

        return null;
        }

    // ----- data members ---------------------------------------------------

    private final String f_sStore;
    private final ExecutorService f_writerExecutor;
    }
