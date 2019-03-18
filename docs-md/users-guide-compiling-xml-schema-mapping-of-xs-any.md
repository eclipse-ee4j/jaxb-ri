Mapping of `<xs:any />` {#compiling-xml-schema-mapping-of-xs-any}
=======================

XJC binds `<xs:any />` in the following ways:

`processContents="skip"` {#processContents__skip_}
------------------------

`<xs:any />` with `processContents=skip` means any well-formed XML
elements can be placed. Therefore, XJC binds this to DOM `Element`
interface.

``` {.xml}
<xs:element name="person">
  <xs:complexType>
    <xs:sequence>
      <xs:element name="name" type="xs:string" />
      <xs:any processContents="skip" maxOccurs="unbounded" minOccurs="0" />
    </xs:sequence>
  </xs:complexType>
</xs:element>
```

``` {.java}
import org.w3c.dom.Element;

@XmlRootElement
class Person {
  public String getName();
  public void setName(String);

  @XmlAnyElement
  public List<Element> getAny();
}
```

`processContents="strict"` {#processContents__strict_}
--------------------------

`<xs:any />` with `processContents=strict` (or `<xs:any />` without any
processContents attribute, since it defaults to \"strict\") means any
XML elements placed here must have corresponding schema definitions.
This mode is not what people typically expect as \"`wildcard`\", but
this is the default. The following shows this binding. (`lax=true` is
unintuitive, but it\'s not an error in this document):

``` {.xml}
<xs:element name="person">
  <xs:complexType>
    <xs:sequence>
      <xs:element name="name" type="xs:string" />
      <xs:any maxOccurs="unbounded" minOccurs="0" />
    </xs:sequence>
  </xs:complexType>
</xs:element>
```

``` {.java}
@XmlRootElement
class Person {
  public String getName();
  public void setName(String);

  @XmlAnyElement(lax=true)
  public List<Object> getAny();
}
```

JAXB binds any such element to an `Object`, and during unmarshalling,
all elements encountered are unmarshalled into corresponding JAXB
objects (including `JAXBElement`s if necessary) and placed in this
field. If it encounters elements that cannot be unmarshalled, DOM
elements are produced instead.

At runtime, you can place either DOM elements or some JAXB objects that
map to elements. A typical mistake is to put a `String` that contains
XML fragment, but this won\'t work; you\'d have to first read that into
a DOM.

`processContents="lax"` {#processContents__lax_}
-----------------------

`<xs:any />` with `processContents=lax` means any XML elements can be
placed here, but if their element names match those defined in the
schema, they have to be valid. XJC actually handles this exactly like
processContents=\'strict\', since the strict binding allows unknown
elements anyway.
