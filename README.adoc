///////////////////////////////////////////////////////////////////////////////
    Copyright (c) 2000, 2021, Oracle and/or its affiliates.

    Licensed under the Universal Permissive License v 1.0 as shown at
    http://oss.oracle.com/licenses/upl.
///////////////////////////////////////////////////////////////////////////////
= Coherence Integrations with Oracle Cloud Infrastructure (OCI)

image:https://oracle.github.io/coherence/assets/images/logo-red.png[Coherence CE]

image:https://img.shields.io/github/workflow/status/oracle/coherence-oci/Coherence%20OCI%20CI[GitHub CI Status]
image:https://img.shields.io/github/v/release/oracle/coherence-oci?sort=semver[GitHub release (latest SemVer)]

This repository contains modules that integrate https://coherence.community/index.html[Oracle Coherence]
with https://www.oracle.com/cloud/[Oracle Cloud Infrastructure]

== Prerequisites

=== Coherence
The Coherence OCI modules require Coherence version 22.06 or later to run.

=== Java
The Coherence OCI modules are compiled to Java 11 byte code so require Java 11, or later, to run.


== Available Integrations

=== OCI Secrets Service

Use the OCI Secrets Service to store keys and certificates used to secure Coherence clusters with TLS.
The keys and certificates are retrieved directly from the secrets service without requiring Java keystores on the Coherence process's file system.

See: The OCI Secrets module link:coherence-oci-secrets/README.adoc[README] file

== Binary Distribution

[NOTE]
====
Developers choosing to distribute a binary implementation of this project are responsible for obtaining and providing all required licenses and copyright notices for the third-party code used in order to ensure compliance with their respective open source licenses.
====
