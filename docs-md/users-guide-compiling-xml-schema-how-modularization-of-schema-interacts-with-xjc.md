How modularization of schema interacts with XJC {#compiling-xml-schema-how-modularization-of-schema-interacts-with-xjc}
===============================================

Over time schema authors have developed several techniques to modularize
large schemas. Some of those techniques have some noteworthy
interactions with XJC.

Chameleon schema {#Chameleon_schema}
----------------

[Chameleon
schema\"](http://www.xfront.com/ZeroOneOrManyNamespaces.html#mixed)
([read more](http://www.google.com/search?q=chameleon+schema), in
particular
[this](http://www.kohsuke.org/xmlschema/XMLSchemaDOsAndDONTs.html#avoid_chameleon))
is a technique used to define multiple almost-identical sets of
definitions into multiple namespaces from a single schema document.

For example, with this technique, you can write just one \"foo\" complex
type and define it into namespace X and Y. In this case, one tends to
hope that XJC will only give you one `Foo` class for this, but
unfortunately because it\'s actually defined in two namespaces, JAXB
needs two Java classes to distinguish `X:foo` and `Y:foo`, so you\'ll
get multiple copies.

If you find this to be problematic, there are a few ways to work around
the problem.

1.  If you are in control of the schema, see if you can rewrite the
    schema to avoid using this technique. In some cases, the schema
    doesn\'t actually exploit the additional power of this technique, so
    this translation can be done without affecting XML instance
    documents. In some other cases, the chameleon schema can be argued
    as a bad schema design, as it duplicates definitions in many places.

2.  If you are not in control of the schema, see if you can rewrite the
    schema nevertheless. This will only work if your transformation
    doesn\'t affect XML instance documents.

3.  Perhaps there can be a plugin that eases the pain of this, such as
    by defining common interfaces among copies.
