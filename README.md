<!--
    Copyright (c) 2000, 2021, Oracle and/or its affiliates.

    Licensed under the Universal Permissive License v 1.0 as shown at
    https://oss.oracle.com/licenses/upl.
-->
# Coherence Integrations with Oracle Cloud Infrastructure (OCI)

![logo](https://oracle.github.io/coherence/assets/images/logo-red.png)

This repository contains modules that integrate [Oracle Coherence](https://coherence.community/index.html)
with [Oracle Cloud Infrastructure](https://www.oracle.com/cloud/)

## Available Integrations

### OCI Secrets Service

Use the OCI Secrets Service to store keys and certificates used to secure Coherence clusters with TLS.
The keys and certificates are retrieved directly from the secrets service without requiring Java keystores on the Coherence process's file system.

See: The OCI Secrets module [README](coherence-oci-secrets/README.adoc) file

**Note:** Developers choosing to distribute a binary implementation of this project are responsible for obtaining and providing all required licenses and copyright notices for the third-party code used in order to ensure compliance with their respective open source licenses.
