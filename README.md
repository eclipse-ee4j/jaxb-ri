[//]: # " Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved. "
[//]: # "  "
[//]: # " This program and the accompanying materials are made available under the "
[//]: # " terms of the Eclipse Distribution License v. 1.0, which is available at "
[//]: # " http://www.eclipse.org/org/documents/edl-v10.php. "
[//]: # "  "
[//]: # " SPDX-License-Identifier: BSD-3-Clause "

# Eclipse Implementation of JAXB

[![Build Status](https://github.com/eclipse-ee4j/jaxb-ri/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/eclipse-ee4j/jaxb-ri/actions/workflows/maven.yml?branch=master)
[![Jakarta Staging (Snapshots)](https://img.shields.io/nexus/s/https/jakarta.oss.sonatype.org/com.sun.xml.bind/jaxb-ri.svg)](https://jakarta.oss.sonatype.org/content/repositories/staging/com/sun/xml/bind/jaxb-ri/)

Jakarta XML Binding gives Java developers an efficient and standard way of mapping between XML and Java code.
Java developers using Jakarta XML Binding are more productive because they can write less code themselves
and do not have to be experts in XML. Jakarta XML Binding makes it easier for developers to extend
their applications with XML and Web Services technologies.

Eclipse Implementation of JAXB enables developers to perform the following operations:
- Unmarshal XML content into a Java representation
- Access and update the Java representation
- Marshal the Java representation of the XML content into XML content

This project is part of [Eclipse Implementation of JAXB](https://projects.eclipse.org/projects/ee4j.jaxb-impl)


## License

Eclipse Implementation of JAXB is licensed under a license - [EDL 1.0](LICENSE.md).


## Contributing

We use [contribution policy](CONTRIBUTING.md), which means we can only accept contributions under
the terms of [Eclipse Contributor Agreement](http://www.eclipse.org/legal/ECA.php).


## Links

* [Home page](https://eclipse-ee4j.github.io/jaxb-ri/)
* [Mailing list](https://accounts.eclipse.org/mailing-list/jaxb-impl-dev)
* [Jakarta XML Binding Specification](https://jakarta.ee/specifications/xml-binding)
* [Jakarta XML Binding Specification project](https://github.com/eclipse-ee4j/jaxb-api)
* [Nightly build job](https://ci.eclipse.org/jaxb-impl/job/jaxb-ri-master-build/)


## Other projects in this repository:

### jaxb-ri/codemodel
Codemodel is an independent library for java code generation, which can be released separately.
Besides here it is used in [istack-commons-project](https://github.com/eclipse-ee4j/jaxb-istack-commons),
which in turn is a dependency of JAXB

### jaxb-ri/xsom
XML Schema Object Model (XSOM) is a Java library that allows applications to easily parse XML Schema
documents and inspect information in them. It can be released separately to satisfy a dependency of
[jaxb-fi](https://github.com/eclipse-ee4j/jaxb-fi), which in turn is optional dependency of JAXB

### jaxb-ri/external
RNGOM and relaxng-datatype under jaxb-ri/external are tools for working with RelaxNG. Both can be released
separately to break circular dependency between several jaxb-ri sub-projects including jaxb-ri/xsom above.
