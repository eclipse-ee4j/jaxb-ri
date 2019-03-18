Compiling DTD {#other-miscellaneous-topics-compiling-dtd}
=============

The JAXB RI is shipped with an \"experimental\" DTD support, which
let\'s you compile XML DTDs. It is marked \"experimental\" not because
the feature is unstable nor unreliable, but rather because it\'s not a
part of the JAXB specification and therefore the level of commitment to
compatibility is lower.

    $ xjc.sh -dtd test.dtd

All the other command-line options of the XJC binding compiler can be
applied. Similarly, the XJC ant task supports DTD. The generated code
will be no different from what is generated from W3C XML Schema. You\'ll
use the same JAXB API to access the generated code, and it is portable
in the sense that it will run on any JAXB 2.0 implementation.

DTD long predates XML namespace, although people since then developed
various techniques to use XML namespaces in conjunction with DTD.
Because of this, XJC is currently unable to reverse-engineer the use of
XML namespace from DTD. If you compile DTDs that use those techniques,
you\'d either manuallly modify the generated code, or you can try a tool
like [Trang](http://www.thaiopensource.com/relaxng/trang.html) that can
convert DTD into XML Schema in ways that better preserves XML
namespaces.

Customizations {#Customizations}
--------------

The customization syntax for DTD is roughly based on the ver.0.21
working draft of the JAXB specification, which is available at
[xml.coverpages.org](http://xml.coverpages.org/jaxb0530spec.pdf). The
deviations from this document are:

-   The `whitespace` attribute of the `conversion` element takes \"
    `preserve`\", \" `replace`\", and \" `collapse`\" instead of \"
    `preserve`\", \" `normalize`\", and \" `collapse`\" as specified in
    the document.

-   The `interface` customization just generates marker interfaces with
    no method.

Compiling DTD from Maven2 {#Compiling_DTD_from_Maven2}
-------------------------

``` {.xml}
<plugin>
  <groupId>org.jvnet.jaxb2.maven2</groupId>
  <artifactId>maven-jaxb2-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>generate</goal>
      </goals>
      <configuration>
        <!--  if you want to put DTD somewhere else
        <schemaDirectory>src/main/jaxb</schemaDirectory>
        -->
        <extension>true</extension>
        <schemaLanguage>DTD</schemaLanguage>
        <schemaIncludes>
          <schemaInclude>*.dtd</schemaInclude>
        </schemaIncludes>
        <bindingIncludes>
          <bindingInclude>*.jaxb</bindingInclude>
        </bindingIncludes>
        <args>
          <arg>-Xinject-listener-code</arg>
        </args>
      </configuration>
    </execution>
  </executions>
  <dependencies>
    <dependency>
      <groupId>org.jvnet.jaxb2-commons</groupId>
      <artifactId>property-listener-injector</artifactId>
      <version>1.0</version>
    </dependency>
  </dependencies>
</plugin>
```

``` {.xml}
<dependency>
  <groupId>com.sun.xml.bind</groupId>
  <artifactId>jaxb-xjc</artifactId>
  <version>2.1.2</version>
</dependency>
```
