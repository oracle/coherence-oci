<doc-view>

<h2 id="_getting_started">Getting Started</h2>
<div class="section">
<p>Coherence OCI integrations use the OCI APIs to communicate with the various OCI services.
The OCI APIs require configuration and authentication to work correctly.</p>


<h3 id="_default_configuration">Default Configuration</h3>
<div class="section">
<p>The default mode of operation for Coherence OCI is to use the OCI APIs default configuration mechanism,
as described in the OCI <a id="" title="" target="_blank" href="https://docs.oracle.com/en-us/iaas/Content/API/Concepts/sdkconfig.htm">SDK and CLI Configuration</a>
documentation. No other additional Coherence configuration is required if the OCI configuration file is present in the default location.</p>


<h4 id="_specify_the_oci_configuration_file">Specify the OCI Configuration File</h4>
<div class="section">
<p>The default location of the OCI configuration file is <code>~/.oci/config</code>
This can be overridden by setting the <code>OCI_CONFIG_FILE</code> environment variable to point to a different configuration file.</p>

<p>The Coherence OCI modules also allow the OCI configuration file location to be overridden using
the <code>coherence.oci.config.file</code> system property, or <code>COHERENCE_OCI_CONFIG_FILE</code> environment variable.</p>

</div>

<h4 id="_specify_the_oci_profile">Specify the OCI Profile</h4>
<div class="section">
<p>The <a id="" title="" target="_blank" href="https://docs.oracle.com/en-us/iaas/Content/API/Concepts/sdkconfig.htm">OCI configuration file</a> typically contains a
<code>DEFAULT</code> profile, but may optionally contain other profiles. The OCI API only supports programmatically setting the profile to use. The Coherence OCI modules allows the profile to be overridden by setting the <code>coherence.oci.config.profile</code> system property, or <code>COHERENCE_OCI_CONFIG_PROFILE</code> environment variable.</p>

</div>
</div>

<h3 id="_xml_configuration">XML Configuration</h3>
<div class="section">
<p>When adding OCI extensions to a Coherence cache configuration file or operational configuration override file, the custom OCI namespace needs to be added to the root element.</p>

<p>In the cache configuration file, add the <code>xmlns:oci="class://com.oracle.coherence.oci.config.OCINamespaceHandler"</code> namespace declaration and add the schema location <code>class://com.oracle.coherence.oci.config.OCINamespaceHandler coherence-oci.xsd</code></p>

<markup
lang="xml"
title="coherence-cache-config.xml"
>&lt;?xml version="1.0"?&gt;
&lt;cache-config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xmlns="http://xmlns.oracle.com/coherence/coherence-cache-config"
              xmlns:oci="class://com.oracle.coherence.oci.config.OCINamespaceHandler"
              xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-cache-config
                                  coherence-cache-config.xsd
                               class://com.oracle.coherence.oci.config.OCINamespaceHandler
                               coherence-oci.xsd"&gt;
&lt;/cache-config&gt;</markup>

<p>In the operational override configuration file, add the <code>xmlns:oci="class://com.oracle.coherence.oci.config.OCINamespaceHandler"</code> namespace declaration and add the schema location <code>class://com.oracle.coherence.oci.config.OCINamespaceHandler coherence-oci.xsd</code></p>

<markup
lang="xml"
title="tangosol-coherence-override.xml"
>&lt;?xml version="1.0"?&gt;
&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xmlns:oci="class://com.oracle.coherence.oci.config.OCINamespaceHandler"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
                               coherence-operational-config.xsd
                               class://com.oracle.coherence.oci.config.OCINamespaceHandler
                               coherence-oci.xsd"&gt;

&lt;/coherence&gt;</markup>

<p>With the additional namespace added, OCI XML extensions can be added to the configuration files by prefixing the OCI XML elements with <code>oci:</code></p>

</div>
</div>
</doc-view>