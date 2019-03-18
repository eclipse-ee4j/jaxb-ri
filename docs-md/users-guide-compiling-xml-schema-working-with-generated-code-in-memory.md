Working with generated code in memory {#compiling-xml-schema-working-with-generated-code-in-memory}
=====================================

Cloning {#Cloning}
-------

The generated beans (and in particular the `JAXBElement` class) do not
support the clone operation. There was a suggestion by another user that
[beanlib](http://beanlib.sourceforge.net/) has been used successfully to
clone JAXB objects.
