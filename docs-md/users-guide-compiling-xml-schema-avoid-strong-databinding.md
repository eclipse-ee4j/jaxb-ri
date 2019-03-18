Avoid strong databinding {#compiling-xml-schema-avoid-strong-databinding}
========================

Under some limited circumstances, a weaker databinding is preferable for
various reasons. JAXB does offer a few ways for you to achieve this.

Avoid mapping to enum {#Avoid_mapping_to_enum}
---------------------

The following customization will stop binding a simple type to a
type-safe enum. This can be convenient when number of constants is too
large to be an useful enum (by default, the JAXB spec won\'t generate
enum with more than 256 constants, but even 100 might be too large for
you.)

``` {.xml}
<xs:simpleType name="foo">
  <xs:annotation><xs:appinfo>
    <jaxb:typesafeEnumClass map="false" />
  </xs:appinfo></xs:annotation>
  <xs:restriction base="xs:string">
    <xs:enumeration value="x" />
    <xs:enumeration value="y" />
    <xs:enumeration value="z" />
  </xs:restriction>
</xs:simpleType>
```

To disable such type-safe enum binding altogether for the entire schema,
use a global binding setting like this (this is actually telling XJC not
to generate enums if a simple type has more than 0 constants \-\-- the
net effect is no enum generation):

``` {.xml}
<xs:schema ...>
  <xs:annotation><xs:appinfo>
    <jaxb:globalBindings typesafeEnumMaxMembers="0" />
  </xs:appinfo></xs:annotation>
  ...
</xs:schema>
```

Mapping to DOM {#Mapping_to_DOM}
--------------

The `<jaxb:dom>`customization allows you to map a certain part of the
schema into a DOM tree. This customization can be attached to the
following schema components:

-   Wildcards (`<xs:any>`)

-   Type definitions (`<xs:complexType>` and `<xs:simpleType>`)

-   Model groups (`<xs:choice>`,`<xs:all>`,`<xs:sequence>`)

-   Model group declarations (`<xs:group>`)

-   Particles

-   Element declarations (`<xs:element>`)

In the following example, a wildcard is mapped to a DOM node. Each
element that matches to the wildcard will be turned into a DOM tree.

``` {.xml}
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
               xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
               jaxb:version="2.0">

        <xs:element>
           <xs:complexType>
              <xs:sequence>
                 <xs:any maxOccurs="unbounded" processContents="skip">
                    <xs:annotation><xs:appinfo>
                      <jaxb:dom/>
                    </xs:appinfo></xs:annotation>
                 </xs:any>
              </xs:sequence>
           </xs:complexType>
        </xs:element>
    .
    .
    .
    </xs:schema>
```

This extension can be used to access wildcard content or can be used to
process a part of a document by using other technologies that require
\"raw\" XML. By default, JAXB generates a `getContent()` method for
accessing wildcard content, but it only supports \"lax\" handling which
means that unknown content is discarded. You may find more information
in 7.12 chapter of [JAXB 2
specification](http://www.jcp.org/en/jsr/detail?id=222).
