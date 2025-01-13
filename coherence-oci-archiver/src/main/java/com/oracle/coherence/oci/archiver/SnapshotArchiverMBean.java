/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import com.tangosol.net.management.annotation.Description;
import com.tangosol.net.management.annotation.MetricsValue;

@Description("Provides Snapshot Archiver statistics.")
public interface SnapshotArchiverMBean {

    /**
     * Returns the number of archiver threads to use in parallel.
     * @return the number of archiver threads to use in parallel
     */
    @Description("The number of archiver threads to use in parallel.")
    @MetricsValue
    long getArchiverThreadCount();

    /**
     * Returns the last number of stores archived.
     * @return the last number of stores archived
     */
    @Description("Last number of stores archived.")
    @MetricsValue
    long getArchivedStoresCount();

    /**
     * Returns the total time archiving stores for this storage member.
     * @return the total time archiving stores for this storage member
     */
    @Description("Total time archiving stores for this storage member.")
    @MetricsValue
    long getArchivedStoresTotalMillis();

    /**
     * Returns the total number of archive store failures for this storage member.
     * @return the total number of archive store failures for this storage member
     */
    @Description("Total number of archive store failures for this storage member.")
    @MetricsValue
    long getArchivedStoresFailures();

    /**
     * Returns the average time to archive stores.
     * @return the average time to archive stores
     */
    @Description("Average time to archive stores.")
    @MetricsValue
    float getArchivedStoresAverageMillis();

    /**
     * Returns the maximum time to archive stores.
     * @return the maximum time to archive stores
     */
    @Description("Maximum time to archive stores.")
    @MetricsValue
    long getArchivedStoresMaxMillis();

    /**
     * Returns the last duration to archive all stores.
     * @return the last duration to archive all stores
     */
    @Description("Last duration to archive all stores.")
    @MetricsValue
    long getArchivedStoresLastDurationMillis();

    /**
     * Returns the last number of stores retrieved.
     * @return the last number of stores retrieved.
     */
    @Description("Last number of stores retrieved.")
    @MetricsValue
    long getRetrievedStoresCount();

    /**
     * Returns the total time retrieving stores.
     * @return the total time retrieving stores
     */
    @Description("Total time retrieving stores.")
    @MetricsValue
    long getRetrievedStoresTotalMillis();

    /**
     * Returns the total number of retrieve store failures.
     * @return the total number of retrieve store failures.
     */
    @Description("Total number of retrieve store failures.")
    @MetricsValue
    long getRetrievedStoresFailures();

    /**
     * Returns the average time to retrieve stores.
     * @return the average time to retrieve stores
     */
    @Description("Average time to retrieve stores.")
    @MetricsValue
    float getRetrievedStoresAverageMillis();

    /**
     * Returns the maximum time to retrieve stores.
     * @return the maximum time to retrieve stores.
     */
    @Description("Maximum time to retrieve stores.")
    @MetricsValue
    float getRetrievedStoresMaxMillis();

    /**
     * Returns the last duration to retrieve all stores.
     * @return the last duration to retrieve all stores.
     */
    @Description("Last duration to retrieve all stores.")
    @MetricsValue
    long getRetrievedStoresLastDurationMillis();

    /**
     * Returns the last number of stores deleted.
     * @return the last number of stores deleted.
     */
    @Description("Last number of stores deleted.")
    @MetricsValue
    long getDeletedStoresCount();

    /**
     * Returns the total time deleting stores.
     * @return the total time deleting stores.
     */
    @Description("Total time deleting stores.")
    @MetricsValue
    long getDeletedStoresTotalMillis();

    /**
     * Returns the total number of delete store failures.
     * @return the total number of delete store failures.
     */
    @Description("Total number of delete store failures.")
    @MetricsValue
    long getDeletedStoresFailures();

    /**
     * Returns the average time to delete stores.
     * @return the average time to delete stores.
     */
    @Description("Average time to delete stores.")
    @MetricsValue
    float getDeletedStoresAverageMillis();

    /**
     * Returns the maximum time to delete stores.
     * @return the maximum time to delete stores.
     */
    @Description("Maximum time to delete stores.")
    @MetricsValue
    float getDeletedStoresMaxMillis();

    /**
     * Returns the last duration to delete all stores.
     * @return the last duration to delete all stores.
     */
    @Description("Last duration to delete all stores.")
    @MetricsValue
    long getDeleteStoresLastDurationMillis();

    /**
     * Resets the statistics.
     */
    @Description("Reset the statistics.")
    public void resetArchiverStatistics();
    }
