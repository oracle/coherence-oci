<doc-view>

<h2 id="_introduction">Introduction</h2>
<div class="section">
<p>Coherence Persistence provides the ability to create snapshots of services at any time.  The snapshot operations are
carried out in parallel, by all the members that own the partitions, and the snapshot files reside on the individual machines.</p>

<p>Archiving provides the ability to take all those snapshot files and archive them to a central location, as a full set of partitions.</p>

<p>Out of the box Coherence provides a directory snapshot archiver implementation which requires a shared directory between all members
to put the snapshots.</p>

<p>This project, provides an alternate option if you are using Oracle Cloud Infrastructure&#8217;s (OCI) Object Storage.</p>

<p>See the <a id="" title="" target="_blank" href="https://docs.oracle.com/en/middleware/fusion-middleware/coherence/14.1.2/administer/persisting-caches.html#GUID-3B67650D-D272-4DBB-9004-0090906FC894">Coherence Documentation</a> for
more information on Persistence and snapshots.</p>

<ul class="ulist">
<li>
<p><router-link to="#pre" @click.native="this.scrollFix('#pre')">Prerequisites</router-link></p>
<ul class="ulist">
<li>
<p><router-link to="#oci-access" @click.native="this.scrollFix('#oci-access')">Oracle Cloud Infrastructure (OCI) Access</router-link></p>

</li>
<li>
<p><router-link to="#install-oci" @click.native="this.scrollFix('#install-oci')">Install the OCI CLI</router-link></p>

</li>
<li>
<p><router-link to="#api-key" @click.native="this.scrollFix('#api-key')">Create an API signing key pair</router-link></p>

</li>
<li>
<p><router-link to="#bucket" @click.native="this.scrollFix('#bucket')">Create a bucket in OCI</router-link></p>

</li>
</ul>
</li>
<li>
<p><router-link to="#coherence" @click.native="this.scrollFix('#coherence')">Setup Coherence</router-link></p>
<ul class="ulist">
<li>
<p><router-link to="#include-library" @click.native="this.scrollFix('#include-library')">Include the OCI snapshot archiver library</router-link></p>

</li>
<li>
<p><router-link to="#bucket-prefix" @click.native="this.scrollFix('#bucket-prefix')">Determine your bucket and prefix</router-link></p>

</li>
<li>
<p><router-link to="#creds" @click.native="this.scrollFix('#creds')">Choose the method to specify your credentials in the override file</router-link></p>
<ul class="ulist">
<li>
<p><router-link to="#profile" @click.native="this.scrollFix('#profile')">1. Specify using a profile</router-link></p>

</li>
<li>
<p><router-link to="#env" @click.native="this.scrollFix('#env')">2. Leave the profile out to use environment variables</router-link></p>

</li>
</ul>
</li>
<li>
<p><router-link to="#archiver" @click.native="this.scrollFix('#archiver')">Specify the archiver in your cache config</router-link></p>

</li>
<li>
<p><router-link to="#threads" @click.native="this.scrollFix('#threads')">Configure archiver threads</router-link></p>

</li>
<li>
<p><router-link to="#mebans" @click.native="this.scrollFix('#mebans')">Enable archiver MBeans</router-link></p>

</li>
</ul>
</li>
</ul>
</div>

<h2 id="pre">Prerequisites</h2>
<div class="section">
<p>To use the Coherence OCI snapshot archiver integrations in your project there are some prerequisites required.</p>


<h3 id="oci-access">Oracle Cloud Infrastructure (OCI) Access</h3>
<div class="section">
<p>You must have an account on Oracle Cloud Infrastructure (OCI). If you do not, you can sign up for a free tier <a id="" title="" target="_blank" href="https://www.oracle.com/au/cloud/free/">here</a>.</p>

</div>

<h3 id="install-oci">Install the OCI CLI</h3>
<div class="section">
<p>Follow the <a id="" title="" target="_blank" href="https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/cliinstall.htm">instructions here</a> to install the OCI CLI.</p>

</div>

<h3 id="api-key">Create an API signing key pair</h3>
<div class="section">
<p>To access your OCI Object storage you need to create an API signing key pair. Instructions can be found <a id="" title="" target="_blank" href="https://docs.oracle.com/en-us/iaas/Content/API/Concepts/apisigningkey.htm#apisigningkey_topic_How_to_Generate_an_API_Signing_Key_Console">here</a> but brief instructions are below:</p>

<ol style="margin-left: 15px;">
<li>
After logging in, view the user&#8217;s details by opening the <code>Profile</code> menu and click My profile or your login name.

</li>
<li>
In the <code>Resources</code> section at the bottom left, click <code>API Keys</code>

</li>
<li>
Click <code>Add API Key</code> at the top left of the <code>API Keys</code> list. The Add API Key dialog displays.

</li>
<li>
Download the private key and place in <code>~/.oci</code> directory and change the perms:
<div class="listing">
<pre>chmod go-rwx ~/.oci/&lt;oci_api_keyfile&gt;.pem</pre>
</div>

</li>
<li>
Copy the contents of the configuration file preview and add to <code>~/.oci/config</code> it should look something like the following. You should change the name from <code>DEFAULT</code> if you already have and entry with this name.
<div class="listing">
<pre>[DEFAULT]
user=ocid1.user.oc1..xxxxxxx
fingerprint=xx:xx:xx:xx
tenancy=ocid1.tenancy.oc1..xxxxxx
region=us-ashburn-1
key_file=&lt;path to your private keyfile&gt; # TODO</pre>
</div>

<div class="admonition note">
<p class="admonition-inline">You should add the path to your private key file.</p>
</div>
</li>
</ol>
</div>

<h3 id="bucket">Create a bucket in OCI</h3>
<div class="section">
<p>Follow the instructions (<a id="" title="" target="_blank" href="https://docs.oracle.com/en-us/iaas/Content/Object/Tasks/managingbuckets_topic-To_create_a_bucket.htm">here</a> to create a bucket to store your archived snapshots.</p>

</div>
</div>

<h2 id="coherence">Setup Coherence</h2>
<div class="section">

<h3 id="include-library">Include the OCI snapshot archiver library</h3>
<div class="section">
<p>You need to include the <code>oci-archiver</code> project as a dependency in your project.</p>

<p>Currently, there are no releases for this project, but you can clone the repository and build it using the following:</p>

<div class="listing">
<pre>git clone https://github.com/oracle/coherence-oci.git
cd coherence-oci
mvn clean install -DskipTests</pre>
</div>

<p>Include the following dependency in your project.</p>

<div class="listing">
<pre>&lt;dependency&gt;
  &lt;groupId&gt;com.oracle.coherence.oci&lt;/groupId&gt;
  &lt;artifactId&gt;coherence-oci-archiver&lt;/artifactId&gt;
  &lt;version&gt;1.0.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</pre>
</div>

</div>

<h3 id="bucket-prefix">Determine your bucket and prefix</h3>
<div class="section">
<p>You must choose an existing bucket name and an optional prefix for the directory structure.
If the prefix is blank, then the directory structure it will start at <code>/</code>.</p>

<div class="admonition note">
<p class="admonition-inline">OCI Object Storages does not have the concept of directories, but they are 'simulated' by providing fully qualified paths.</p>
</div>
</div>

<h3 id="creds">Choose the method to specify Your credentials in the override file</h3>
<div class="section">
<p>There are two options to specify the credentials for the snapshot archiver.</p>

<ol style="margin-left: 15px;">
<li>
Specify an OCI Profile name (from <code>~/.oci/config</code>) in the override file

</li>
<li>
Use environment variables

</li>
</ol>
<p>This must be configured in your operational override file and specified via <code>-Dcoherence.override=filename.xml</code>.</p>

<p>This allows for flexibility based upon your requirements.</p>


<h4 id="profile">1. Specify sing an OCI profile</h4>
<div class="section">
<div class="listing">
<pre>&lt;?xml version='1.0'?&gt;
&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config coherence-operational-config.xsd"
           xml-override="../common/tangosol-coherence-override.xml"&gt;

  &lt;cluster-config&gt;
    &lt;snapshot-archivers&gt;
      &lt;custom-archiver id="oci-snapshot-archiver"&gt;
        &lt;class-name&gt;com.oracle.coherence.oci.archiver.ObjectStorageSnapshotArchiver&lt;/class-name&gt;
        &lt;init-params&gt;
          &lt;init-param&gt;
            &lt;param-value&gt;{cluster-name}&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-value&gt;{service-name}&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value system-property="oci.archiver.bucket"&gt;archiver-test-bucket&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value system-property="oci.archiver.prefix"&gt;test-prefix&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value system-property="oci.archiver.profile"&gt;OBJECT_STORAGE&lt;/param-value&gt;
          &lt;/init-param&gt;
        &lt;/init-params&gt;
      &lt;/custom-archiver&gt;
    &lt;/snapshot-archivers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</pre>
</div>

<div class="admonition note">
<p class="admonition-inline">the system properties used above are examples, and you can change them to suit your needs.</p>
</div>
</div>

<h4 id="env">2. Leave the profile out to use environment variables</h4>
<div class="section">
<p>When you leave the profile out of the <code>init-params</code> this means the archiver will look for the required information
in the following environment variables:</p>

<ul class="ulist">
<li>
<p>Tenancy OCID: - <code>OCI_ARCHIVER_TENANCY_OCID</code></p>

</li>
<li>
<p>Region - <code>OCI_ARCHIVER_REGION</code></p>

</li>
<li>
<p>User OCID - <code>OCI_ARCHIVER_USER_OCID</code></p>

</li>
<li>
<p>Finger Print - <code>OCI_ARCHIVER_FINGERPRINT</code></p>

</li>
<li>
<p>Private Key Path - <code>OCI_ARCHIVER_PRIVATE_KEY_PATH</code></p>

</li>
</ul>
<div class="listing">
<pre>&lt;?xml version='1.0'?&gt;

&lt;coherence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.oracle.com/coherence/coherence-operational-config"
           xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-operational-config coherence-operational-config.xsd"
           xml-override="../common/tangosol-coherence-override.xml"&gt;

  &lt;cluster-config&gt;
    &lt;snapshot-archivers&gt;
      &lt;custom-archiver id="oci-snapshot-archiver"&gt;
        &lt;class-name&gt;
          com.oracle.coherence.oci.archiver.ObjectStorageSnapshotArchiver
        &lt;/class-name&gt;
        &lt;init-params&gt;
          &lt;init-param&gt;
            &lt;param-value&gt;{cluster-name}&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-value&gt;{service-name}&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value system-property="oci.archiver.bucket"&gt;archiver-test-bucket&lt;/param-value&gt;
          &lt;/init-param&gt;
          &lt;init-param&gt;
            &lt;param-type&gt;string&lt;/param-type&gt;
            &lt;param-value system-property="oci.archiver.prefix"&gt;test-prefix&lt;/param-value&gt;
          &lt;/init-param&gt;
        &lt;/init-params&gt;
      &lt;/custom-archiver&gt;
    &lt;/snapshot-archivers&gt;
  &lt;/cluster-config&gt;
&lt;/coherence&gt;</pre>
</div>

</div>
</div>

<h3 id="archiver">Specify the archiver in your cache config</h3>
<div class="section">
<p>Below is an example where we specify the name of the archiver to use:</p>

<div class="listing">
<pre>&lt;?xml version="1.0"?&gt;
&lt;cache-config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xmlns="http://xmlns.oracle.com/coherence/coherence-cache-config"
              xsi:schemaLocation="http://xmlns.oracle.com/coherence/coherence-cache-config coherence-cache-config.xsd"&gt;

  &lt;caching-scheme-mapping&gt;
    &lt;cache-mapping&gt;
      &lt;cache-name&gt;*&lt;/cache-name&gt;
      &lt;scheme-name&gt;server&lt;/scheme-name&gt;
    &lt;/cache-mapping&gt;
  &lt;/caching-scheme-mapping&gt;

  &lt;caching-schemes&gt;
    &lt;distributed-scheme&gt;
      &lt;scheme-name&gt;server&lt;/scheme-name&gt;
      &lt;service-name&gt;PartitionedCache&lt;/service-name&gt;
      &lt;backing-map-scheme&gt;
        &lt;transient&gt;{transient false}&lt;/transient&gt;
        &lt;local-scheme&gt;
          &lt;unit-calculator&gt;BINARY&lt;/unit-calculator&gt;
          &lt;expiry-delay&gt;0&lt;/expiry-delay&gt;
        &lt;/local-scheme&gt;
      &lt;/backing-map-scheme&gt;
      &lt;persistence&gt;
        &lt;environment&gt;default-on-demand&lt;/environment&gt;
        &lt;!-- &lt;environment&gt;default-active&lt;/environment&gt; --&gt;
        &lt;archiver&gt;oci-snapshot-archiver&lt;/archiver&gt;
      &lt;/persistence&gt;
      &lt;autostart&gt;true&lt;/autostart&gt;
    &lt;/distributed-scheme&gt;
  &lt;/caching-schemes&gt;
&lt;/cache-config&gt;</pre>
</div>

</div>

<h3 id="threads">Configure archiver threads</h3>
<div class="section">
<p>To optimize archiver performance, you can specify the following system property on your cache servers, to start
the required number of archiver threads per storage member.</p>

<ul class="ulist">
<li>
<p><code>-Dcoherence.distributed.persistence.oci.archiver.threads=8</code></p>

</li>
</ul>
<div class="admonition note">
<p class="admonition-inline">The default is 4 and it is recommended to change this and test various values to determine the optimum value for your cluster.</p>
</div>
</div>

<h3 id="mebans">Monitoring archiver throughput</h3>
<div class="section">
<p>By default, the following system property is enabled and instructs Coherence to create an archiver MBean to monitor snapshot performance.</p>

<ul class="ulist">
<li>
<p><code>-Dcoherence.distributed.persistence.oci.archiver.mbean.enabled=true</code></p>

</li>
</ul>
<p>You can set this to <code>false</code> to disable this MBean.
There is one MBean per service and node named: <code>Coherence:type=ObjectStorageSnapshotArchiver,service=serviceName,nodeId=n</code></p>

<p>The attributes collected are shown below:</p>

<markup
lang="java"
title="SnapshotArchiverMBean.java"
>/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.archiver;

import com.tangosol.net.management.annotation.Description;
import com.tangosol.net.management.annotation.MetricsValue;


/**
 * Defines various attributes to record for snapshot a {@link ObjectStorageSnapshotArchiver}.
 */
@Description("Provides Snapshot Archiver statistics.")
public interface SnapshotArchiverMBean {

    /**
     * Returns the number of archiver threads to use in parallel.
     * @return the number of archiver threads to use in parallel
     */
    @Description("The number of archiver threads to use in parallel.")
    @MetricsValue
    long getArchiverThreadCount();

    /**
     * Returns the last number of stores archived.
     * @return the last number of stores archived
     */
    @Description("Last number of stores archived.")
    @MetricsValue
    long getArchivedStoresCount();

    /**
     * Returns the total time archiving stores for this storage member.
     * @return the total time archiving stores for this storage member
     */
    @Description("Total time archiving stores for this storage member.")
    @MetricsValue
    long getArchivedStoresTotalMillis();

    /**
     * Returns the total number of archive store failures for this storage member.
     * @return the total number of archive store failures for this storage member
     */
    @Description("Total number of archive store failures for this storage member.")
    @MetricsValue
    long getArchivedStoresFailures();

    /**
     * Returns the average time to archive stores.
     * @return the average time to archive stores
     */
    @Description("Average time to archive stores.")
    @MetricsValue
    float getArchivedStoresAverageMillis();

    /**
     * Returns the maximum time to archive stores.
     * @return the maximum time to archive stores
     */
    @Description("Maximum time to archive stores.")
    @MetricsValue
    long getArchivedStoresMaxMillis();

    /**
     * Returns the last duration to archive all stores.
     * @return the last duration to archive all stores
     */
    @Description("Last duration to archive all stores.")
    @MetricsValue
    long getArchivedStoresLastDurationMillis();

    /**
     * Returns the last number of stores retrieved.
     * @return the last number of stores retrieved.
     */
    @Description("Last number of stores retrieved.")
    @MetricsValue
    long getRetrievedStoresCount();

    /**
     * Returns the total time retrieving stores.
     * @return the total time retrieving stores
     */
    @Description("Total time retrieving stores.")
    @MetricsValue
    long getRetrievedStoresTotalMillis();

    /**
     * Returns the total number of retrieve store failures.
     * @return the total number of retrieve store failures.
     */
    @Description("Total number of retrieve store failures.")
    @MetricsValue
    long getRetrievedStoresFailures();

    /**
     * Returns the average time to retrieve stores.
     * @return the average time to retrieve stores
     */
    @Description("Average time to retrieve stores.")
    @MetricsValue
    float getRetrievedStoresAverageMillis();

    /**
     * Returns the maximum time to retrieve stores.
     * @return the maximum time to retrieve stores.
     */
    @Description("Maximum time to retrieve stores.")
    @MetricsValue
    float getRetrievedStoresMaxMillis();

    /**
     * Returns the last duration to retrieve all stores.
     * @return the last duration to retrieve all stores.
     */
    @Description("Last duration to retrieve all stores.")
    @MetricsValue
    long getRetrievedStoresLastDurationMillis();

    /**
     * Returns the last number of stores deleted.
     * @return the last number of stores deleted.
     */
    @Description("Last number of stores deleted.")
    @MetricsValue
    long getDeletedStoresCount();

    /**
     * Returns the total time deleting stores.
     * @return the total time deleting stores.
     */
    @Description("Total time deleting stores.")
    @MetricsValue
    long getDeletedStoresTotalMillis();

    /**
     * Returns the total number of delete store failures.
     * @return the total number of delete store failures.
     */
    @Description("Total number of delete store failures.")
    @MetricsValue
    long getDeletedStoresFailures();

    /**
     * Returns the average time to delete stores.
     * @return the average time to delete stores.
     */
    @Description("Average time to delete stores.")
    @MetricsValue
    float getDeletedStoresAverageMillis();

    /**
     * Returns the maximum time to delete stores.
     * @return the maximum time to delete stores.
     */
    @Description("Maximum time to delete stores.")
    @MetricsValue
    float getDeletedStoresMaxMillis();

    /**
     * Returns the last duration to delete all stores.
     * @return the last duration to delete all stores.
     */
    @Description("Last duration to delete all stores.")
    @MetricsValue
    long getDeleteStoresLastDurationMillis();

    /**
     * Resets the statistics.
     */
    @Description("Reset the statistics.")
    public void resetArchiverStatistics();
    }</markup>

</div>
</div>
</doc-view>