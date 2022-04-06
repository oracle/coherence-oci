/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.util;

import com.oracle.bmc.OCID;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import com.oracle.bmc.secrets.SecretsClient;

import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;

import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;

import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;

import com.oracle.bmc.vault.VaultsClient;

import com.oracle.bmc.vault.model.SecretSummary;

import com.oracle.bmc.vault.requests.ListSecretsRequest;

import com.oracle.bmc.vault.responses.ListSecretsResponse;

import com.oracle.coherence.common.base.Logger;

import com.oracle.coherence.oci.config.AuthenticationBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

/**
 * A class that provides data read from the OCI Secret Service.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class SecretsFetcher
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link SecretsFetcher}.
     */
    public SecretsFetcher()
        {
        this(null);
        }

    /**
     * Create a {@link SecretsFetcher}.
     *
     * @param auth  the OCI {@link AbstractAuthenticationDetailsProvider authentication provider}
     */
    public SecretsFetcher(AbstractAuthenticationDetailsProvider auth)
        {
        this(auth, null, null);
        }

    /**
     * Create a {@link SecretsFetcher}.
     *
     * @param secretsClient  the OCI {@link SecretsClient Secrets service client}
     * @param vaultsClient   the OCI {@link VaultsClient Vault service client}
     */
    public SecretsFetcher(SecretsClient secretsClient, VaultsClient vaultsClient)
        {
        this(null, secretsClient, vaultsClient);
        }


    /**
     * Create a {@link SecretsFetcher}.
     *
     * @param auth           the OCI {@link AbstractAuthenticationDetailsProvider authentication provider}
     * @param secretsClient  the OCI {@link SecretsClient Secrets service client}
     * @param vaultsClient   the OCI {@link VaultsClient Vault service client}
     */
    public SecretsFetcher(AbstractAuthenticationDetailsProvider auth, SecretsClient secretsClient, VaultsClient vaultsClient)
        {
        if (secretsClient == null)
            {
            if (auth == null)
                {
                auth = AuthenticationBuilder.INSTANCE.realize();
                }
            f_secretsClient = SecretsClient.builder().build(auth);
            }
        else
            {
            f_secretsClient = secretsClient;
            }

        if (vaultsClient == null)
            {
            if (auth == null)
                {
                auth = AuthenticationBuilder.INSTANCE.realize();
                }
            f_vaultsClient = VaultsClient.builder().build(auth);
            }
        else
            {
            f_vaultsClient = vaultsClient;
            }

        f_auth = auth;
        }

    // ----- SecretsFetcher methods -----------------------------------------

    public byte[] get(String sId)
        {
        Logger.log("Fetching OCI secret: " + sId, 9);
        GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest.builder()
                .secretId(sId)
                .build();

        GetSecretBundleResponse          response = f_secretsClient.getSecretBundle(getSecretBundleRequest);
        Base64SecretBundleContentDetails content  = (Base64SecretBundleContentDetails) response
                .getSecretBundle()
                .getSecretBundleContent();

        byte[] abData = f_decoder.decode(content.getContent());
        Logger.log("Fetched OCI secret: " + sId + " read " + abData.length + " bytes", 9);
        return abData;
        }

    public byte[] get(String sName, String sCompartmentId)
        {
        Logger.log("Fetching OCI secret: name=" + sName + " compartment=" + sCompartmentId, 9);

        ListSecretsRequest listSecretsRequest = ListSecretsRequest.builder()
                .compartmentId(sCompartmentId)
                .name(sName)
                .lifecycleState(SecretSummary.LifecycleState.Active)
                .sortOrder(ListSecretsRequest.SortOrder.Desc)
                .build();

        ListSecretsResponse listResponse = f_vaultsClient.listSecrets(listSecretsRequest);
        List<SecretSummary> listSummary  = listResponse.getItems();

        if (listSummary.isEmpty())
            {
            throw new IllegalStateException("Cannot find secret named " + sName
                    + " in compartment " + sCompartmentId);
            }

        SecretSummary summary = listSummary.get(0);

        return get(summary.getId());
        }

    /**
     * Fetch the specified secret.
     *
     * @param sSecret           the secret OCID or name
     * @param sCompartmentOCID  an optional compartment OCID required if the {@code sSecret} parameter is a name
     *
     * @return the secret data as an {@link InputStream} or {@link null} if there is no secret data
     */
    public InputStream getSecret(String sSecret, String sCompartmentOCID)
        {
        byte[] abData;

        if (OCID.isValid(sSecret))
            {
            abData = get(sSecret);
            }
        else
            {
            // sSecret is not an OCID, so assume it is a name
            if (sCompartmentOCID == null || sCompartmentOCID.isEmpty())
                {
                throw new IllegalArgumentException("Secret id is not an OCID, "
                        + "but no Compartment Id was set so it cannot be looked up as a secret name. "
                        + "id=" + sSecret);
                }
            if (OCID.isValid(sCompartmentOCID))
                {
                throw new IllegalArgumentException("Secret id \"" + sSecret + "\" is not an OCID, "
                        + "and the Compartment Id is also not a valid OCID \"" + sCompartmentOCID + "\"");
                }
            abData = get(sSecret, sCompartmentOCID);
            }

        return abData == null ? null : new ByteArrayInputStream(abData);
        }

    // ----- helper methods -------------------------------------------------

    /**
     * Returns the {@link SecretsClient} used by this fetcher.
     *
     * @return the {@link SecretsClient} used by this fetcher
     */
    public SecretsClient getSecretsClient()
        {
        return f_secretsClient;
        }

    /**
     * Returns the {@link VaultsClient} used by this fetcher.
     *
     * @return the {@link VaultsClient} used by this fetcher
     */
    public VaultsClient getVaultsClient()
        {
        return f_vaultsClient;
        }

    /**
     * Returns the {@link AbstractAuthenticationDetailsProvider} used by this fetcher.
     *
     * @return the {@link AbstractAuthenticationDetailsProvider} used by this fetcher
     */
    public AbstractAuthenticationDetailsProvider getAuth()
        {
        return f_auth;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The OCI authentication.
     */
    private final AbstractAuthenticationDetailsProvider f_auth;

    /**
     * The Secret Service client.
     */
    private final SecretsClient f_secretsClient;

    /**
     * The Vault Service client.
     */
    private final VaultsClient f_vaultsClient;

    /**
     * A Base64 decoder to decode data from the secret service.
     */
    private final Base64.Decoder f_decoder = Base64.getDecoder();
    }
