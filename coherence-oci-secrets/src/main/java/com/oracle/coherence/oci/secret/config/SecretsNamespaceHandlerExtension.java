/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.secret.config;

import com.oracle.coherence.oci.config.NamespaceHandlerExtension;
import com.oracle.coherence.oci.config.OCINamespaceHandler;
import com.oracle.coherence.oci.config.SimpleBuilderProcessor;

import com.tangosol.coherence.config.xml.processor.InstanceProcessor;

/**
 * A {@link NamespaceHandlerExtension} that adds extensions applicable to
 * the OCI Secrets Service.
 * <p>
 * This class will be loaded by the {@link OCINamespaceHandler} class using
 * the {@link java.util.ServiceLoader}.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class SecretsNamespaceHandlerExtension
        implements NamespaceHandlerExtension
    {
    // ----- NamespaceHandlerExtension methods ------------------------------

    @Override
    public void extend(OCINamespaceHandler handler)
        {
        handler.registerProcessor(ELEMENT_CERT_LOADER, new SimpleBuilderProcessor<>(SecretsCertificateLoaderBuilder::new));
        handler.registerProcessor(ELEMENT_KEY_LOADER, new SimpleBuilderProcessor<>(SecretsPrivateKeyLoaderBuilder::new));
        handler.registerProcessor(ELEMENT_KEY_STORE_LOADER, new SimpleBuilderProcessor<>(SecretsKeyStoreLoaderBuilder::new));
        handler.registerProcessor(ELEMENT_PWD_PROVIDER, new SimpleBuilderProcessor<>(SecretsPasswordProviderBuilder::new));
        handler.registerProcessor(ELEMENT_SECRETS_CLIENT, new InstanceProcessor());
        handler.registerProcessor(ELEMENT_VAULTS_CLIENT, new InstanceProcessor());
        }

    // ----- constants ------------------------------------------------------

    /**
     * The name of the element containing the id of a secret.
     */
    public static final String ELEMENT_SECRET_ID = "secret-id";

    /**
     * The name of the element containing the name of a secret.
     */
    public static final String ELEMENT_SECRET_NAME = "secret-name";

    /**
     * The name of the element containing the {@link com.oracle.bmc.secrets.SecretsClient} builder.
     */
    public static final String ELEMENT_SECRETS_CLIENT = "secrets-client";

    /**
     * The name of the element containing the {@link com.oracle.bmc.vault.VaultsClient} builder.
     */
    public static final String ELEMENT_VAULTS_CLIENT = "vaults-client";

    /**
     * The name of the XML element containing the {@link com.oracle.coherence.oci.secret.util.SecretsPasswordProvider} configuration.
     */
    public static final String ELEMENT_PWD_PROVIDER = "secrets-password-provider";

    /**
     * The name of the Secrets private key loader element.
     */
    public static final String ELEMENT_KEY_LOADER = "secrets-key";

    /**
     * The name of the Secrets private cert loader element.
     */
    public static final String ELEMENT_CERT_LOADER = "secrets-cert";

    /**
     * The name of the Secrets private KeyStore loader element.
     */
    public static final String ELEMENT_KEY_STORE_LOADER = "secrets-key-store";
    }
