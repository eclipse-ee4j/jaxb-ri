# JAXB

The Java™ Architecture for XML Binding (JAXB) provides an API and tools that automate the mapping between XML documents and Java objects.

The JAXB framework enables developers to perform the following operations:
- Unmarshal XML content into a Java representation
- Access and update the Java representation
- Marshal the Java representation of the XML content into XML content

JAXB gives Java developers an efficient and standard way of mapping between XML and Java code. Java developers using JAXB are more productive because they can write less code themselves and do not have to be experts in XML. JAXB makes it easier for developers to extend their applications with XML and Web Services technologies.

:warning: This project is now part of the EE4J initiative. This repository
has been archived as all activities are now happening in the 
[corresponding Eclipse repository](https://github.com/eclipse-ee4j/jaxb-ri).
See [here](https://www.eclipse.org/ee4j/status.php) for the overall
EE4J transition status.

# jaxb-ri/codemodel
Codemodel is an independent library for java code generation, which is released separately and integrated in JAXB. 
Besides JAXB it is used in [istack-commons-project](https://github.com/eclipse-ee4j/jaxb-istack-commons), 
which in turn is a dependency of JAXB

# jaxb-ri/xsom
XML Schema Object Model (XSOM) is a Java library that allows applications to easily parse XML Schema
documents and inspect information in them. It is released separately because it is a dependency of 
[jaxb-fi](https://github.com/eclipse-ee4j/jaxb-fi),
which in turn is a dependency of JAXB

# jaxb-ri/external
RNGOM and relaxng-datatype under jaxb-ri/external are tools for working with RelaxNG. It is released separately because it is a dependency of 
several jaxb-ri projects including jaxb-ri/xsom above.

## Documentation
Documentation for this release consists of the following:
- [Release Notes](https://javaee.github.io/jaxb-v2/doc/user-guide/ch02.html)
- Running the binding compiler (XJC): [XJC, XJC Ant Task](https://javaee.github.io/jaxb-v2/doc/user-guide/ch04.html#tools-xjc-ant-task)
- Running the schema generator (schemagen): [SchemaGen, SchemaGen Ant Task](https://javaee.github.io/jaxb-v2/doc/user-guide/ch04.html#tools-schemagen-ant-task)
- [JAXB Users Guide](https://javaee.github.io/jaxb-v2/doc/user-guide/ch03.html)
- [Sample Apps](https://javaee.github.io/jaxb-v2/doc/user-guide/ch01.html#jaxb-2-0-sample-apps)
- JAXB FAQs [Frequently Asked Questions](https://javaee.github.io/jaxb-v2/doc/user-guide/ch06.html)

## Licensing and Governance

JAXB is licensed under Eclipse Distribution License v. 1.0, which is available at http://www.eclipse.org/org/documents/edl-v10.php. 

We use [GlassFish Governance Policy](https://javaee.github.io/jaxb-v2/CONTRIBUTING), 
which means we can only accept contributions under the 
terms of [OCA](http://oracle.com/technetwork/goto/oca).

## Links
- Please use [Metro and JAXB](https://javaee.groups.io/g/metro) forum for feedback
- JAXB-RI project home page: [https://javaee.github.io/jaxb-v2/](https://javaee.github.io/jaxb-v2/)
- [METRO project](https://javaee.github.io/metro)
- [JSR 222](https://jcp.org/en/jsr/detail?id=222)
- [Download standalone distribution](https://repo1.maven.org/maven2/com/sun/xml/bind/jaxb-ri/2.3.0/jaxb-ri-2.3.0.zip)
