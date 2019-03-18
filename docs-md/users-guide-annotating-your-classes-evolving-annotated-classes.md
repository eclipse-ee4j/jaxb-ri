Evolving annotated classes {#annotating-your-classes-evolving-annotated-classes}
==========================

Here is the basic problem of evolution. You got your CoolApp v1, which
contains class Foo that has some JAXB annotations. Now you are working
towawrd CoolApp v2, and you want to make some changes to Foo. But you
want to do so in such a way that v1 and v2 can still talk to each other.

The evolution compatibility has two different aspects. One is the
*schema compatibility*, which is about the relationship between the v1
schema and the v2 schema. The other is about *runtime compatibility*,
which is about reading/writing documents between two versions.

Runtime compatibility {#Runtime_compatibility}
---------------------

There are two directions in the runtime compatibility. One is whether v1
can still read what v2 write (*forward compatible*), and the other is
whether v2 can read what v1 wrote (*backward compatible*).

\"`Semi-compatible`\" {#_Semi_compatible_}
---------------------

JAXB can read XML documents that don\'t exactly match what\'s expected.
This is the default behavior of the JAXB unmarshaller, yet you can
change it to a more draconian behavior (TODO: pointer to the
unmarshalling section.)

When we are talking about evolving classes, it\'s often convenient to
leave it in the default behavior, as that would allow JAXB to nicely
ignore elements/attributes newly added in v2. So we call it *backward
semi-compatible* if v2 can read what v1 wrote in this default
unmarshalling mode, and similarly *forward semi-compatible* if v1 can
read what v2 wrote in this default unmarshalling mode.

Technically, these are weaker than true backward/forward compatibility
(since you can\'t do a draconian error detection), yet in practice it
works just fine.

Adding/removing/changing non-annotated things {#Adding_removing_changing_non_annotated_things}
---------------------------------------------

You can add, remove, or change any non-annotated fields, methods,
inner/nested types, constructors, interfaces. Those changes are both
backward and forward compatible, as they don\'t cause any change to the
XML representation.

Adding super class is backward compatible and forward semi-compatible.
Similarly, removing super class is forward compatible and backward
semi-compatible.

Adding/removing/changing properties {#Adding_removing_changing_properties}
-----------------------------------

Adding new annotated fields or methods is backward compatible and
forward semi-compatible. Similarly, removing them is forward compatible
and backward semi-compatible.

Changing a property is bit more tricky.

1.  If you change the property name from X to Y, that would be the
    equivalent of deleting X and adding Y, so it would be backward and
    forward semi-compatible. What JAXB really cares is properties\' XML
    names and not Java names, so by using the `name` parameter of
    `XmlElement`, `XmlAttribute` et al, you can change Java property
    names without affecting XML, or change XML without affecting Java
    properties. These are backward and forward semi-compatible. See
    below:

2.  ``` {.java}
    // BEFORE
    public class Foo {
        public String abc;
    }
    // AFTER: Java name changed, but XML remains the same
    public class Foo {
        @XmlElement(name="abc")
        public String def;
    }
    ```

    ``` {.java}
    // BEFORE
    public class Foo {
        public String abc;
    }
    // AFTER: no Java change, but XML will look different
    public class Foo {
        @XmlElement(name="def")
        public String abc;
    }
    ```

3.  If you change a property type, generally speaking it will be not
    compatible at all. For example, you can\'t change from
    `java.util.Calendar` to `int` and expect it to work. To make it a
    somewhat compatible change, the old type and the new type has to be
    related. For example, `String` can represent all `int` values, so
    changing `int` to `String` would be a backward compatible and
    forward semi-compatible change. `XmlJavaTypeAdapter` allows you to
    make changes to Java without affecting XML (or vice versa.)

Changing class names {#Changing_class_names}
--------------------

`XmlType` and `XmlRootElement` allows you to change a class name without
affecting XML.

``` {.java}
// BEFORE
@XmlRootElement
public class Foo { ... }

// AFTER: no XML change
@XmlRootElement(name="foo")
@XmlType(name="foo")
public class Bar { ... }
```

``` {.java}
// BEFORE
public class Foo { ... }

// AFTER: no XML change
@XmlType(name="foo")
public class Bar { ... }
```

Schema Compatibility {#Schema_Compatibility}
--------------------

TODO.
