<doc-view>

<h2 id="_introduction">Introduction</h2>
<div class="section">
<p>This module provides integrations between Coherence and the OCI Secrets Service.</p>

<p>The following features are available:</p>

<ul class="ulist">
<li>
<p><router-link to="/docs/secrets/02_ssl">Store TLS/SSL keystores, keys and certificates in OCI secrets</router-link></p>

</li>
<li>
<p><router-link to="/docs/secrets/03_password_provider">Store passwords in OCI secrets</router-link></p>

</li>
</ul>
</div>

<h2 id="_prerequisites">Prerequisites</h2>
<div class="section">
<p>To use the Coherence OCI Secretes integrations in your project there are some prerequisites required.</p>


<h3 id="_configuration">Configuration</h3>
<div class="section">
<p>The Coherence OCI modules require the OCI APIs to be configured as described in the <router-link to="/docs/about/02_getting_started">Getting Started</router-link> guide.</p>

</div>

<h3 id="_project_dependencies">Project Dependencies</h3>
<div class="section">
<p>To use the Coherence OCI Secrets module your project must depend on <code>coherence.jar</code> and <code>coherence-oci-secrets.jar</code> by adding the following dependency to your project.</p>

<markup
lang="xml"
title="Maven pom.xml"
>&lt;dependency&gt;
    &lt;groupId&gt;com.oracle.coherence.ce&lt;/groupId&gt;
    &lt;artifactId&gt;coherence&lt;/artifactId&gt;
    &lt;version&gt;${coherence.version}&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
    &lt;groupId&gt;com.oracle.coherence&lt;/groupId&gt;
    &lt;artifactId&gt;coherence-oci-secrets&lt;/artifactId&gt;
    &lt;version&gt;${coherence.oci.version}&lt;/version&gt;
&lt;/dependency&gt;</markup>

<markup

title="Gradle build.gradle"
>implementation "com.oracle.coherence.ce:coherence:${coherence.version}"
implementation "com.oracle.coherence:coherence-oci-secrets:${coherence.oci.version}"</markup>

<p>Replacing <code>${coherence.version}</code> with the version of Coherence being used and
replacing <code>${coherence.oci.version}</code> with the version of the Coherence OCI module you are using.</p>

</div>
</div>
</doc-view>