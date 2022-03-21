/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.testing;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A no-op implementation of {@link AbstractAuthenticationDetailsProvider}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class NullAuthenticationDetailsProvider
        implements BasicAuthenticationDetailsProvider
    {
    @Override
    public String getKeyId()
        {
        return "";
        }

    @Override
    public InputStream getPrivateKey()
        {
        return new ByteArrayInputStream(new byte[0]);
        }

    @Override
    public String getPassPhrase()
        {
        return "";
        }

    @Override
    public char[] getPassphraseCharacters()
        {
        return new char[0];
        }

    /**
     * A singleton instance of {@link NullAuthenticationDetailsProvider}.
     */
    public static final NullAuthenticationDetailsProvider INSTANCE = new NullAuthenticationDetailsProvider();
    }
