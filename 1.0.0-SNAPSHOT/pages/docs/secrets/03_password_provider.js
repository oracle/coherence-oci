<doc-view>

<h2 id="_store_passwords_in_secrets">Store Passwords in Secrets</h2>
<div class="section">
<p>Some Coherence configuration elements allow passwords to be specified using a custom <code>PasswordProvider</code>, for example when providing TLS key credentials. The Coherence OCI secrets service integrations provide a custom <code>PasswordProvider</code> that obtains a password from a secret.</p>

<p>The Coherence OCI XML configuration extensions provide a <code>secrets-password-provider</code> element to configure the secret password provider.
The Coherence OCI custom XML namespace must be added to the Coherence operational configuration file or cache configuration file that the password provider is being configured in, as described in the <router-link to="/docs/about/02_getting_started">Getting Started</router-link> guide.</p>

<p>With the OCI namespace added, a secret password provider can be used anywhere the Coherence XML uses a <code>&lt;password-provider&gt;</code> element, by adding the <code>&lt;oci:secrets-password-provider&gt;</code> element as a child of the <code>&lt;password-provider&gt;</code> element.</p>

<p>In the example below, the password will come from a secret with the OCID <code>ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a</code>.</p>

<markup
lang="xml"

>&lt;password-provider id="secret-password"&gt;
  &lt;oci:secrets-password-provider&gt;
    ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a
  &lt;/oci:secrets-password-provider&gt;
&lt;/password-provider&gt;</markup>

<p>Alternatively, instead of specifying the OCID of the secret, a combination of a secret name and a Compartment OCID can be used.
In the example below the password will be read from the secret named <code>foo</code> in the compartment with the OCID <code>ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a</code>.</p>

<markup
lang="xml"

>&lt;password-provider id="secret-password"&gt;
  &lt;oci:secrets-password-provider&gt;
    &lt;oci:secret-name&gt;foo&lt;/oci:secret-name&gt;
    &lt;oci:compartment-id&gt;
      ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a
    &lt;/oci:compartment-id&gt;
  &lt;/oci:secrets-password-provider&gt;
&lt;/password-provider&gt;</markup>

<p>A password provider can be added to the Coherence operational configuration file, as shown below:</p>

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
      &lt;password-provider id="secret-one"&gt;
        &lt;oci:secrets-password-provider&gt;
          ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a
        &lt;/oci:secrets-password-provider&gt;
      &lt;/password-provider&gt;

      &lt;password-provider id="secret-two"&gt;
        &lt;oci:secrets-password-provider&gt;
          &lt;oci:secret-name&gt;my-secret&lt;/oci:secret-name&gt;
          &lt;oci:compartment-id&gt;
            ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmenttxgxkxku5zhmpu35j67hdxvxpglijz4a
          &lt;/oci:compartment-id&gt;
        &lt;/oci:secrets-password-provider&gt;
      &lt;/password-provider&gt;
    &lt;/password-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

<p>The <code>secret-one</code> password provider will retrieve the password from the secret with the OCID <code>ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a</code></p>

<p>The <code>secret-two</code> password provider will retrieve the password from the secret named <code>my-secret</code> in the OCI compartment with the OCID <code>ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmenttxgxkxku5zhmpu35j67hdxvxpglijz4a</code>.</p>

</div>
</doc-view>