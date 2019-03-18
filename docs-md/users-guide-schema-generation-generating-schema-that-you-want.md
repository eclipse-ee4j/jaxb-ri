Generating Schema that you want {#schema-generation-generating-schema-that-you-want}
===============================

This section discusses how you can change the generated XML schema. For
changes that also affect the infoset (such as changing elements to
attributes, namespaces, etc.), refer to a different section \"XML layout
and in-memory data layout\".

Adding facets to datatypes {#Adding_facets_to_datatypes}
--------------------------

As of JAXB 2.1.4, currently no support for this, although there has been
several discussions in the users alias.

The JAXB project is currently lacking resources to attack this problem,
and therefore looking for volunteers to work on this project. The basic
idea would be to define enough annotations to cover the basic constraint
facets (such as length, enumerations, pattern, etc.) The schema
generator would have to be then extended to honor those annotations and
generate schemas accordingly.

Some users pointed out relevance of this to [JSR 303: Bean
Validation](http://jcp.org/en/jsr/detail?id=303). If you are interested
in picking up this task, let us know!
