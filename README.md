<!--
  Copyright (c) 2000, 2023, Oracle and/or its affiliates.

  Licensed under the Universal Permissive License v 1.0 as shown at
  https://oss.oracle.com/licenses/upl.
-->

# Oracle Coherence OCI Integrations

<img src=https://oracle.github.io/coherence/assets/images/logo-red.png><img>

This repository contains modules that integrate [Oracle Coherence](https://coherence.community/index.html)
with [Oracle Cloud Infrastructure](https://www.oracle.com/cloud/).

## Getting Started

The Coherence OCI integrations are published as a set of jar dependencies that you use in your Coherence application.
There is nothing to install, you use the modules just as you would any other Java dependencies.

## Prerequisites

### Coherence
The Coherence OCI modules require Coherence version 22.06 or later to run.

### Java
The Coherence OCI modules are compiled to Java 11 byte code so require Java 11, or later, to run.


## Available Integrations

### OCI Secrets Service

Use the OCI Secrets Service to store keys and certificates used to secure Coherence clusters with TLS.
The keys and certificates are retrieved directly from the secrets service without requiring Java key stores on the Coherence process's file system.

See: The OCI Secrets module link:coherence-oci-secrets/README.adoc[README] file

## Binary Distribution
     
Developers choosing to distribute a binary implementation of this project are responsible for obtaining and providing all required licenses and copyright notices for the third-party code used in order to ensure compliance with their respective open source licenses.


## Contributing

This project welcomes contributions from the community. Before submitting a pull request, please [review our contribution guide](./CONTRIBUTING.md)

## Security

Please consult the [security guide](./SECURITY.md) for our responsible security vulnerability disclosure process

## License

Copyright (c) 2023 Oracle and/or its affiliates.

Released under the Universal Permissive License v1.0 as shown at
<https://oss.oracle.com/licenses/upl/>.