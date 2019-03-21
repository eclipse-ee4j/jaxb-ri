# JAXB

The Java™ Architecture for XML Binding (JAXB) provides an API and tools that automate the mapping between XML documents and Java objects.

The JAXB framework enables developers to perform the following operations:
- Unmarshal XML content into a Java representation
- Access and update the Java representation
- Marshal the Java representation of the XML content into XML content

JAXB gives Java developers an efficient and standard way of mapping between XML and Java code. Java developers using JAXB are more productive because they can write less code themselves and do not have to be experts in XML. JAXB makes it easier for developers to extend their applications with XML and Web Services technologies.

## Documentation
Documentation for this release consists of the following:
- [Release Notes](https://javaee.github.io/jaxb-v2/doc/user-guide/ch02.html)
- [Maven artifacts](#maven-artifacts)
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

## Build instructions
JavaSE 11 and Maven 3.3.9 are required to build JAXB.
Simply run `mvn clean install` inside jaxb-ri subdirectory.

There are three projects inside JAXB repository which are JAXB dependencies, but are not part
of the main build:

- **jaxb-ri/codemodel**:
Codemodel is an independent library for java code generation, which is released separately and integrated in JAXB. 
Besides JAXB it is used in [istack-commons-project](https://github.com/eclipse-ee4j/jaxb-istack-commons), 
which in turn is a dependency of JAXB

- **jaxb-ri/xsom**:
XML Schema Object Model (XSOM) is a Java library that allows applications to easily parse XML Schema
documents and inspect information in them. It is released separately because it is a dependency of 
[jaxb-fi](https://github.com/eclipse-ee4j/jaxb-fi),
which in turn is a dependency of JAXB

- **jaxb-ri/external**:
RNGOM and relaxng-datatype under jaxb-ri/external are tools for working with RelaxNG. It is released separately because it is a dependency of 
several jaxb-ri projects including jaxb-ri/xsom above.

## <a name="maven-artifacts"></a> Using JAXB with Maven 

### Maven coordinates for JAXB artifacts

-   **jakarta.xml.bind:jakarta.xml.bind-api**: API classes for JAXB. Required to compile
    against JAXB.

-   **org.glassfish.jaxb:jaxb-runtime**: Implementation of JAXB, runtime used
    for serialization and deserialization java objects to/from xml.

-   **org.glassfish.jaxb:jaxb-xjc**: Tool to generate JAXB java sources
    from XML representation.

-   **org.glassfish.jaxb:jaxb-jxc**: Tool to generate XML schema from
    JAXB java sources.


#### JAXB fat-jar bundles:    

-   **com.sun.xml.bind:jaxb-impl**: JAXB runtime fat jar.
-   **com.sun.xml.bind:jaxb-xjc**: Class generation tool fat jar.
-   **com.sun.xml.bind:jaxb-xjc**: Schema generation tool fat jar.

In contrast to `org.glassfish.jaxb` artifacts, these jars have all dependency classes included inside. These artifacts does not contain JPMS module descriptors.
In Maven projects `org.glassfish.jaxb` artifacts are supposed to be used instead. 


#### Binary distribution:
-   **com.sun.xml.bind:jaxb-ri**: Zip distribution containing tooling
    scripts and all dependency jars in one archive.

Old fashioned zip archive distribution.

## Examples
### JAXB API and Runtime 

Minimum requirement to compile is jaxb-api. If application is
running on an environment where JAXB runtime is provided, `jaxb-api` is
all that is needed.

```xml
<!-- API -->
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>${jaxb.version}</version>
</dependency>
```

If application needs to include the runtime, e.g. running
standalone on JavaSE `jaxb-runtime` should be also included.

```xml
<!-- API -->
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>${jaxb.version}</version>
</dependency>

<!-- Runtime -->
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>${jaxb.version}</version>
</dependency>
    
```

## Using JAXB tools with Maven

To generate JAXB classes from schema in Maven project, a community
[maven-jaxb2-plugin](https://github.com/highsource/maven-jaxb2-plugin)
can be used. 

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jvnet.jaxb2.maven2</groupId>
            <artifactId>maven-jaxb2-plugin</artifactId>
            <executions>
                <execution>
                    <id>generate</id>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
    
```
See the [maven-jaxb2-plugin documentation](https://github.com/highsource/maven-jaxb2-plugin) for configuration details.

Alternatively there are tooling artifacts jaxb-xjc and jaxb-jxc, which can be invoked manually. 

```xml
<!-- Tooling dependencies -->
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-xjc</artifactId>
    <version>${jaxb.version}</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-jxc</artifactId>
    <version>${jaxb.version}</version>
</dependency>

<!-- Invoke tooling API (on JavaSE 11) -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
        
        <!-- Generate java sources from schema -->
        <execution>
            <id>xjc</id>
            <goals>
                <goal>exec</goal>
            </goals>
            <configuration>
                <executable>java</executable>
                <arguments>
                    <argument>--module-path</argument>
                    <modulepath/>
                    <argument>-m</argument>
                    <argument>com.sun.tools.xjc/com.sun.tools.xjc.XJCFacade</argument>
                    <argument>-p</argument>
                    <argument>com.example</argument>
                    <argument>-d</argument>
                    <argument>${project.build.directory}/generated-sources</argument>
                    <argument>${project.build.directory}/classes/schema.xsd</argument>
                </arguments>
            </configuration>
        </execution>

        <!-- Generate XML Schema from sources -->
        <execution>
            <id>jxc</id>
            <goals>
                <goal>exec</goal>
            </goals>
            <configuration>
                <executable>java</executable>
                <arguments>
                    <argument>--module-path</argument>
                    <modulepath/>
                    <argument>-m</argument>
                    <argument>com.sun.tools.jxc/com.sun.tools.jxc.SchemaGeneratorFacade</argument>
                    <argument>-d</argument>
                    <argument>${project.build.directory}/generated-sources</argument>
                    <argument>${project.build.directory}/classes/com/example/Author.java</argument>
                    <argument>${project.build.directory}/classes/com/example/Book.java</argument>
                </arguments>
                <longModulepath>false</longModulepath>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Using JAXB tools with Ant

- Running the binding compiler (XJC): [XJC, XJC Ant Task](https://eclipse-ee4j.github.io/jaxb-ri/docs/ch04.html#tools-xjc-ant-task)
- Running the schema generator (schemagen): [SchemaGen, SchemaGen Ant Task](https://eclipse-ee4j.github.io/jaxb-ri/docs/ch04.html#tools-schemagen-ant-task)

See also [xml schema compiler
usage](https://eclipse-ee4j.github.io/jaxb-ri/docs/ch03.html#schema-generation-invoking-schemagen-programatically).

## XJC and Schemagen command line scripts
Where are schemagen and xjc command line scripts available in JavaSE
prior to 11? These are included only in the [JAXB zip distribution.](https://repo1.maven.org/maven2/com/sun/xml/bind/jaxb-ri/2.3.2/)
