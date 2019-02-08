Different ways of marshalling {#marshalling-different-ways-of-marshalling}
=============================

Different output media {#Different_output_media}
----------------------

The most basic notion of the marshalling is to take a JAXB-bound object
that has `@XmlRootElement`, and write it out as a whole XML document. So
perhaps you have a class like this:

``` {.java}
class Point {
  @XmlElement
  public int x;
  @XmlElement
  public int y;
  Point(...) { ... }
}
```

Then you can do:

``` {.java}
marshaller.marshal( new Point(1,3), System.out );
marshaller.marshal( new Point(1,3), new File("out.xml") );
```

.. and so on. There\'re seven `Marshaller.marshal` methods that takes
different output media as the second parameter. If you are writing to a
file, a socket, or memory, then you should use the version that takes
`OutputStream`. Unless you change the target encoding to something else
(default is UTF-8), there\'s a special marshaller codepath for
`OutputStream`, which makes it run really fast. You also don\'t have to
use `BufferedOutputStream`, since the JAXB RI does the adequate
buffering.

You can also write to `Writer`, but in this case you\'ll be responsible
for encoding characters, so in general you need to be careful. If you
want to marshal XML into an encoding other than UTF-8, it\'s best to use
the `JAXB_ENCODING` property and then write to `OutputStream`, as it
escapes characters to things like `&#x1824;` correctly.

The next medium we support is W3C DOM. This is bit unintuitive, but
you\'ll do it like this:

``` {.java}
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
dbf.setNamespaceAware(true);
Document doc = dbf.newDocumentBuilder().newDocument();

marshaller.marshal( new Point(1,3), doc );
```

And after the method invocation you get a complete DOM tree that
represents the marshalled document.

The other versions of the marshal methods are there to write XML
documents in terms of other XML APIs, such as SAX and StAX. The version
that takes `ContentHandler` is useful when you need a custom formatting
needs (like you want each attribute to be in new line, etc), but
otherwise they are not very interesting if you are writing a whole
document.

Marshalling into a subtree {#Marshalling_into_a_subtree}
--------------------------

Another common use of JAXB is where you are writing a bigger document,
and you use JAXB to generate part(s) of it. The JAX-WS RI is the prime
example. It produces a SOAP message, and JAXB is only used to produce
the body. When you are doing this, you first set `JAXB_FRAGMENT`
property on the marshaller. This changes the behaviors of the marshaller
so that it works better in this situation.

If you are writing to an `OutputStream` or `Writer` and generally
sending it to someone else, you can do something like this:

``` {.java}
System.out.println("<envelope>");
marshaller.marshal( object, System.out );
System.out.println("</envelope>");
```

Like I mentioned, this is probably the fastest, even though `println`
isn\'t very pretty. `JAXB_FRAGMENT` prevents the marshaller from
producing an XML declaration, so the above works just fine. The downside
of this approach is that if the ancestor elements declare the
namespaces, JAXB won\'t be able to take advantage of them.

You can also marshal an object as a subtree of an existing DOM tree. To
do this, you pass the `Element` object as the second parameter, and the
marshaller will marshal an object as a child of this node.

StAX is also very convenient for doing this sort of things. You can
create `XMLStreamWriter`, write some stuff, and then pass that to the
marshaller. `JAXB_FRAGMENT` prevents the marshaller from producing
`startDocument` and `endDocument` token. When doing this sub-tree
marshaling to DOM and StAX, JAXB can take advantage of available
in-scope namespace bindings.

Finally, you can marshal an object as a subtree into `ContentHandler`,
but it requires a fair amount of SAX programming experience, and it goes
beyond the scope of this entry.

Marshalling a non-element {#Marshalling_a_non_element}
-------------------------

Another common use case is where you have an object that doesn\'t have
`@XmlRootElement` on it. JAXB allows you to marshal it like this:

``` {.java}
marshaller.marshal( new JAXBElement(
  new QName("","rootTag"),Point.class,new Point(...)));
```

This puts the `<rootTag>` element as the root element, followed by the
contents of the object, then `</rootTag>`. You can actually use it with
a class that has `@XmlRootElement`, and that simply renames the root
element name.

At the first glance the second `Point.class` parameter may look
redundant, but it\'s actually necessary to determine if the marshaller
will produce (infamous) `@xsi`:type. In this example, both the class and
the instance are `Point`, so you won\'t see `@xsi`:type. But if they are
different, you\'ll see it.

This can be also used to marshal a simple object, like `String` or an
integer.

Marshalling a non-element with `@xsi`:type

::: {.informalexample}
``` {.java}
marshaller.marshal( new JAXBElement(
  new QName("","rootTag"),String.class,"foo bar"));
```
:::

But unfortunately it **cannot** be used to marshal objects like `List`
or `Map`, as they aren\'t handled as the first-class citizen in the JAXB
world.

Connecting to other XML APIs {#Connecting_to_other_XML_APIs}
----------------------------

Because of the `Source` and `Result` support, JAXB objects can be easily
marshalled into other XML APIs that are not mentioned here. For example,
[dom4j](http://www.dom4j.org/) has `DocumentResult` that extends
`Result`, so you can do:

``` {.java}
DocumentResult dr = new DocumentResult();
marshaller.marshal( object, dr );
o = dr.getDocument();
```

Similar mechanism is available for JDOM and XOM. This conversion is much
more efficient than first marshalling to `ByteArrayOutputStream` and
then read it back into these DOMs. The same mechanism can be used to
marshal to FastInfoset or send the marshaled document to an XSLT engine
(`TransformerHandler`.)

The other interesting connector is `JAXBSource`, which wraps a
marshaller and allows a JAXB object to be used as a \"source\" of XML.
Many XML APIs take `Source` as an input, and now JAXB object can be
passed to them directly.

For example, you can marshal a JAXB object and unmarshal it into another
JAXBContext like this:

``` {.java}
JAXBContext context1 = ... ;
JAXBContext context2 = ... ;

context1.createUnmarshaller().unmarshal( new JAXBSource(context2,object) );
```

This amounts to looking at the same XML by using different schema, and
again this is much more efficient than going through
`ByteArrayOutputStream`.
