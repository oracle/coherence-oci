/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.testing;

import com.oracle.bmc.OCID;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.vault.VaultsClient;
import com.oracle.bmc.vault.model.Base64SecretContentDetails;
import com.oracle.bmc.vault.model.CreateSecretDetails;
import com.oracle.bmc.vault.model.SecretContentDetails;
import com.oracle.bmc.vault.model.SecretSummary;
import com.oracle.bmc.vault.model.UpdateSecretDetails;
import com.oracle.bmc.vault.requests.CreateSecretRequest;
import com.oracle.bmc.vault.requests.ListSecretsRequest;
import com.oracle.bmc.vault.requests.UpdateSecretRequest;
import com.oracle.bmc.vault.responses.CreateSecretResponse;
import com.oracle.bmc.vault.responses.ListSecretsResponse;
import com.oracle.bmc.vault.responses.UpdateSecretResponse;
import com.oracle.coherence.common.base.Exceptions;
import com.oracle.coherence.common.base.Logger;
import com.oracle.coherence.common.base.Reads;
import com.oracle.coherence.oci.secret.util.SecretsFetcher;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsBlankString.blankOrNullString;

/**
 * Utilities for testing with secrets.
 *
 * @author Jonathan Knight  2022.03.17
 */
public class SecretsUtils
    {
    // ----- constructors ---------------------------------------------------

    public SecretsUtils(AbstractAuthenticationDetailsProvider auth, String sRegion)
        {
        this(auth, Region.fromRegionId(sRegion));
        }

    public SecretsUtils(AbstractAuthenticationDetailsProvider auth, Region region)
        {
        m_region  = region;
        m_fetcher = new SecretsFetcher(auth);
        }

    // ----- SecretsUtils ---------------------------------------------------

    public byte[] getSecretData(String sId)
        {
        if (!OCID.isValid(sId))
            {
            throw new IllegalArgumentException("Invalid OCID argument: " + sId);
            }
        return m_fetcher.get(sId);
        }

    public byte[] getSecretData(String sName, String sCompartmentId)
        {
        if (!OCID.isValid(sCompartmentId))
            {
            throw new IllegalArgumentException("Invalid Compartment OCID argument: " + sCompartmentId);
            }
        return m_fetcher.get(sName, sCompartmentId);
        }

    public String findSecretOCID(String sCompartmentId, String sVaultId, String sSecretName)
        {
        if (!OCID.isValid(sCompartmentId))
            {
            throw new IllegalArgumentException("Invalid Compartment OCID argument: " + sCompartmentId);
            }

        if (!OCID.isValid(sVaultId))
            {
            throw new IllegalArgumentException("Invalid Vault OCID argument: " + sVaultId);
            }

        assertThat(sSecretName, is(not(blankOrNullString())));

        Logger.info(String.format("Ensuring secret %s compartment=%s vault=%s", sSecretName, sCompartmentId, sVaultId));

        VaultsClient vaultsClient = m_fetcher.getVaultsClient();

        ListSecretsRequest listSecretsRequest = ListSecretsRequest.builder()
                .compartmentId(sCompartmentId)
                .name(sSecretName)
                .build();

        ListSecretsResponse listSecretsResponse = vaultsClient.listSecrets(listSecretsRequest);
        List<SecretSummary> secretSummaries = listSecretsResponse.getItems();

        return secretSummaries == null || secretSummaries.isEmpty()
                ? null
                : secretSummaries.get(0).getId();
        }

    public String ensureSecret(String sCompartmentId, String sVaultId, String sKeyId, String sSecretName, File file)
        {
        try
            {
            byte[] abData = Reads.read(file);
            return ensureSecret(sCompartmentId, sVaultId, sKeyId, sSecretName, abData);
            }
        catch (IOException e)
            {
            throw Exceptions.ensureRuntimeException(e);
            }
        }

    public String ensureSecret(String sCompartmentId, String sVaultId, String sKeyId, String sSecretName, byte[] abData)
        {
        if (!OCID.isValid(sCompartmentId))
            {
            throw new IllegalArgumentException("Invalid Compartment OCID argument: " + sCompartmentId);
            }

        if (!OCID.isValid(sVaultId))
            {
            throw new IllegalArgumentException("Invalid Vault OCID argument: " + sVaultId);
            }

        if (!OCID.isValid(sVaultId))
            {
            throw new IllegalArgumentException("Invalid Key OCID argument: " + sKeyId);
            }

        Logger.info(String.format("Ensuring secret %s compartment=%s vault=%s", sSecretName, sCompartmentId, sVaultId));

        String       sSecretdId   = findSecretOCID(sCompartmentId, sVaultId, sSecretName);
        VaultsClient vaultsClient = m_fetcher.getVaultsClient();
        String       sSecretData  = Base64.getEncoder().encodeToString(abData);

        SecretContentDetails content = Base64SecretContentDetails.builder()
                .content(sSecretData)
                .build();

        if (sSecretdId == null)
            {
            Logger.info(String.format("Creating secret %s compartment=%s vault=%s", sSecretName, sCompartmentId, sVaultId));
            CreateSecretDetails details = CreateSecretDetails.builder()
                    .secretName(sSecretName)
                    .vaultId(sVaultId)
                    .keyId(sKeyId)
                    .compartmentId(sCompartmentId)
                    .secretContent(content)
                    .description("Coherence testing")
                    .build();
            CreateSecretResponse createResponse = vaultsClient.createSecret(CreateSecretRequest.builder().createSecretDetails(details).build());
            sSecretdId = createResponse.getSecret().getId();
            Logger.info(String.format("Created secret %s compartment=%s vault=%s secretOCID=%s", sSecretName, sCompartmentId, sVaultId, sSecretdId));
            }
        else
            {
            Logger.info(String.format("Updating secret %s compartment=%s vault=%s", sSecretName, sCompartmentId, sVaultId));
            UpdateSecretDetails details = UpdateSecretDetails.builder()
                    .secretContent(content)
                    .build();

            UpdateSecretRequest request = UpdateSecretRequest.builder()
                    .secretId(sSecretdId)
                    .updateSecretDetails(details)
                    .build();

            UpdateSecretResponse updateResponse = vaultsClient.updateSecret(request);
            sSecretdId = updateResponse.getSecret().getId();
            Logger.info(String.format("Updated secret %s compartment=%s vault=%s secretOCID=%s", sSecretName, sCompartmentId, sVaultId, sSecretdId));
            }

        return sSecretdId;
        }

    // ----- helper methods -------------------------------------------------


    // ----- data members ---------------------------------------------------

    private final Region m_region;

    private final SecretsFetcher m_fetcher;
    }
