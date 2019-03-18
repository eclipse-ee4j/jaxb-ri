Using JAXB with Maven {#deployment-maven-coordinates}
=====================

Maven coordinates for JAXB artifacts {#Maven_artifacts}
------------------------------------

-   **java.xml.bind:jaxb-api** API classes for JAXB. Required to compile
    against JAXB.

-   **org.glassfish.jaxb:jaxb-runtime** Contains the main runtime used
    for serialization and deserialization java objects to/from xml.

-   **org.glassfish.jaxb:jaxb-xjc:** Tool to generate JAXB java sources
    from XML representation.

-   **org.glassfish.jaxb:jaxb-jxc:** Tool to generate XML schema from
    JAXB java sources.

-   **com.sun.xml.bind:jaxb-ri:** Zip distribution containing tooling
    scripts and all dependency jars in one archive.

JAXB API and Runtime {#Api_and_runtime}
--------------------

Minimum requirement to compile is jaxb-api. If a client application is
running on an environment where JAXB runtime is provided, `jaxb-api` is
all that is needed.

``` {.xml}
                <!-- API -->
                <dependency>
                    <groupId>jakarta.xml.bind</groupId>
                    <artifactId>jakarta.xml.bind-api</artifactId>
                    <version>${jaxb.version}</version>
                </dependency>
             
```

If client application needs to include the runtime, e.g. running
standalone on JavaSE `jaxb-runtime` should be also included.

``` {.xml}
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

Using JAXB tools for java sources and XML schema generation {#Jaxb_tooling}
-----------------------------------------------------------

To generate JAXB classes from schema community
[maven-jaxb2-plugin](https://github.com/highsource/maven-jaxb2-plugin)
can be used.

``` {.xml}
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

Alternatively to community plugins, there are tooling artifacts jaxb-xjc
and jaxb-jxc, which can be used for java from XML schema generation and
vice versa.

``` {.xml}
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

            <!-- Invoke tooling API (Java 11) -->
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
                                <argument>--module-path</argument> <!-- or -p  -->
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

See also [xml schema compiler
usage](#schema-generation-invoking-schemagen-programatically).

Where are schemagen and xjc command line scripts available in JavaSE
prior to 11? These are included only in the [zip
distribution.](#Maven_artifacts)
