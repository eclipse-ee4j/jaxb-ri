Interaction between marshalling and DOM {#marshalling-interaction-between-marshalling-and-dom}
=======================================

Sometimes you may notice that JAXB is producing XML with seemingly
unnecessary namespace declarations. In this section, we\'ll discuss the
possible causes and how to resolve this.

Caused by DOM mapping {#Caused_by_DOM_mapping}
---------------------

The \#1 cause of extra namespace declarations is due to the DOM mapping.
This mainly happens because of a schema construct that forces XJC to
generate a property with DOM. This includes the use of wildcard
`<xs:any/>` (see more about this
[???](#compiling-xml-schema-mapping-of-xs-any)), as well as `xs:anyType`
(which can also happen by omission, such as `<xs:element
        name="foo"/>`, which is interpreted as `<xs:element
        name="foo" type="xs:anyType" />`.

During unmarshalling, when a subtree of the input XML is converted into
XML, JAXB copies all the in-scope namespace bindings active at that time
to the root of the DOM element. So for example, given the following Java
class and XML, the DOM tree that the `child` field will get will look
like the following:

``` {.java}
@XmlRootElement
class Foo {
  @XmlAnyElement
  public Element child;
}
```

``` {.xml}
<foo xmlns:a="a" xmlns:b="b" xmlns:c="c">
  <subtree xmlns:c="cc">
    <data>a:xyz</data>
  </subtree>
</foo>
```

``` {.xml}
<subtree xmlns:a="a" xmlns:b="b" xmlns:c="cc">
    <data>a:xyz</data>
  </subtree>
```

Note that the two namespace declarations are copied over, but `c` is not
because it\'s overridden. Also not that JAXB is not touching the
whitespace in document. This copying of namespace declarations is
necessary to preserve the infoset in the input document. For example, if
the `<data>` is a QName, its meaning would change if JAXB unmarshaller
doesn\'t copy it.

Now, imagine what happens when you marshal this back to XML. Despite the
fact that in this example neither `b` nor `c` prefixes are in use, JAXB
cannot delete them, because it doesn\'t know if those attributes are
significant to the application or not. Therefore, this could end up
producing XML with \"extra namespace declarations\" like:

``` {.xml}
<foo>
  <subtree xmlns:a="a" xmlns:b="b" xmlns:c="cc">
    <data>a:xyz</data>
  </subtree>
</foo>
```

Resolving this problem is not possible in the general case, but
sometimes one of the following strategy works:

1.  Sometimes schema author incorrectly assumes that
    `<xs:element name="foo"/>` means
    `<xs:element name="foo" type="xs:string"/>`, because attribute
    declarations work somewhat like this. In such a case, adding
    explicit `type` attribute avoids the use of DOM, so things will work
    as expected.

2.  The wildcard processing mode \" `strict`\" would force a typed
    binding, and thereby eliminate any DOM mapping.

3.  You might be able to manulally go into the DOM tree and remove
    unnecessary namespace declarations, if your application knows what
    are necessary and what are not.
