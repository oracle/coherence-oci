<doc-view>

<h2 id="_overview">Overview</h2>
<div class="section">
<v-layout row wrap class="mb-5">
<v-flex xs12>
<v-container fluid grid-list-md class="pa-0">
<v-layout row wrap class="pillars">
<v-flex xs12 sm4 lg3>
<v-card>
<router-link to="#docs/about/01_introduction.adoc" @click.native="this.scrollFix('#docs/about/01_introduction.adoc')"><div class="card__link-hover"/>
</router-link>
<v-layout align-center justify-center class="">
<v-avatar size="150px">
<v-icon class="xxx-large">assistant</v-icon>
</v-avatar>
</v-layout>
<div class="px-3">
<v-divider class="indigo lighten-4"/>
</div>
<v-card-title primary class="headline layout justify-center">
<span style="text-align:center">Introduction</span>
</v-card-title>
<v-card-text class="caption">
<p></p>
<p>Integrations with the OCI Vault and Secrets service,
for example storing TLS keys and certs as secrets.</p>
</v-card-text>
</v-card>
</v-flex>
</v-layout>
</v-container>
</v-flex>
</v-layout>
</div>

<h2 id="_oci_integrations">OCI Integrations</h2>
<div class="section">
<v-layout row wrap class="mb-5">
<v-flex xs12>
<v-container fluid grid-list-md class="pa-0">
<v-layout row wrap class="pillars">
<v-flex xs12 sm4 lg3>
<v-card>
<router-link to="#docs/secrets/01_introduction.adoc" @click.native="this.scrollFix('#docs/secrets/01_introduction.adoc')"><div class="card__link-hover"/>
</router-link>
<v-layout align-center justify-center class="">
<v-avatar size="150px">
<v-icon class="xxx-large">visibility_off</v-icon>
</v-avatar>
</v-layout>
<div class="px-3">
<v-divider class="indigo lighten-4"/>
</div>
<v-card-title primary class="headline layout justify-center">
<span style="text-align:center">Vault & Secrets</span>
</v-card-title>
<v-card-text class="caption">
<p></p>
<p>Integrations with the OCI Vault and Secrets service.
For example, storing TLS keys and certs as secrets.</p>
</v-card-text>
</v-card>
</v-flex>
</v-layout>
</v-container>
</v-flex>
</v-layout>
</div>
</doc-view>