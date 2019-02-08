Changing prefixes {#marshalling-changing-prefixes}
=================

By default, a JAXB marshaller uses random namespace prefixes (such as
`ns1`, `ns2`, \...) when it needs to declare new namespace URIs. While
this is perfectly valid XML wrt the schema, for human readability, you
might want to change them to something that makes more sense.

The JAXB RI defines NamespacePrefixMapper to allow you to do this. See
the `namespace-prefix` sample in the distribution for more details.
