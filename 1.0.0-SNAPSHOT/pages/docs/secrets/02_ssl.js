<doc-view>

<h2 id="_store_ssl_keys_certs_in_secrets">Store SSL Keys &amp; Certs in Secrets</h2>
<div class="section">
<p>Coherence can be configured to use SSL for secure communication, as described in the
<a id="" title="" target="_blank" href="https://docs.oracle.com/en/middleware/standalone/coherence/14.1.1.0/secure/using-ssl-secure-communication.html#GUID-90E20139-3945-4993-9048-7FBC93B243A3">Using SSL to Secure Communication</a> section of the Coherence documentation. By default, they keystores, keys or certificates used by Coherence are stored on the file system or classpath of the JVM. The Coherence OCI Secrets module allows keystores, private keys and certificates to be fetched from the OCI Secrets Service instead of requiring them to be pre-installed as files.</p>

<p>The Coherence APIs provide a way to extend Coherence SSL support to load keystores, private keys and certificates using custom loaders. The Coherence OCI Secrets module makes use of these APIs to add custom loaders to load data from the OCI Secret Service. The keystores, private keys and certificates are then never stored on the local file system.</p>

<div class="admonition note">
<p class="admonition-textlabel">Note</p>
<p ><p>The Coherence OCI Secrets module only fetches keystores, private keys and certificates,
it does not provide an API to upload this data to the OCI Secrets Service.</p>
</p>
</div>
<p>The following files can be retrieved from OCI secrets:</p>

<ul class="ulist">
<li>
<p><router-link to="#keys" @click.native="this.scrollFix('#keys')">Private Keys</router-link> from Secrets</p>

</li>
<li>
<p><router-link to="#certs" @click.native="this.scrollFix('#certs')">Certificates</router-link> from Secrets</p>

</li>
<li>
<p><router-link to="#keystores" @click.native="this.scrollFix('#keystores')">Java Keystores</router-link> from Secrets</p>

</li>
</ul>

<h3 id="_xml_configuration">XML Configuration</h3>
<div class="section">
<p>The Coherence OCI Secrets module provides a number of custom Coherence XML configuration extensions.
To use these additional XML elements, the Coherence OCI custom XML namespace must be added to the Coherence operational configuration file or cache configuration file, as described in the <router-link to="/docs/about/02_getting_started">Getting Started</router-link> guide.</p>

</div>
</div>

<h2 id="keys">Private Keys in Secrets</h2>
<div class="section">
<p>If a private key is stored in the OCI Secrets Service, Coherence SSL can be configured to use that private key.
When configuring SSL in Coherence, a <code>socket-provider</code> element is added to the operational configuration or cache configuration files.</p>

<p>For example, the XML below configures the socket provider to use a private key located on the file system at <code>file:/coherence/security/client.pem</code></p>

<markup
lang="xml"

>&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
              coherence-operational-config.xsd"&gt;

  &lt;cluster-config&gt;
    &lt;socket-providers&gt;
      &lt;socket-provider id="mySSLConfig"&gt;
         &lt;ssl&gt;
            &lt;protocol&gt;TLS&lt;/protocol&gt;
            &lt;identity-manager&gt;
              &lt;key&gt;file:/coherence/security/client.pem&lt;/key&gt;
              &lt;cert&gt;file:/coherence/security/client.cert&lt;/cert&gt;
            &lt;/identity-manager&gt;
            &lt;trust-manager&gt;
              &lt;cert&gt;file:/coherence/security/ca.cert&lt;/cert&gt;
            &lt;/trust-manager&gt;
         &lt;/ssl&gt;
      &lt;/socket-provider&gt;
    &lt;/socket-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

<p>Instead of using the file system, the private key in the <code>file:/coherence/security/client.pem</code> file can be uploaded to the OCI Secrets Service. The <code>secrets-key</code> XML element can then be used to retrieve the provate key from the OCI Secrets Service.</p>

<p>In the example below, the OCI custom namespace has been added to the configuration file. In the <code>&lt;identity-manager&gt;</code> section, instead of the <code>&lt;key&gt;</code> element, the <code>&lt;oci:secrets-key&gt;</code> element is used to specify the OCID of the secret that the private key is stored in.</p>

<markup
lang="xml"

>&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xmlns:oci="class://com.oracle.coherence.oci.config.OCINamespaceHandler"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
                coherence-operational-config.xsd
                class://com.oracle.coherence.oci.config.OCINamespaceHandler
                coherence-oci.xsd"&gt;

  &lt;cluster-config&gt;
    &lt;socket-providers&gt;
      &lt;socket-provider id="mySSLConfig"&gt;
         &lt;ssl&gt;
            &lt;protocol&gt;TLS&lt;/protocol&gt;
            &lt;identity-manager&gt;
              &lt;oci:secrets-key&gt;
                ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a
              &lt;/oci:secrets-key&gt;
              &lt;cert&gt;file:/coherence/security/client.cert&lt;/cert&gt;
            &lt;/identity-manager&gt;
            &lt;trust-manager&gt;
              &lt;cert&gt;file:/coherence/security/ca.cert&lt;/cert&gt;
            &lt;/trust-manager&gt;
         &lt;/ssl&gt;
      &lt;/socket-provider&gt;
    &lt;/socket-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>


<h3 id="_use_secret_names_instead_of_ocids">Use Secret Names Instead of OCIDs</h3>
<div class="section">
<p>The default behaviour of the <code>oci:secrets-key</code> element is that its content is the OCID of a secret containing a private key PEM file. Alternatively the name of the secret can be used by specifying the <code>&lt;oci:secret-name&gt;</code> element as a child of the &lt;oci:secret-key&gt;` element and additionally a compartment OCID using the <code>&lt;oci:compartment-id&gt;</code> element.</p>

<p>The example below will use the secret with the name <code>my-secret-pem</code> in the OCI compartment with OCID <code>ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a</code>.</p>

<markup
lang="xml"

>&lt;identity-manager&gt;
  &lt;oci:secrets-key&gt;
    &lt;oci:secret-name&gt;my-secret-pem&lt;/oci:secret-name&gt;
    &lt;oci:compartment-id&gt;
      ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a
    &lt;/oci:compartment-id&gt;
  &lt;/oci:secrets-key&gt;
  &lt;cert&gt;file:/coherence/security/client.cert&lt;/cert&gt;
&lt;/identity-manager&gt;</markup>

<p>If the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are set the <code>&lt;oci:compartment-id&gt;</code> element can be omitted, leaving just the <code>&lt;oci:secret-name&gt;</code> element:</p>

<markup
lang="xml"

>&lt;identity-manager&gt;
  &lt;oci:secrets-key&gt;
    &lt;oci:secret-name&gt;my-secret-pem&lt;/oci:secret-name&gt;
  &lt;/oci:secrets-key&gt;
  &lt;cert&gt;file:/coherence/security/client.cert&lt;/cert&gt;
&lt;/identity-manager&gt;</markup>

<p>Or alternatively if the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are set the secret name can be specified as the content of the <code>&lt;oci:secrets-key&gt;</code> element:</p>

<markup
lang="xml"

>&lt;identity-manager&gt;
  &lt;oci:secrets-key&gt;my-secret-pem&lt;/oci:secrets-key&gt;
  &lt;cert&gt;file:/coherence/security/client.cert&lt;/cert&gt;
&lt;/identity-manager&gt;</markup>

<p>If the <code>&lt;oci:secrets-key&gt;</code> element does not contain a valid OCID, it is assumed to be a secret name, in which case the Compartment OCID must be specified.
If the <code>&lt;oci:secret-name&gt;</code> element is used, or if the <code>&lt;oci:secrets-key&gt;</code> element is not an OCID and no Compartment OCID is specified or the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are not set, then an exception will be thrown.</p>

</div>
</div>

<h2 id="certs">Certificates in Secrets</h2>
<div class="section">
<p>If a certificate is stored in the OCI Secrets Service, Coherence SSL can be configured to use that certificate.
When configuring SSL in Coherence, a <code>socket-provider</code> element is added to the operational configuration or cache configuration files.</p>

<p>For example, the XML below configures the socket provider with an <code>&lt;identity-manager&gt;</code> that uses the certificate located on the file system at <code>file:/coherence/security/client.cert</code> and a <code>&lt;trust-manager&gt;</code> that uses the CA certificate located on the file system at <code>file:/coherence/security/ca.cert</code></p>

<markup
lang="xml"

>&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
              coherence-operational-config.xsd"&gt;

  &lt;cluster-config&gt;
    &lt;socket-providers&gt;
      &lt;socket-provider id="mySSLConfig"&gt;
         &lt;ssl&gt;
            &lt;protocol&gt;TLS&lt;/protocol&gt;
            &lt;identity-manager&gt;
              &lt;key&gt;file:/coherence/security/client.pem&lt;/key&gt;
              &lt;cert&gt;file:/coherence/security/client.cert&lt;/cert&gt;
            &lt;/identity-manager&gt;
            &lt;trust-manager&gt;
              &lt;cert&gt;file:/coherence/security/ca.cert&lt;/cert&gt;
            &lt;/trust-manager&gt;
         &lt;/ssl&gt;
      &lt;/socket-provider&gt;
    &lt;/socket-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

<p>Instead of using the file system, the certificate files can be uploaded to the OCI Secrets Service. The <code>secrets-cert</code> XML element can then be used to retrieve the provate key from the OCI Secrets Service.</p>

<p>In the example below, the OCI custom namespace has been added to the configuration file. In the <code>&lt;identity-manager&gt;</code> section, instead of the <code>&lt;key&gt;</code> element, the <code>&lt;oci:secrets-key&gt;</code> element is used to specify the OCID of the secret that the certificates are stored in.</p>

<markup
lang="xml"

>&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xmlns:oci="class://com.oracle.coherence.oci.config.OCINamespaceHandler"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
                coherence-operational-config.xsd
                class://com.oracle.coherence.oci.config.OCINamespaceHandler
                coherence-oci.xsd"&gt;

  &lt;cluster-config&gt;
    &lt;socket-providers&gt;
      &lt;socket-provider id="mySSLConfig"&gt;
         &lt;ssl&gt;
            &lt;protocol&gt;TLS&lt;/protocol&gt;
            &lt;identity-manager&gt;
              &lt;oci:secrets-key&gt;
                ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a
              &lt;/oci:secrets-key&gt;
              &lt;oci:secrets-cert&gt;
                ocid1.secret.oc1..decdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz89
              &lt;/oci:secrets-cert&gt;
            &lt;/identity-manager&gt;
            &lt;trust-manager&gt;
              &lt;oci:secrets-cert&gt;
                ocid1.secret.oc1..xacdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz37
              &lt;/oci:secrets-cert&gt;
            &lt;/trust-manager&gt;
         &lt;/ssl&gt;
      &lt;/socket-provider&gt;
    &lt;/socket-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>


<h3 id="_use_secret_names_instead_of_ocids_2">Use Secret Names Instead of OCIDs</h3>
<div class="section">
<p>The default behaviour of the <code>oci:secrets-cert</code> element is that its content is the OCID of a secret containing the certificate file. Alternatively the name of the secret can be used by specifying the <code>&lt;oci:secret-name&gt;</code> element as a child of the &lt;oci:secret-cert&gt;` element and additionally a compartment OCID using the <code>&lt;oci:compartment-id&gt;</code> element.</p>

<p>The example below will create a trust manager using the CA certificate from the secret with the name <code>my-secret-ca-cert</code> in the OCI compartment with OCID <code>ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a</code>.</p>

<markup
lang="xml"

>&lt;trust-manager&gt;
  &lt;oci:secrets-cert&gt;
    &lt;oci:secret-name&gt;my-secret-ca-cert&lt;/oci:secret-name&gt;
    &lt;oci:compartment-id&gt;
      ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a
    &lt;/oci:compartment-id&gt;
  &lt;/oci:secrets-cert&gt;
&lt;/trust-manager&gt;</markup>

<p>If the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are set the <code>&lt;oci:compartment-id&gt;</code> element can be omitted, leaving just the <code>&lt;oci:secret-name&gt;</code> element:</p>

<markup
lang="xml"

>&lt;trust-manager&gt;
  &lt;oci:secrets-cert&gt;
    &lt;oci:secret-name&gt;my-secret-ca-cert&lt;/oci:secret-name&gt;
  &lt;/oci:secrets-cert&gt;
&lt;/trust-manager&gt;</markup>

<p>Or alternatively if the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are set the secret name can be specified as the content of the <code>&lt;oci:secrets-cert&gt;</code> element:</p>

<markup
lang="xml"

>&lt;trust-manager&gt;
  &lt;oci:secrets-cert&gt;my-secret-ca-cert&lt;/oci:secrets-cert&gt;
&lt;/trust-manager&gt;</markup>

<p>If the <code>&lt;oci:secrets-cert&gt;</code> element does not contain a valid OCID, it is assumed to be a secret name, in which case the Compartment OCID must be specified.
If the <code>&lt;oci:secret-name&gt;</code> element is used, or if the <code>&lt;oci:secrets-cert&gt;</code> element is not an OCID and no Compartment OCID is specified or the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are not set, then an exception will be thrown.</p>

</div>
</div>

<h2 id="keystores">Keystores in Secrets</h2>
<div class="section">
<p>Instead of using private keys and certificates files, Coherence can use Java keystores in the <code>&lt;identity-manager&gt;</code> and <code>&lt;trust-manager&gt;</code> configurations. Normally keystores are loaded from the file system, but using the Coherence SSL extensions, a custom loader can be used to read the keystore from an alternative source. The Coherence OCI Secrets integration provides a custom keystore loader that can load keystore data from a secret stored in the OCI Secrets Service.</p>

<p>For example, the XML below configures the socket provider to use two keystores the file system at <code>file:/coherence/security/server.jks</code> and <code>file:/coherence/security/server-trust.jks</code></p>

<markup
lang="xml"

>&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
                coherence-operational-config.xsd"&gt;

  &lt;cluster-config&gt;
    &lt;socket-providers&gt;
      &lt;socket-provider id="mySSLConfig"&gt;
         &lt;ssl&gt;
            &lt;protocol&gt;TLS&lt;/protocol&gt;
            &lt;identity-manager&gt;
              &lt;keystore&gt;
                &lt;url&gt;file:/coherence/security/server.jks&lt;/url&gt;
              &lt;/keystore&gt;
            &lt;/identity-manager&gt;
            &lt;trust-manager&gt;
              &lt;keystore&gt;
                &lt;url&gt;file:/coherence/security/server-trust.jks&lt;/url&gt;
              &lt;/keystore&gt;
            &lt;/trust-manager&gt;
         &lt;/ssl&gt;
      &lt;/socket-provider&gt;
    &lt;/socket-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

<p>Instead of using the file system, the two keystores can be uploaded to secrets in the OCI Secrets Service.
Using the Coherence OCI Secrets integration, it is then possible to load the keystores directlry from the secrets using the relevant OCID.</p>

<markup
lang="xml"

>&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:oci="class://com.oracle.coherence.oci.config.OCINamespaceHandler"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
                coherence-operational-config.xsd
                class://com.oracle.coherence.oci.config.OCINamespaceHandler
                coherence-oci.xsd"&gt;

  &lt;cluster-config&gt;
    &lt;socket-providers&gt;
      &lt;socket-provider id="mySSLConfig"&gt;
         &lt;ssl&gt;
            &lt;protocol&gt;TLS&lt;/protocol&gt;
            &lt;identity-manager&gt;
              &lt;keystore&gt;
                &lt;key-store-loader&gt;
                  &lt;oci:secrets-key-store&gt;
                    ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a
                  &lt;/oci:secrets-key-store&gt;
                &lt;/key-store-loader&gt;
              &lt;/keystore&gt;
            &lt;/identity-manager&gt;
            &lt;trust-manager&gt;
              &lt;keystore&gt;
                &lt;key-store-loader&gt;
                  &lt;oci:secrets-key-store&gt;
                    ocid1.secret.oc1..xyzdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglixt5c
                  &lt;/oci:secrets-key-store&gt;
                &lt;/key-store-loader&gt;
              &lt;/keystore&gt;
            &lt;/trust-manager&gt;
         &lt;/ssl&gt;
      &lt;/socket-provider&gt;
    &lt;/socket-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

<p>The keystore for the identity manager will be loaded from the secret with OCID <code>ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a</code> and the keystore for the trust manager will be loaded from the secret with OCID <code>ocid1.secret.oc1..xyzdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglixt5c</code>.</p>


<h3 id="_use_secret_names_instead_of_ocids_3">Use Secret Names Instead of OCIDs</h3>
<div class="section">
<p>The default behaviour of the <code>oci:secrets-key-store</code> element is that its content is the OCID of a secret containing the keystore file. Alternatively the name of the secret can be used by specifying the <code>&lt;oci:secret-name&gt;</code> element as a child of the &lt;oci:secrets-key-store&gt;` element and additionally a compartment OCID using the <code>&lt;oci:compartment-id&gt;</code> element.</p>

<p>The example below will create a trust store using the keystore data from the secret with the name <code>my-truststore</code> in the OCI compartment with OCID <code>ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a</code>.</p>

<markup
lang="xml"

>&lt;trust-manager&gt;
  &lt;key-store-loader&gt;
    &lt;oci:secrets-key-store&gt;
      &lt;oci:secret-name&gt;my-truststore&lt;/oci:secret-name&gt;
      &lt;oci:compartment-id&gt;
        ocid1.compartment.oc1..abcdeaaabvmgyifakecompartmentxgxkxku5zhmpu35j67hdxvxpglijz4a
      &lt;/oci:compartment-id&gt;
    &lt;/oci:secrets-key-store&gt;
  &lt;/key-store-loader&gt;
&lt;/trust-manager&gt;</markup>

<p>If the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are set the <code>&lt;oci:compartment-id&gt;</code> element can be omitted, leaving just the <code>&lt;oci:secret-name&gt;</code> element:</p>

<markup
lang="xml"

>&lt;trust-manager&gt;
  &lt;key-store-loader&gt;
    &lt;oci:secrets-key-store&gt;
      &lt;oci:secret-name&gt;my-truststore&lt;/oci:secret-name&gt;
    &lt;/oci:secrets-key-store&gt;
  &lt;/key-store-loader&gt;
&lt;/trust-manager&gt;</markup>

<p>Or alternatively if the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are set the secret name can be specified as the content of the <code>&lt;oci:secrets-key-store&gt;</code> element:</p>

<markup
lang="xml"

>&lt;trust-manager&gt;
  &lt;key-store-loader&gt;
    &lt;oci:secrets-key-store&gt;my-truststore&lt;/oci:secrets-key-store&gt;
  &lt;/key-store-loader&gt;
&lt;/trust-manager&gt;</markup>

<p>If the <code>&lt;oci:secrets-key-store&gt;</code> element does not contain a valid OCID, it is assumed to be a secret name, in which case the Compartment OCID must be specified.
If the <code>&lt;oci:secret-name&gt;</code> element is used, or if the <code>&lt;oci:secrets-key-store&gt;</code> element is not an OCID and no Compartment OCID is specified or the <code>coherence.oci.compartment</code> system property, or <code>COHERENCE_OCI_COMPARTMENT</code> environment variable are not set, then an exception will be thrown.</p>

</div>
</div>
</doc-view>