Dealing with errors {#compiling-xml-schema-dealing-with-errors}
===================

Schema errors {#Schema_errors}
-------------

Because XML Schema is so complicated, and because there are a lot of
tools out there do not implement the spec correctly, it is often the
case that a schema you are trying to compile has some real errors in it.
When this is the case, you\'ll see XJC reporting somewhat cryptic errors
such as `rcase-RecurseLax.2: There is not a
        complete functional mapping between the particles.`

The JAXB RI uses the schema correctness checker from the underlying JAXP
implementation, which is the JAXP RI in a typical setup. The JAXP RI is
one of the most conformant schema validators, and therefore most likely
correct. So the first course of action usually is to fix problems in the
schema.

However, in some situations, you might not have an authority to make
changes to the schema. If that is the case and you really need to
compile the schema, you can bypass the correctness check by using the
`-nv` option in XJC. When you do this, keep in mind that you are
possibly feeding \"garbage\" in, so you may see XJC choke with some
random exception.

Property \'fooBarZot\' is already defined {#Property__fooBarZot__is_already_defined}
-----------------------------------------

One of the typical errors you\'ll see when compiling a complex schema
is:

``` {.output}
parsing a schema...
[ERROR] Property "MiOrMoOrMn" is already defined.
  line 132 of
file:/C:/kohsuke/Sun/JAXB/jaxb-unit/schemas/individual/MathML2/presentation/scripts.xsd

[ERROR] The following location is relevant to the above error
  line 138 of
file:/C:/kohsuke/Sun/JAXB/jaxb-unit/schemas/individual/MathML2/presentation/scripts.xsd
```

This is an actual example of the offending part of a schema, taken from
MathML. If you go to line 132 of `scripts.xsd`, you\'ll see that it has
a somewhat complicated content model definition:

``` {.xml}
<xs:group name="mmultiscripts.content">
    <xs:sequence>
        <xs:group ref="Presentation-expr.class"/>
        <xs:sequence minOccurs="0" maxOccurs="unbounded">      <!-- line 132 -->
            <xs:group ref="Presentation-expr-or-none.class"/>
            <xs:group ref="Presentation-expr-or-none.class"/>
        </xs:sequence>
        <xs:sequence minOccurs="0">
            <xs:element ref="mprescripts"/>
            <xs:sequence maxOccurs="unbounded">                 <!-- line 138 -->
                <xs:group ref="Presentation-expr-or-none.class"/>
                <xs:group ref="Presentation-expr-or-none.class"/>
            </xs:sequence>
        </xs:sequence>
    </xs:sequence>
</xs:group>
```

This is a standard technique in designing a schema. When you want to say
\"in this element, `B` can occur arbitrary times, but `C` can occur only
up to once\", you write this as `B*,(C,B*)?`. This, however, confuses
JAXB, because it tries to bind the first `B` to its own property, then
`C` to its own property, then the second `B` to its own property, and so
we end up having a collision again.

In this particular case, `B` isn\'t a single element but it\'s a choice
of large number of elements abstracted away in `<xs:group>`s, so they
are hard to see. But if you see the same content model referring to the
same element/group twice in a different place, you can suspect this.

In this case, you\'d probably want the whole thing to map to a single
list so that you can retain the order those elements show up in the
document. You can do this by putting the same `<jaxb:property>`
customization on the whole \"`mmultiscripts.content`\" model group, like
this (or you can do it externally with XPath):

``` {.xml}
<xs:groupname="mmultiscripts.content">
<xs:annotation>
    <xs:appinfo>
        <jaxb:propertyname="content"/>
    </xs:appinfo>
</xs:annotation>
<xs:sequence>
<xs:groupref="Presentation-expr.class"/>
```

Another way to fix this problem is to use the simpler and better binding
mode in XJC, which is a JAXB RI vendor extension.

Two declarations cause a collision in the ObjectFactory class {#Two_declarations_cause_a_collision_in_the_ObjectFactory_class}
-------------------------------------------------------------

When schemas contain similar looking element/type names, they can result
in \"Two declarations cause a collision in the ObjectFactory class\"
errors. To be more precise, for each of all types and many elements
(exactly what elements get a factory and what doesn\'t is bit tricky to
explain), XJC produces one method on the `ObjectFactory` class in the
same package. The `ObjectFactory` class is created for each package that
XJC generates some files into. The name of the method is derived from
XML element/type names, and the error is reported if two elements/types
try to generate the same method name.

There are two approaches to fix this problem. If the collision is coming
from two different schemas with different target namespaces, then you
can easily avoid the collision by compiling them into different Java
packages. To do this, use `<schemabindings>` customization on two
schemas and specify the package name.

Another way to fix this problem is to use `<factoryMethod>`
customization on two conflicting elements/types to specify different
factory method names. This can be used in all cases, but if you have a
large number of conflicts, you\'ll have to specify this customization
one by one.

Notice that `<class>` customization doesn\'t affect the `ObjectFactory`
method name by itself.

Customization errors {#Customization_errors}
--------------------

### XPath evaluation of \... results in empty target node {#XPath_evaluation_of_____results_in_empty_target_node}

External JAXB customizations are specified by using XPath (or using
[SCD](customization-of-schema-compilation-using-scd-for-customizations).)
This works by writing an XPath expression that matches a particular
element in the schema document. For example, given the following schema
and binding file:

**`test.xsd`.**

``` {.xml}
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xs:complexTypename="foo"/>
</xs:schema>
```

**`test.xjb`.**

``` {.xml}
<bindings version="2.0" xmlns="http://java.sun.com/xml/ns/jaxb" xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <bindings schemaLocation="test.xsd">
        <bindings node="//xs:complexType[@name='foo']">
            <classname="Bar"/>
        </bindings>
    </bindings>
</bindings>
```

will be interpreted as if the class customization is attached to the
complex type \'`foo`\'.

For this to work, the XPath expression needs to match one and only one
element in the schema document. When the XPath expression is incorrect
and it didn\'t match anything, you get this \"XPath evaluation of \...
results in empty target node\" problem.

Common causes of this problem include typos, incorrect namespace URI
declarations, and misunderstanding of XPath.
