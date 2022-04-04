<doc-view>

<v-layout row wrap>
<v-flex xs12 sm10 lg10>
<v-card class="section-def" v-bind:color="$store.state.currentColor">
<v-card-text class="pa-3">
<v-card class="section-def__card">
<v-card-text>
<dl>
<dt slot=title>Coherence Integrations with Oracle Cloud Infrastructure (OCI)</dt>
<dd slot="desc"><p><img src="https://oracle.github.io/coherence/assets/images/logo-red.png" alt="Coherence CE" />
</p>

<p><img src="https://img.shields.io/github/workflow/status/oracle/coherence-oci/Coherence%20OCI%20CI" alt="GitHub CI Status" />

<img src="https://img.shields.io/github/v/release/oracle/coherence-oci?sort=semver" alt="GitHub release (latest SemVer)" />
</p>

<p>This repository contains modules that integrate <a id="" title="" target="_blank" href="https://coherence.community/index.html">Oracle Coherence</a>
with <a id="" title="" target="_blank" href="https://www.oracle.com/cloud/">Oracle Cloud Infrastructure</a></p>
</dd>
</dl>
</v-card-text>
</v-card>
</v-card-text>
</v-card>
</v-flex>
</v-layout>

<h2 id="_prerequisites">Prerequisites</h2>
<div class="section">

<h3 id="_coherence">Coherence</h3>
<div class="section">
<p>The Coherence OCI modules require Coherence version 22.06 or later to run.</p>

</div>

<h3 id="_java">Java</h3>
<div class="section">
<p>The Coherence OCI modules are compiled to Java 11 byte code so require Java 11, or later, to run.</p>

</div>
</div>

<h2 id="_available_integrations">Available Integrations</h2>
<div class="section">

<h3 id="_oci_secrets_service">OCI Secrets Service</h3>
<div class="section">
<p>Use the OCI Secrets Service to store keys and certificates used to secure Coherence clusters with TLS.
The keys and certificates are retrieved directly from the secrets service without requiring Java keystores on the Coherence process&#8217;s file system.</p>

<p>See: The OCI Secrets module <a id="" title="" target="_blank" href="coherence-oci-secrets/README.adoc">README</a> file</p>

</div>
</div>

<h2 id="_binary_distribution">Binary Distribution</h2>
<div class="section">
<div class="admonition note">
<p class="admonition-textlabel">Note</p>
<p ><p>Developers choosing to distribute a binary implementation of this project are responsible for obtaining and providing all required licenses and copyright notices for the third-party code used in order to ensure compliance with their respective open source licenses.</p>
</p>
</div>
</div>
</doc-view>