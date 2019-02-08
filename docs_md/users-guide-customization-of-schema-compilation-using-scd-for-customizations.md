Using SCD for customizations {#customization-of-schema-compilation-using-scd-for-customizations}
============================

When using an external customization file, the JAXB spec requires that
you use XPath as a means to specify what your customization is attached
to. For example, if you want to change the class name generated from a
complex type, you\'d write something like:

``` {.xml}
<bindings xmlns="http://java.sun.com/xml/ns/jaxb" version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <bindings schemaLocation="../path/to/my.xsd" node="/xs:schema/xs:complexType[@name='foo']">
    <class name="FooType"/>
  </bindings>
</bindings>
```

While the above process works, the problem with this is that the XPath+
`schemaLocation` combo tends to be verbose and error prone. It\'s
verbose, because often a trivial target schema component like this
\"global complex type foo\" takes up a lot of characters. The xs
namespace declaration also takes up some space, although in this case we
managed to avoid declaring the \"tns\" namespace (that represents the
namespace that the schema defines.)

It\'s also error prone, because it relies on the way schema documents
are laid out, because the schemaLocation attribute needs to point to the
right schema document file. When a schema is split into multiple files
for modularity (happens especially often with large schemas), then
you\'d have to find which schema file it is. Even though you can use
relative paths, this hard-coding of path information makes it hard to
pass around the binding file to other people.

JAXB RI 2.1 and onward offers a better way to do this as a vendor
extension.

The key technology to solve this problem is a [\"`schema component
    designator`\"](http://www.w3.org/TR/xmlschema-ref/) (SCD.) This is a
path language just like XPath, but whereas XPath is designed to refer to
XML infoset items like elements and attributes, SCD is designed to refer
to schema components like element declarations or complex types.

With SCD, the above binding can be written more concisely as follows:

``` {.xml}
<bindings xmlns="http://java.sun.com/xml/ns/jaxb" version="2.1" xmlns:tns="http://my.namespace/">
  <bindings scd="/type::tns:foo">
    <class name="FooType"/>
  </bindings>
</bindings>
```

`/type::tns:foo` can be written more concisely as `/~tns:foo`, too. If
you are interested in more about the syntax of SCDs, read [the example
part of the
spec](http://www.w3.org/TR/xmlschema-ref/#section-path-examples), and
maybe [EBNF](http://www.w3.org/TR/xmlschema-ref/#section-path-ebnf). If
you know XPath, I think you\'ll find this fairly easy to learn.

Another benefit of an SCD is that tools will have easier time generating
SCDs than XPath, as XPaths are often vulnerable to small changes in the
schema document, while SCDs are much more robust. The downside of using
SCD is as of JAXB 2.1, this feature is a vendor extension and not
defined in the spec.
