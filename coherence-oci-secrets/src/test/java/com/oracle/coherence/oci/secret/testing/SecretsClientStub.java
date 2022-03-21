/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.testing;

import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;

import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.model.SecretBundle;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import com.oracle.coherence.common.base.Exceptions;
import com.oracle.coherence.common.base.Reads;
import com.oracle.coherence.oci.testing.BasicAuthenticationStub;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A mock {@link SecretsClient}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class SecretsClientStub
        extends SecretsClient
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link SecretsClientStub}.
     */
    public SecretsClientStub(File file)
        {
        this(new BasicAuthenticationStub("test", file, null));
        }

    /**
     * Create a {@link SecretsClientStub}.
     */
    public SecretsClientStub(String sFile)
        {
        this(new BasicAuthenticationStub("test", new File(sFile), null));
        }

    /**
     * Create a {@link SecretsClientStub}.
     *
     * @param auth  the {@link BasicAuthenticationDetailsProvider} to use
     */
    public SecretsClientStub(BasicAuthenticationDetailsProvider auth)
        {
        super(auth);
        f_auth = auth;
        }

    // ----- SecretsClientStub methods --------------------------------------

    /**
     * Add a secret to the stub.
     *
     * @param sName     the name of the secret
     * @param supplier  the {@link Supplier} that will be called to provide the secret's data
     */
    public void addSecret(String sName, Supplier<byte[]> supplier)
        {
        f_mapSecrets.put(sName, supplier);
        }

    /**
     * Add a secret to the stub.
     *
     * @param sName   the name of the secret
     * @param abData  the secret data
     */
    public void addSecret(String sName, byte[] abData)
        {
        f_mapSecrets.put(sName, () -> abData);
        }

    /**
     * Add a secret to the stub.
     *
     * @param sName  the name of the secret
     * @param sData  the secret data
     */
    public void addSecret(String sName, String sData)
        {
        f_mapSecrets.put(sName, () -> sData.getBytes(StandardCharsets.UTF_8));
        }

    /**
     * Add a secret to the stub.
     *
     * @param sName  the name of the secret
     * @param file   the {@link File} containing the secret data
     */
    public void addSecret(String sName, File file)
        {
        f_mapSecrets.put(sName, () -> readFile(file));
        }

    // ----- SecretsClient methods ------------------------------------------

    @Override
    public GetSecretBundleResponse getSecretBundle(GetSecretBundleRequest request)
        {
        Supplier<byte[]> supplier = f_mapSecrets.get(request.getSecretId());
        if (supplier == null)
            {
            throw new IllegalArgumentException("Invalid secret name: " + request.getSecretId());
            }

        Base64SecretBundleContentDetails content = Base64SecretBundleContentDetails.builder()
                .content(Base64.getEncoder().encodeToString(supplier.get()))
                .build();

        SecretBundle bundle = SecretBundle.builder()
                .secretBundleContent(content)
                .build();

        return GetSecretBundleResponse.builder()
                .secretBundle(bundle)
                .build();
        }

    // ----- helper methods -------------------------------------------------

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

    /**
     * A {@link Map} of secrets this client stub can provide.
     */
    private final Map<String, Supplier<byte[]>> f_mapSecrets = new HashMap<>();
    }
