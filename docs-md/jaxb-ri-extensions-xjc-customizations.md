XJC Customizations {#jaxb-ri-extensions-xjc-customizations}
==================

Customizations {#section-3752096477276927}
--------------

The JAXB RI provides additional customizations that are not defined by
the JAXB specification. Note the following:

-   These features may only be used when the JAXB XJC binding compiler
    is run in the `-extension` mode.

-   All of the JAXB RI vendor extensions are defined in the
    \"`http://java.sun.com/xml/ns/jaxb/xjc`\" namespace.

-   The namespaces containing extension binding declarations are
    specified to a JAXB processor by the occurrence of the global
    attribute `@jaxb:extensionBindingPrefixes` within an instance of
    `<xs:schema>` element. The value of this attribute is a
    whitespace-separated list of namespace prefixes. For more
    information, please refer to section 6.1.1 of the JAXB
    Specification.

### Index of Customizations {#section-811903782726232}

-   [SCD Support](#scd)

-   [Extending a Common Super Class](#superclass) - Extending a Common
    Super Class

-   [Extending a Common Super Interface](#superinterface) - Extending a
    Common Super Interface

-   [Enhanced \<jaxb:javaType\>](#javatype) - Enhanced \<jaxb:javaType\>
    customization

-   [Experimental simpler & better binding mode](#simple) - Experimental
    simpler & better binding mode

-   [Alternative Derivation-by-restriction Binding
    Mode](#treatrestrictionlikenewtype) - Alternative
    derivation-by-restriction binding mode

-   [Allow separate compilations to perform element
    substitutions](#substitutable) - Allow separate compilations to
    perform element substitutions

### SCD Support {#scd}

The JAXB RI supports the use of [schema component
designator](http://www.w3.org/TR/2005/WD-xmlschema-ref-20050329/) as a
means of specifying the customization target (of all standard JAXB
customizations as well as vendor extensions explained below.) To use
this feature, use the `scd` attribute on \<bindings\> element instead of
the `schemaLocation` and `node` attributes.

::: {.informalexample}
``` {.xml}
<bindings xmlns:tns="http://example.com/myns"
          xmlns="http://java.sun.com/xml/ns/jaxb" version="2.1">
    <bindings
            ...
            scd="tns:foo">
        <!-- this customization applies to the global element declaration -->
        <!-- 'foo' in the http://example.com/myns namespace -->
        <class name="FooElement"/>
    </bindings>
    <bindings
            ...
            scd="~tns:bar">
        <!-- this customization applies to the global type declaration -->
        <!-- 'bar' in the http://example.com/myns namespace -->
        <class name="BarType"/>
    </bindings>
</bindings>  
```
:::

Compared to the standard XPath based approach, SCD allows more robust
and concise way of identifying a target of a customization. For more
about SCD, refer to the scd example. Note that SCD is a W3C working
draft, and may change in the future.

### Extending a Common Super Class {#superclass}

The `<xjc:superClass>` customization allows you to specify the fully
qualified name of the Java class that is to be used as the super class
of all the generated implementation classes. The `<xjc:superClass>`
customization can only occur within your `<jaxb:globalBindings>`
customization on the `<xs:schema>` element:

::: {.informalexample}
``` {.xml}
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
           xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
           jaxb:extensionBindingPrefixes="xjc"
           jaxb:version="2.0">

    <xs:annotation>
        <xs:appinfo>
            <jaxb:globalBindings>
                <xjc:superClass
                        name="org.acme.RocketBooster"/>
            </jaxb:globalBindings>
        </xs:appinfo>
    </xs:annotation>
    
    ...
    
</xs:schema>
```
:::

In the sample above, the `<xjc:superClass>` customization will cause all
of the generated implementation classes to extend the named class,
`org.acme.RocketBooster`.

### Extending a Common Super Interface {#superinterface}

The `<xjc:superInterface>` customization allows you to specify the fully
qualified name of the Java interface that is to be used as the root
interface of all the generated interfaces. The `<xjc:superInterface>`
customization can only occur within your `<jaxb:globalBindings>`
customization on the `<xs:schema>` element:

::: {.informalexample}
``` {.xml}
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
           xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
           jaxb:extensionBindingPrefixes="xjc"
           jaxb:version="2.0">

    <xs:annotation>
        <xs:appinfo>
            <jaxb:globalBindings>
                <xjc:superInterface
                        name="org.acme.RocketBooster"/>
            </jaxb:globalBindings>
        </xs:appinfo>
    </xs:annotation>

    ...

</xs:schema>
```
:::

In the sample above, the `<xjc:superInterface>` customization will cause
all of the generated interfaces to extend the named interface,
`org.acme.RocketBooster`.

### Enhanced \<jaxb:javaType\> {#javatype}

The \<xjc:javaType\> customization can be used just like the standard
\<jaxb:javaType\> customization, except that it allows you to specify an
`XmlAdapter`-derived class, instead of parse&print method pair.

This customization can be used in all the places \<jaxb:javaType\> is
used, but nowhere else:

::: {.informalexample}
``` {.xml}
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
           xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
           jaxb:extensionBindingPrefixes="xjc"
           jaxb:version="2.0">
    
    ...
    
    <xsd:simpleType name="LayerRate_T">
        <xsd:annotation>
            <xsd:appinfo>
                <xjc:javaType name="org.acme.foo.LayerRate"
                              adapter="org.acme.foo.LayerRateAdapter"/>
            </xsd:appinfo>
        </xsd:annotation>
        
        ... gory simple type definition here ...
        
    </xsd:simpleType>
</xsd:schema>
```
:::

In the above example, `LayerRate_T` simple type is adapted by
`org.acme.foo.LayerRateAdapter`, which extends from `XmlAdapter`.

::: {.informalexample}
``` {.xml}
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
           xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
           jaxb:extensionBindingPrefixes="xjc"
           jaxb:version="2.0">
    
    <xsd:annotation>
        <xsd:appinfo>
            <jaxb:globalBindings>
                <xjc:javaType name="org.acme.foo.MyDateType"
                              xmlType="xsd:dateTime"
                              adapter="org.acme.foo.MyAdapterImpl"/>
            </jaxb:globalBindings>
        </xsd:appinfo>
    </xsd:annotation>

    ...
    
</xsd:schema>
```
:::

In the above example, all the use of `xsd:dateTime` type is adapter by
`org.acme.foo.MyAdapterImpl` to `org.acme.foo.MyDateType`

### Experimental simpler & better binding mode {#simple}

This experimental binding mode can be enabled as a part of the global
binding. See below:

::: {.informalexample}
``` {.xml}
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
           xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
           jaxb:extensionBindingPrefixes="xjc"
           jaxb:version="2.0">

    <xs:annotation>
        <xs:appinfo>
            <jaxb:globalBindings generateValueClass="false">
                <xjc:simple/>
            </jaxb:globalBindings>
        </xs:appinfo>
    </xs:annotation>

    ...

</xs:schema>
```
:::

When enabled, XJC produces Java source code that are more concise and
easier to use. Improvements include:

1.  Some content model definitions, such as `A,B,A`, which used to cause
    an XJC compilation error and required manual intervention, now
    compile out of the box without any customization.

2.  Some content model definitions that used to bind to a non-intuitive
    Java class now binds to a much better Java class:

    ::: {.informalexample}
    ``` {.xml}
    <!-- schema -->
    <xs:complexType name="foo">
        <xs:choice>
            <xs:sequence>
                <xs:element name="a" type="xs:int"/>
                <xs:element name="b" type="xs:int"/>
            </xs:sequence>
            <xs:sequence>
                <xs:element name="b" type="xs:int"/>
                <xs:element name="c" type="xs:int"/>
            </xs:sequence>
        </xs:choice>
    </xs:complexType>
    ```

    ``` {.java}
    // before
    class Foo {
        List<JAXBElement<Integer>> content;
    }

    // in <xjc:simple> binding
    class Foo {
        Integer a;
        int b; // notice that b is effectively mandatory, hence primitive
        Integer c;
    }
    ```
    :::

3.  When repetable elements are bound, the method name will become
    plural.

    ::: {.informalexample}
    ``` {.xml}
    <!-- schema -->
    <xs:complexType name="person">
        <xs:sequence>
            <xs:element name="child" type="xs:string"
                        maxOccurs="unbounded"/>
            <xs:element name="parent" type="xs:string"
                        maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
    ```

    ``` {.java}
    // before
    public class Person {
        protected List<String> child;
        protected List<String> parent;
    }

    // in <xjc:simple> binding
    public class Person {
        protected List<String> children;
        protected List<String> parents;
    }     
    ```
    :::

Once again, readers are warned that this is an **experimental binding
mode**, and therefore the binding is subject to change in future
versions of the JAXB RI without notice. Please send feedbacks on this
binding to <https://javaee.groups.io/g/metro>

### Alternative Derivation-by-restriction Binding Mode {#treatrestrictionlikenewtype}

Normally, the JAXB specification requires that a
derivation-by-restriction be mapped to an inheritance betwee n two Java
classes. This is necessary to preserve the type hierarchy, but one of
the downsides is that the derived class does not really provide
easy-to-use properties that reflect the restricted content model.

This experimental \<xjc:treatRestrictionLikeNewType\> changes this
behavior by not preserving the type inheritance to Java. Instead, it
generates two unrelated Java classes, both with proper properties. For
example, given the following schema:

::: {.informalexample}
``` {.xml}
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
           jaxb:extensionBindingPrefixes="xjc"
           xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
           jaxb:version="2.0"
           elementFormDefault="qualified">

    <xs:annotation>
        <xs:appinfo>
            <jaxb:globalBindings>
                <xjc:treatRestrictionLikeNewType/>
            </jaxb:globalBindings>
        </xs:appinfo>
    </xs:annotation>

    <xs:complexType name="DerivedType">
        <xs:complexContent>
            <xs:restriction base="ResponseOptionType">
                <xs:sequence>
                    <xs:element name="foo" type="xs:string"/>
                </xs:sequence>
            </xs:restriction>
        </xs:complexContent>
    </xs:complexType>

    <xs:complexType name="ResponseOptionType">
        <xs:sequence>
            <xs:element name="foo" type="xs:string"
                        maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

</xs:schema>
```
:::

The generated `Derived` class will look like this (comment and
annotations removed for brevity):

::: {.informalexample}
``` {.java}
public class DerivedType {
    protected String foo;

    public String getFoo() { return foo; }
    public void setFoo(String value) { this.foo = value; }
} 
```
:::

In contrast, without this customization the `Derived` class would look
like the following:

::: {.informalexample}
``` {.java}
public class DerivedType extends ResponseOptionType {

    // it simply inherits List<String> ResponseOptionType.getFoo()

}
```
:::

### Allow separate compilations to perform element substitutions {#substitutable}

In an attempt to make the generated code easier to use, the JAXB
specification sometimes choose bindings based on how certain feature is
used. One of them is element substitution feature. If no actual element
substitution happens in the schema, JAXB assumes that the element is not
used for substitution, and generates code that assumes it.

Most of the time this is fine, but when you expect other \"extension\"
schemas to be compiled later on top of your base schema, and if those
extension schemas do element substitutions, this binding causes a
problem ( [see
example](https://github.com/eclipse-ee4j/jaxb-ri/issues/289).)

\<xjc:substitutable\> customization is a work around for this issue. It
explicitly tells XJC that a certain element is used for element
substitution head, even though no actual substitution might be present
in the current compilation. This customization should be attached in the
element declaration itself, like this:

::: {.informalexample}
``` {.xml}
<xs:element name="Model" type="Model">
    <xs:annotation>
        <xs:appinfo>
            <xjc:substitutable/>
        </xs:appinfo>
    </xs:annotation>
</xs:element>
```
:::
