<doc-view>

<h2 id="_oci_secrets_service_integration">OCI Secrets Service Integration</h2>
<div class="section">
<p>This module provides integrations between Coherence and the OCI Secrets Service.
To use the Coherence OCI Secrets module add the following dependency to your project.</p>

<markup
lang="xml"
title="Maven pom.xml"
>&lt;dependency&gt;
    &lt;groupId&gt;com.oracle.coherence.ce&lt;/groupId&gt;
    &lt;artifactId&gt;coherence&lt;/artifactId&gt;
    &lt;version&gt;${coherenceVersion}&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
    &lt;groupId&gt;com.oracle.coherence&lt;/groupId&gt;
    &lt;artifactId&gt;coherence-oci-secrets&lt;/artifactId&gt;
    &lt;version&gt;${coherenceOciVersion}&lt;/version&gt;
&lt;/dependency&gt;</markup>

<markup

title="Gradle build.gradle"
>implementation "com.oracle.coherence.ce:coherence:${coherenceVersion}"
implementation "com.oracle.coherence:coherence-oci-secrets:${coherenceOciVersion}"</markup>

<p>Replacing <code>${coherenceVersion}</code> with the version of Coherence being used and
replacing <code>${coherenceOciVersion}</code> with the version of the Coherence OCI module you are using.</p>

</div>

<h2 id="_enabling_the_oci_xml_configuration_namespace">Enabling the OCI XML Configuration Namespace</h2>
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

<h2 id="_configure_an_oci_secrets_passwordprovider">Configure An OCI Secrets PasswordProvider</h2>
<div class="section">
<p>Some Coherence configuration elements allow passwords to be specified using a custom <code>PasswordProvider</code>, for example when providing TLS key credentials. The Coherence OCI secrets service integrations provide a custom <code>PasswordProvider</code> that obtains a password from a secret.</p>

<p>With the OCI namespace added as described above, a secret password provider can be used anywhere the Coherence XML expects a <code>&lt;password-provider&gt;</code> by using XML like the snippet shown below:</p>

<markup
lang="xml"

>&lt;oci:secrets-password-provider&gt;
  &lt;oci:secret-id&gt;foo&lt;/oci:secret-id&gt;
&lt;/oci:secrets-password-provider&gt;</markup>

<p>In the above example, the password will come from a secret with the id <code>foo</code>.</p>

<p>For example, a password provider can be added to the operational override file.</p>

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

  &lt;cluster-config&gt;
    &lt;password-providers&gt;
      &lt;password-provider id="test-pass"&gt;
        &lt;oci:secrets-password-provider&gt;
          &lt;oci:secret-id&gt;foo&lt;/oci:secret-id&gt;
        &lt;/oci:secrets-password-provider&gt;
      &lt;/password-provider&gt;
    &lt;/password-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

</div>

<h2 id="_configure_coherence_tls_using_oci_secrets">Configure Coherence TLS using OCI Secrets</h2>
<div class="section">
<p>Since Coherence version 22.06 configuring TLS has become much more flexible and extendable.
Previously Coherence relied on Java KeysStore files to provide the keys and certs used to create an <code>SSLContext</code>.
From version 22.06 support for direct use of key and certificate files was added, as well as the ability to configure custom key and certificate loaders, so that they were not tied to the file system.</p>

<p>The Coherence OCI Secrets Service Integration extension adds custom key and certificate loaders that load data directly from secrets. If using password protected encrypted keys or keystores, these passwords can also be stored in secrets.</p>

</div>
</doc-view>