/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.util;

import com.oracle.bmc.secrets.SecretsClient;

import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;

import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;

import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Base64;
import java.util.Objects;

/**
 * A class that provides data read from the OCI Secret Service.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class SecretsFetcher
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link SecretsFetcher}.
     *
     * @param client  the OCI secret srvice client
     */
    public SecretsFetcher(SecretsClient client)
        {
        f_client = Objects.requireNonNull(client);
        }

    // ----- SecretsFetcher methods -----------------------------------------

    public InputStream get(String sId)
        {
        GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest.builder()
                .secretId(sId)
                .build();

        GetSecretBundleResponse response = f_client.getSecretBundle(getSecretBundleRequest);
        Base64SecretBundleContentDetails content = (Base64SecretBundleContentDetails) response
                .getSecretBundle()
                .getSecretBundleContent();

        byte[] ab = f_decoder.decode(content.getContent());

        return new ByteArrayInputStream(ab);
        }

    // ----- helper methods -------------------------------------------------

    /**
     * Returns the {@link SecretsClient} used by this provider.
     *
     * @return the {@link SecretsClient} used by this provider
     */
    public SecretsClient getClient()
        {
        return f_client;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The Secret Service client.
     */
    private final SecretsClient f_client;

    /**
     * A Base64 decoder to decode data from the secret service.
     */
    private final Base64.Decoder f_decoder = Base64.getDecoder();
    }
