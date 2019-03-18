Element default values and unmarshalling {#unmarshalling-element-default-values-and-unmarshalling}
========================================

Because of the \"strange\" way that element default values in XML Schema
work, people often get confused about their behavior. This section
describes how this works.

When a class has an element property with the default value, and if the
document you are reading is missing the element, then the unmarshaller
does *not* fill the field with the default value. Instead, the
unmarshaller fills in the field when the element is present but the
content is missing. See below:

``` {.xml}
<foo />
```

``` {.xml}
<foo>
  <a/>  <!-- or <a></a> -->
</foo>
```

``` {.xml}
<foo>
  <a>abc</a>
</foo>
```

``` {.java}
@XmlRootElement
class Foo {
  @XmlElement(defaultValue="value") public String a=null;
}

Foo foo = unmarshaller.unmarshal("instance1.xml");
System.out.println(foo.a);   // null

Foo foo = unmarshaller.unmarshal("instance2.xml");
System.out.println(foo.a);   // "value". The default kicked in.

Foo foo = unmarshaller.unmarshal("instance3.xml");
System.out.println(foo.a);   // "abc". Read from the instance.
```

This is consistent with the XML Schema spec, where it essentially states
that the element defaults do not kick in when the element is absent, so
unfortunately we can\'t change this behavior.

Depending on your expectation, using a field initializer may achieve
what you are looking for. See below:

``` {.java}
@XmlRootElement
class Foo {
  @XmlElement public String a="value";
}

Foo foo = unmarshaller.unmarshal("instance1.xml");
System.out.println(foo.a);   // "value", because JAXB didn't overwrite the value

Foo foo = unmarshaller.unmarshal("instance2.xml");
System.out.println(foo.a);   // "", because <a> element had 0-length string in it

Foo foo = unmarshaller.unmarshal("instance3.xml");
System.out.println(foo.a);   // "abc". Read from the instance.
```

Alternatively, attribute default values work in a way that agrees with
the typical expectation, so consider using that. Also, see
[???](#marshalling-element-default-values-and-marshalling).
