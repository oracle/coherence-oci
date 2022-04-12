<doc-view>

<h2 id="_custom_authentication">Custom Authentication</h2>
<div class="section">
<p>The Coherence OCI integrations require connections to various OCI services.
As such, they require configuring with the correct configuration file and authentications.
Out of the box, Coherence OCI integrations use the default OCI configuration mechanism, as described in the <router-link to="/docs/about/02_getting_started">Getting Started</router-link> guide.</p>

<p>It is possible to override the default behaviour and provide a custom configured OCI <code>com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider</code> as shown in the
<a id="" title="" target="_blank" href="https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/javasdkgettingstarted.htm">OCI Java API Getting Started</a> guide.
This allows the Coherence OCI integrations to use any custom configuration required by a specific application.</p>


<h3 id="_configure_a_global_coherence_oci_authentication">Configure a Global Coherence OCI Authentication</h3>
<div class="section">
<p>Taking the code from the
<a id="" title="" target="_blank" href="https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/javasdkgettingstarted.htm">OCI Java API Getting Started</a> guide,
an <code>AuthenticationDetailsProvider</code> instance can be created like this:</p>

<markup
lang="java"
title="AuthenticationFactory.java"
>package com.oracle.coherence.oci.example;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;

import java.io.InputStream;
import java.util.function.Supplier;

public class AuthenticationFactory
    {
    public static AuthenticationDetailsProvider createAuthentication()
        {
        Supplier&lt;InputStream&gt; privateKeySupplier
            = new SimplePrivateKeySupplier("~/.oci/oci_api_key.pem");

        return SimpleAuthenticationDetailsProvider.builder()
                .tenantId("myTenantId")
                .userId("myUserId")
                .fingerprint("myFingerprint")
                .privateKeySupplier(privateKeySupplier::get)
                .build();
        }
    }</markup>

<p>The Coherence OCI integrations can then be configured to use the <code>AuthenticationFactory</code>.</p>

<p>All the Coherence OCI integrations will look for a custom cluster resource defined in the Coherence operational configuration file, with the id <code>oci-authentication</code>. If this resource is present, it will be used to provide an <code>AbstractAuthenticationDetailsProvider</code> instead of the OCI defaults.</p>

<p>For example, the <code>com.oracle.coherence.oci.example.AuthenticationFactory</code> above can be added to the <code>&lt;cluster-config&gt;</code> resources section in the operational configuration file as shown below:</p>

<markup
lang="xml"
title="tangosol-coherence-override.xml"
>&lt;?xml version="1.0"?&gt;
&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
              coherence-operational-config.xsd"&gt;

  &lt;cluster-config&gt;
    &lt;resources&gt;
      &lt;resource id="oci-authentication"&gt;
        &lt;class-factory-name&gt;com.oracle.coherence.oci.example.AuthenticationFactory&lt;/class-factory-name&gt;
        &lt;method-name&gt;createAuthentication&lt;/method-name&gt;
      &lt;/resource&gt;
    &lt;/resources&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

<p>With the above configuration, whenever any of the Coherence OCI integrations requires a <code>AbstractAuthenticationDetailsProvider</code> instance, it will call the <code>AuthenticationFactory.createAuthentication()</code> method.</p>


<h4 id="_parameterizing_the_configuration">Parameterizing the Configuration</h4>
<div class="section">
<p>In the <code>AuthenticationFactory</code> above, a number of values used to create the <code>AuthenticationDetailsProvider</code> were hard coded in the <code>createAuthentication()</code> method. This is fine for an example, but not very flexible for real life use. Typically, configuration values would be obtained from System properties or environment variables in the <code>createAuthentication()</code> method, but alternatively they can be passed in from the configuration.</p>

<p>The <code>AuthenticationFactory</code> class could be changed to parameterize the private key location, tenant id, user id and fingerprint, as shown below.</p>

<markup
lang="java"
title="AuthenticationFactory.java"
>package com.oracle.coherence.oci.example;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;

import java.io.InputStream;
import java.util.function.Supplier;

public class AuthenticationFactory
    {
    public static AuthenticationDetailsProvider createAuthentication(String pem,
            String tenantId, String userId, String fingerprint)
        {
        Supplier&lt;InputStream&gt; privateKeySupplier
            = new SimplePrivateKeySupplier(pem);

        return SimpleAuthenticationDetailsProvider.builder()
                .tenantId(tenantId)
                .userId(userId)
                .fingerprint(fingerprint)
                .privateKeySupplier(privateKeySupplier::get)
                .build();
        }
    }</markup>

<p>The Coherence operational configuration file can then be changed to pass in the corresponding values.</p>

<markup
lang="xml"
title="tangosol-coherence-override.xml"
>&lt;?xml version="1.0"?&gt;
&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config
              coherence-operational-config.xsd"&gt;

  &lt;cluster-config&gt;
    &lt;resources&gt;
      &lt;resource id="oci-authentication"&gt;
        &lt;class-factory-name&gt;com.oracle.coherence.oci.example.AuthenticationFactory&lt;/class-factory-name&gt;
        &lt;method-name&gt;createAuthentication&lt;/method-name&gt;
        &lt;init-params&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value&gt;~/.oci/oci_api_key.pem&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value&gt;myTenantId&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value&gt;myUserId&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value&gt;myFingerprint&lt;/param-value&gt;
          &lt;/init-param&gt;
        &lt;/init-params&gt;
      &lt;/resource&gt;
    &lt;/resources&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

</div>
</div>

<h3 id="_configure_in_line_coherence_oci_authentication">Configure In-Line Coherence OCI Authentication</h3>
<div class="section">
<p>Most of the Coherence OCI integrations can have a custom authentication configured in-line using the <code>&lt;oci:authentication&gt;</code> XML element, wherever they are added to Coherence XML configuration files.</p>

<p>For example, a <router-link to="/docs/secrets/03_password_provider">Secrets PasswordProvider</router-link> can be configured directly with custom authentication.</p>

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
      &lt;password-provider id="secrets"&gt;
        &lt;oci:secrets-password-provider&gt;
          &lt;oci:secret-id&gt;ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a&lt;/oci:secret-id&gt;
          &lt;oci:authentication&gt;
            &lt;class-factory-name&gt;com.oracle.coherence.oci.example.AuthenticationFactory&lt;/class-factory-name&gt;
            &lt;method-name&gt;createAuthentication&lt;/method-name&gt;
          &lt;/oci:authentication&gt;
        &lt;/oci:secrets-password-provider&gt;
      &lt;/password-provider&gt;
    &lt;/password-providers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</markup>

<p>In the example above, the password provider will obtain the password from a secret with the OCID <code>ocid1.secret.oc1..abcdeaaabvmgyifakesecrettxgxkxku5zhmpu35j67hdxvxpglijz4a</code> using the authentication obtained from the <code>AuthenticationFactory</code> class.</p>

</div>
</div>
</doc-view>