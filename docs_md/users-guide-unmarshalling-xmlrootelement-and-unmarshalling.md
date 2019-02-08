`@XmlRootElement` and unmarshalling {#unmarshalling-xmlrootelement-and-unmarshalling}
===================================

Classes with `XmlRootElement` can be unmarshalled from XML elements
simply by invoking the unmarshal method that takes one parameter. This
is the simplest mode of unmarshalling.

Unmarshalling with `@XmlRootElement`

::: {.informalexample}
``` {.java}
@XmlRootElement
class Foo {
  @XmlAttribute
  String name;
  @XmlElement
  String content;
}

Unmarshaller u = ...;
Foo foo = (Foo)u.unmarshal(new File("foo.xml"));
```
:::

``` {.xml}
<foo name="something">
  <content>abc</content>
</foo>
```

However, sometimes you may need to unmarshal an instance of a type that
does not have an `XmlRootElement`. For example, you might dynamically
find out at the runtime that a certain element has a certain type. For
example, the following document illustrates an XML instance where the
content of `<someOtherTagName>` element is represented by the `Foo`
class.

``` {.xml}
<someOtherTagName name="something">
  <content>abc</content>
</someOtherTagName>
```

To unmarshal this into a `Foo` class, use the version of the `unmarshal`
method that takes the \'expectedType\' argument, as follows:

``` {.java}
Unmarshaller u = ...;
JAXBElement<Foo> root = u.unmarshal(new StreamSource(new File("foo.xml")),Foo.class);
Foo foo = root.getValue();
```

To reduce the number of the `unmarshal` methods, this two-argument
version is not defined for every single-argument version. So as in this
example, you might need to perform additional wrapping of the input
parameter.

This instructs JAXB that the caller is expecting to unmarshal `Foo`
instance. JAXB returns a `JAXBElement` of `Foo`, and this `JAXBElement`
captures the tag name of the root element.
