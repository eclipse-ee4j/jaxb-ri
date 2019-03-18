Mapping cyclic references to XML {#annotating-your-classes-mapping-cyclic-references-to-xml}
================================

Object models designed in Java often have cycles, which prevent
straight-forward conversion to XML by JAXB. In fact, when you try to
marshal an object tree that contains a cycle, the JAXB marshaller
reports an error, pointing out the objects that formed the cycle. This
is because JAXB by itself cannot figure out how to cut cycles into a
tree.

Thus it is your responsibility to annotate classes and use other means
to \"tell\" JAXB how to handle a cycle. This chapter talks about various
techniques to do this.

Parent pointers {#Parent_pointers}
---------------

One of the very common forms of cycle is a parent pointer. The following
example illustrates a typical parent pointer, and how this can be turned
into \"natural\" XML:

``` {.java}
@XmlRootElement
class Department {
  @XmlAttribute
  String name;
  @XmlElement(name="employee")
  List<Employee> employees;
}

class Employee {
  @XmlTransient
  Department department;  // parent pointer
  @XmlAttribute
  String name;

  public void afterUnmarshal(Unmarshaller u, Object parent) {
    this.department = (Department)parent;
  }
}
```

This will produce the following XML:

``` {.xml}
<department name="accounting">
  <employee name="Joe Chin" />
  <employee name="Adam Smith" />
  ...
</department>
```

And reading this document back into Java objects will produce the
expected tree with all the proper parent pointers set up correctly.

The first technique here is the use of `XmlTransient` on the parent
pointer. This tells JAXB that you don\'t need this parent pointer to be
represented explicitly in XML, because the fact that `employee` is
always contained inside `department` implies the parent/child
relationship. This causes the marshaller to produce the expected XML.
However, when you unmarshal it, since this field is not bound, the
`Employee.department` field will be left null.

That\'s where the second technique comes in, which is the use of the
`afterUnmarshal` callback. This method is invoked by the JAXB
implementation on each instance when the unmarshalling of a `Employee`
object completes. Furthermore, the second parameter to the method is the
parent object, which in this case is a `Department` object. So in this
example, this sets up the parent pointer correctly.

This callback can be also used to perform other post-unmarshalling set
up work.

Many-to-many relationship {#Many_to_many_relationship}
-------------------------

TBD

`@XmlID` and `@XmlIDREF` {#_XmlID_and__XmlIDREF}
------------------------

When a reference to another object is annotated with `XmlIDREF`, its
corresponding XML it will be referenced by `xs:IDREF`, instead of
containment. See below for an example:

Example of `@XmlID` and `@XmlIDREF`

::: {.informalexample}
``` {.java}
@XmlRootElement
class Root {
  List<Foo> foos;
  List<Bar> bars;
}
class Foo {
  // you don't have to make it an attribute, but that's more common
  @XmlAttribute @XmlIDREF Bar bar;
}
class Bar {
  // you don't have to make it an attribute, but that's more common
  @XmlAttribute @XmlID String id;
}
```
:::

``` {.xml}
<xs:complexType name="foo">
  <xs:sequence/>
  <xs:attribute name="bar" type="xs:IDREF"/>
  </xs:sequence>
</xs:complexType>
<xs:complexType name="bar">
  <xs:sequence/>
  <xs:attribute name="id" type="xs:ID"/>
</xs:complexType>
```

``` {.xml}
<root>
  <foo bar="x"/>
  <foo bar="y"/>
  <bar id="x"/>
  <bar id="y"/>
</root>
```

There are a few things to consider when you do this. First, the object
to be referenced must have an ID that is unique within the whole
document. You\'d also need to ensure that the referenced objects are
*contained* somewhere else (like in the `Root` class in this case), or
else `Bar` objects will never be marshalled. This technique can be used
to remove the cyclic references, but it\'s only possible when your
object model has an easy cut point.

Use the CycleRecoverable interface {#Use_the_CycleRecoverable_interface}
----------------------------------

Starting 2.1 EA2, the JAXB RI exposes `CycleRecoverable` interface.
Applications can choose to implement this interface in some of its
objects. When a cyclic reference is detected during marshalling, and if
the object that formed a cycle implements this interface, then the
method on this interface is called to allow an application to nominate
its replacement to be written to XML. In this way, the application can
recover from a cycle gracefully.

This technique allows you to cope with a situation where you cannot
easily determine upfront as to where a cycle might happen. On the other
hand, this feature is a JAXB RI feature. Another downside of this is
that unless you nominate your replacement carefully, you can make the
marshalling output invalid with respect to the schema, and thus you
might hit another problem when you try to read it back later.
