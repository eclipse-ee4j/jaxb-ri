[![Build Status](https://travis-ci.com/eclipse-ee4j/jaxb-ri.svg?branch=master)](https://travis-ci.com/eclipse-ee4j/jaxb-ri)

# JAXB

The Java™ Architecture for XML Binding (JAXB) provides an API and tools that automate the mapping between XML documents and Java objects.

The JAXB framework enables developers to perform the following operations:
- Unmarshal XML content into a Java representation
- Access and update the Java representation
- Marshal the Java representation of the XML content into XML content

JAXB gives Java developers an efficient and standard way of mapping between XML and Java code. Java developers using JAXB are more productive because they can write less code themselves and do not have to be experts in XML. JAXB makes it easier for developers to extend their applications with XML and Web Services technologies.

## Links
- [JAXB site and documentation](https://eclipse-ee4j.github.io/jaxb-ri/) 
- [Eclipse JAXB Implementation project](https://projects.eclipse.org/projects/ee4j.jaxb-impl)
- [JAXB mailing list](https://accounts.eclipse.org/mailing-list/jaxb-dev)
- [JSR 222](https://jcp.org/en/jsr/detail?id=222)
- [Download standalone distribution](https://repo1.maven.org/maven2/com/sun/xml/bind/jaxb-ri/2.3.2/jaxb-ri-2.3.2.zip)


## Other JAXB related projects in this repository:

## jaxb-ri/codemodel
Codemodel is an independent library for java code generation, which is released separately and integrated in JAXB. 
Besides JAXB it is used in [istack-commons-project](https://github.com/eclipse-ee4j/jaxb-istack-commons), 
which in turn is a dependency of JAXB

## jaxb-ri/xsom
XML Schema Object Model (XSOM) is a Java library that allows applications to easily parse XML Schema
documents and inspect information in them. It is released separately because it is a dependency of 
[jaxb-fi](https://github.com/eclipse-ee4j/jaxb-fi),
which in turn is a dependency of JAXB

## jaxb-ri/external
RNGOM and relaxng-datatype under jaxb-ri/external are tools for working with RelaxNG. It is released separately because it is a dependency of 
several jaxb-ri projects including jaxb-ri/xsom above.

## Licensing and Governance

JAXB is licensed under Eclipse Distribution License v. 1.0, which is available at http://www.eclipse.org/org/documents/edl-v10.php. 

We use Eclipse Contributor Agreement and can only accept contributions under the 
terms of [ECA](https://www.eclipse.org/legal/ECA.php).

