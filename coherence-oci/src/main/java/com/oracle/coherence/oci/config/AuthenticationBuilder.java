/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.config;

import com.oracle.bmc.ConfigFileReader;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;

import com.oracle.bmc.util.internal.FileUtils;

import com.oracle.coherence.oci.CoherenceOCI;

import com.tangosol.coherence.config.Config;
import com.tangosol.coherence.config.ParameterList;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;

import com.tangosol.config.ConfigurationException;

import com.tangosol.config.annotation.Injectable;
import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.NullParameterResolver;
import com.tangosol.config.expression.ParameterResolver;

import java.io.IOException;

/**
 * A {@link ParameterizedBuilder} that builds an {@link AbstractAuthenticationDetailsProvider}
 * instances based on reading an OCI configuration file.
 *
 * @author Jonathan Knight  2022.01.25
 */
public class AuthenticationBuilder
        implements ParameterizedBuilder<AbstractAuthenticationDetailsProvider>
    {
    /**
     * Realize a {@link AbstractAuthenticationDetailsProvider} instance.
     *
     * @return a {@link AbstractAuthenticationDetailsProvider} instance
     */
    public AbstractAuthenticationDetailsProvider realize()
        {
        return realize(new NullParameterResolver(), null, null);
        }

    @Override
    public AbstractAuthenticationDetailsProvider realize(ParameterResolver resolver, ClassLoader loader, ParameterList parameterList)
        {
        String sFileName    = m_exFileName == null ? null : m_exFileName.evaluate(resolver);
        String sProfileName = m_exProfileName == null ? null : m_exProfileName.evaluate(resolver);

        if (isNullOrEmpty(sFileName))
            {
            // no config file was configured, try the default property/env-var
            sFileName = Config.getProperty(CoherenceOCI.PROP_OCI_CONFIG_FILE);
            }

        if (isNullOrEmpty(sProfileName))
            {
            // no config profile was configured, try the default property/env-var
            sProfileName = Config.getProperty(CoherenceOCI.PROP_OCI_CONFIG_PROFILE);
            }

        if (isNullOrEmpty(sFileName) && isNullOrEmpty(sProfileName))
            {
            // Neither a file name, nor profile are present, use the default
            return new ConfigFileAuthenticationDetailsProvider(parseDefault());
            }

        if (isNullOrEmpty(sFileName))
            {
            // Just a profile name has been specified
            return new ConfigFileAuthenticationDetailsProvider(parseDefault(sProfileName));
            }

        if (isNullOrEmpty(sProfileName))
            {
            // Just a config file name has been specified
            return new ConfigFileAuthenticationDetailsProvider(parse(sFileName));
            }

        // Both a config file name and profile name have been specified
        return new ConfigFileAuthenticationDetailsProvider(parse(sFileName, sProfileName));
        }

    @Injectable("file-name")
    public void setFileName(Expression<String> exFileName)
        {
        m_exFileName = exFileName;
        }

    /**
     * Return the OCI configuration file.
     *
     * @return the OCI configuration file
     */
    protected Expression<String> getFileName()
        {
        return m_exFileName;
        }

    @Injectable("profile-name")
    public void setProfileName(Expression<String> exProfileName)
        {
        m_exProfileName = exProfileName;
        }

    /**
     * Return the OCI profile name.
     *
     * @return the OCI profile name
     */
    protected Expression<String> getProfileName()
        {
        return m_exProfileName;
        }

    // ----- helper methods -------------------------------------------------

    private ConfigFileReader.ConfigFile parseDefault() throws ConfigurationException
        {
        try
            {
            return ConfigFileReader.parseDefault();
            }
        catch (IOException e)
            {
            throw new ConfigurationException(String.format(ERROR_BAD_CONFIG, FileUtils.expandUserHome("~/.oci/config")), ERROR_ADVICE, e);
            }
        }

    private ConfigFileReader.ConfigFile parseDefault(String sProfileName) throws ConfigurationException
        {
        try
            {
            return ConfigFileReader.parseDefault(sProfileName);
            }
        catch (IOException e)
            {
            throw new ConfigurationException(String.format(ERROR_BAD_CONFIG_AND_PROFILE, FileUtils.expandUserHome("~/.oci/config"), sProfileName), ERROR_ADVICE, e);
            }
        }

    private ConfigFileReader.ConfigFile parse(String sFileName) throws ConfigurationException
        {
        try
            {
            return ConfigFileReader.parse(sFileName);
            }
        catch (IOException e)
            {
            throw new ConfigurationException(String.format(ERROR_BAD_CONFIG, sFileName), ERROR_ADVICE, e);
            }
        }

    private ConfigFileReader.ConfigFile parse(String sFileName, String sProfileName) throws ConfigurationException
        {
        try
            {
            return ConfigFileReader.parse(sFileName, sProfileName);
            }
        catch (IOException e)
            {
            throw new ConfigurationException(String.format(ERROR_BAD_CONFIG_AND_PROFILE, sFileName, sProfileName), ERROR_ADVICE, e);
            }
        }

    private static boolean isNullOrEmpty(String s)
        {
        return s == null || s.isEmpty();
        }

    // ----- constants ------------------------------------------------------

    /**
     * A singleton instance of the {@link AuthenticationBuilder}.
     */
    public static final AuthenticationBuilder INSTANCE = new AuthenticationBuilder();

    private static final String ERROR_BAD_CONFIG = "Could not parse the OCI configuration file %s";

    private static final String ERROR_BAD_CONFIG_AND_PROFILE = "Could not parse the OCI configuration file %s using profile %s";

    private static final String ERROR_ADVICE = "Ensure the OCI environment is correctly configured";

    // ----- data members ---------------------------------------------------

    /**
     * The {@link Expression} that resolves the OCI configuration file name.
     */
    private Expression<String> m_exFileName;

    /**
     * The {@link Expression} that resolves the OCI profile name.
     */
    private Expression<String> m_exProfileName;
    }
