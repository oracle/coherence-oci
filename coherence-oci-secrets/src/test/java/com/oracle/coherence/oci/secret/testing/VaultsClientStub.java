/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.testing;

import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.vault.VaultsClient;

import com.oracle.coherence.common.base.Exceptions;
import com.oracle.coherence.common.base.Reads;
import com.oracle.coherence.oci.testing.BasicAuthenticationStub;

import java.io.File;
import java.io.IOException;

/**
 * A mock {@link VaultsClient}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class VaultsClientStub
        extends VaultsClient
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link VaultsClientStub}.
     */
    public VaultsClientStub(File file)
        {
        this(new BasicAuthenticationStub("test", file, null));
        }

    /**
     * Create a {@link VaultsClientStub}.
     */
    public VaultsClientStub(String sFile)
        {
        this(new BasicAuthenticationStub("test", new File(sFile), null));
        }

    /**
     * Create a {@link VaultsClientStub}.
     *
     * @param auth  the {@link BasicAuthenticationDetailsProvider} to use
     */
    public VaultsClientStub(BasicAuthenticationDetailsProvider auth)
        {
        super(auth);
        f_auth = auth;
        }

    public BasicAuthenticationDetailsProvider getAuth()
        {
        return f_auth;
        }

    private byte[] readFile(File file)
        {
        try
            {
            return Reads.read(file);
            }
        catch (IOException e)
            {
            throw Exceptions.ensureRuntimeException(e);
            }
        }

    // ----- data members ---------------------------------------------------

    /**
     * The {@link BasicAuthenticationDetailsProvider} the client should use.
     */
    private final BasicAuthenticationDetailsProvider f_auth;
    }
