Customizing Java packages {#customization-of-schema-compilation-customizing-java-packages}
=========================

The JAXB specification provides a `<jaxb:schemaBindings>` customization
so that you can control which namespace goes to which package. See the
example below:

``` {.xml}
    <jaxb:schemaBindings>
      <jaxb:package name="org.acme.foo"/>
    </jaxb:schemaBindings>
```

You can do this as an internal customization (in which case you put this
in `<xs:annotation>``<xs:appinfo>` under place it right under the
`<xs:schema>` element), or do this as an external customization, like
this:

``` {.xml}
<bindings xmlns="http://java.sun.com/xml/ns/jaxb" version="2.1">
  <bindings schemaLocation="../path/to/my.xsd">
    <schemaBindings>
      <package name="org.acme.foo"/>
    </schemaBindings>
  </bindings>
</bindings>
```

Note that this customization is per namespace. That is, even if your
schema is split into multiple schema documents, you cannot put them into
different packages if they are all in the same namespace.

Tip: get rid of the org.w3.\_2001.xmlschema package {#Tip__get_rid_of_the_org_w3__2001_xmlschema_package}
---------------------------------------------------

Under some rare circumstances, XJC will generate some Java classes into
a package called `org.w3._2001.xmlschema`. This happens when XJC decides
that it needs some Java artifacts for the XML Schema built-in namespace
of `http://www.w3.org/2001/XMLSchema`.

Since this package name is most often problematic, you can rename this
by simply saving the following text in an .xsd file and submitting it to
XJC along with the other schemas you have:

``` {.xml}
<schema xmlns="http://www.w3.org/2001/XMLSchema"
  targetNamespace="http://www.w3.org/2001/XMLSchema"
  xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
  jaxb:version="2.0">
  <annotation><appinfo>
    <jaxb:schemaBindings>
      <jaxb:package name="org.acme.foo"/>
    </jaxb:schemaBindings>
  </appinfo></annotation>
</schema>
```

This is bit tricky, but the idea is that since you can define a schema
for one namespace in multiple schema documents, this makes XJC think
that this schema is a part of the built-in \"XML Schema for XML
Schema\".
