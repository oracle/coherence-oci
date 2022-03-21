/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.testing;

import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;

import com.oracle.coherence.common.base.Exceptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A mock {@link BasicAuthenticationDetailsProvider}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class BasicAuthenticationStub
        implements BasicAuthenticationDetailsProvider
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link BasicAuthenticationStub}.
     *
     * @param sId    an identifier for this stub
     * @param sFile  the file containing the PEM encoded key
     */
    public BasicAuthenticationStub(String sId, String sFile)
        {
        this(sId, new File(sFile), null);
        }

    /**
     * Create a {@link BasicAuthenticationStub}.
     *
     * @param sId       an identifier for this stub
     * @param file      the {@link File} containing the PEM encoded key
     * @param sKeyPass  the optional private key pass phrase
     */
    public BasicAuthenticationStub(String sId, File file, String sKeyPass)
        {
        f_sId       = sId;
        f_filePEM   = file;
        f_acKeyPass = sKeyPass == null ? new char[0] : sKeyPass.toCharArray();
        }

    // ----- BasicAuthenticationDetailsProvider methods ---------------------

    @Override
    public String getKeyId()
        {
        return f_sId;
        }

    @Override
    public InputStream getPrivateKey()
        {
        try
            {
            return new FileInputStream(f_filePEM);
            }
        catch (FileNotFoundException e)
            {
            throw Exceptions.ensureRuntimeException(e);
            }
        }

    @Override
    public String getPassPhrase()
        {
        return new String(f_acKeyPass);
        }

    @Override
    public char[] getPassphraseCharacters()
        {
        return f_acKeyPass;
        }

    // ----- constants ------------------------------------------------------

    public static final String PROP_PEM_FILE = "test.oci.auth.file";

    public static final String PROP_PASS_PHRASE = "test.oci.auth.pass";

    // ----- data members ---------------------------------------------------

    /**
     * The identifier for this stub.
     */
    private final String f_sId;

    /**
     * The {@link File} containing the PEM encoded key.
     */
    private final File f_filePEM;

    /**
     * The private key pass phrase.
     */
    private final char[] f_acKeyPass;
    }
