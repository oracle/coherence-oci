/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.testing;

import com.oracle.bedrock.runtime.concurrent.RemoteCallable;
import com.tangosol.net.Coherence;

/**
 * A test Coherence client.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class CoherenceClient
    {
    public static void main(String[] args) throws Exception
        {
        s_coherence = Coherence.client().start().join();

        synchronized (WAITER)
            {
            WAITER.wait();
            }
        }

    public static Coherence getCoherence()
        {
        return s_coherence;
        }

    // ----- inner class: IsRunning -----------------------------------------

    public static class IsRunning
            implements RemoteCallable<Boolean>
        {
        @Override
        public Boolean call()
            {
            Coherence coherence = CoherenceClient.getCoherence();
            return coherence != null && coherence.isStarted();
            }
        }

    // ----- data members ---------------------------------------------------

    public static final IsRunning IS_RUNNING = new IsRunning();

    private static volatile Coherence s_coherence;

    private static final Object WAITER = new Object();
    }
