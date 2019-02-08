Element default values and marshalling {#marshalling-element-default-values-and-marshalling}
======================================

Because of a \"strange\" way element default values in XML Schema work,
people often get confused about its behavior. This section describes how
this works.

When a class has an element property with the default value, and if a
value is null, then the marshaller will not produce the corresponding
element in XML:

``` {.java}
@XmlRootElement
class Foo {
  @XmlElement(defaultValue="value") public String a=null;
}

marshaller.marshal(new Foo(),System.out);
```

``` {.xml}
<foo />
```

This is consistent with the XML Schema spec, where it essentially states
that the element defaults do not kick in when the element is absent.
Attribute default values do not have this problem, so if you can change
the schema, changing it to an attribute is usually a better idea.
Alternatively, depending on your expectation, setting the field to a
default value in Java may achieve what you are looking for.

``` {.java}
@XmlRootElement
class Foo {
  @XmlElement public String a="value";
}
@XmlRootElement
class Bar {
  @XmlAttribute public String a;
}

marshaller.marshal(new Foo(),System.out);
marshaller.marshal(new Bar(),System.out);
```

``` {.xml}
<foo>
    <a>value</a>
</foo>

<bar/>
```

Also, see
[???](#unmarshalling-element-default-values-and-unmarshalling).
