Mapping interfaces {#annotating-your-classes-mapping-interfaces}
==================

Because of the difference between the XML type system induced by W3C XML
Schema and the Java type system, JAXB cannot bind interfaces out of the
box, but there are a few things you can do.

Use `@XmlRootElement` {#Use__XmlRootElement}
---------------------

When your interface is implemented by a large number of sub-classes,
consider using `XmlRootElement` annotation like this:

``` {.java}
@XmlRootElement
class Zoo {
  @XmlAnyElement
  public List<Animal> animals;
}

interface Animal {
  void sleep();
  void eat();
  ...
}

@XmlRootElement
class Dog implements Animal { ... }

@XmlRootElement
class Lion implements Animal { ... }
```

This will produce XML documents like this:

``` {.xml}
<zoo>
    <lion> ... </lion>
    <dog> ... </dog>
</zoo>
```

The key characteristics of this approach is:

1.  Implementations are open-ended; anyone can implement those
    interfaces, even by different people from different modules,
    provided they are all given to the `JAXBContext.newInstance` method.
    There\'s no need to list all the implementation classes in anywhere.

2.  Each implementation of the interface needs to have an unique element
    name.

3.  Every reference to interface needs to have the `XmlElementRef`
    annotation. The `type=Object.class` portion tells JAXB that the
    greatest common base type of all implementations would be
    `java.lang.Object`.

`@XmlElementWrapper` is often useful with this, as it allows you need to
group them. Such as:

``` {.java}
@XmlRootElement
class Zoo {
  @XmlElementWrapper
  @XmlAnyElement
  public List<Animal> onExhibit;
  @XmlElementWrapper
  @XmlAnyElement
  public List<Animal> resting;
}
```

``` {.xml}
<zoo>
    <onExhibit>
        <lion> ... </lion>
        <dog> ... </dog>
    </onExhibit>
    <resting>
        <lion> ... </lion>
        <dog> ... </dog>
    </resting>
</zoo>
```

Use `@XmlJavaTypeAdapter` {#Use__XmlJavaTypeAdapter}
-------------------------

When you use interfaces just to hide your implementation classes from
exposure, and when there\'s 1-to-1 (or close to 1-on-1) relationship
between a class and an interface, `XmlJavaTypeAdapter` can be used like
below.

``` {.java}
@XmlJavaTypeAdapter(FooImpl.Adapter.class)
interface IFoo {
  ...
}
class FooImpl implements IFoo {
  @XmlAttribute
  private String name;
  @XmlElement
  private int x;

  ...

  static class Adapter extends XmlAdapter<FooImpl,IFoo> {
    IFoo unmarshal(FooImpl v) { return v; }
    FooImpl marshal(IFoo v) { return (FooImpl)v; }
  }
}

class Somewhere {
  public IFoo lhs;
  public IFoo rhs;
}
```

``` {.xml}
<somewhere>
  <lhs name="...">
    <x>5</x>
  </lhs>
  <rhs name="...">
    <x>5</x>
  </rhs>
</somewhere>
```

The key characteristics of this approach is:

1.  Interface and implementation will be tightly coupled through an
    adapter, although changing an adapter code will allow you to support
    multiple implementations.

2.  There\'s no need of any annotation in where interfaces are used.

A variation of this technique is when you have a few implementations for
interface, not just one.

``` {.java}
@XmlJavaTypeAdapter(AbstractFooImpl.Adapter.class)
interface IFoo {
  ...
}
abstract class AbstractFooImpl implements IFoo {
  ...

  static class Adapter extends XmlAdapter<AbstractFooImpl,IFoo> {
    IFoo unmarshal(AbstractFooImpl v) { return v; }
    AbstractFooImpl marshal(IFoo v) { return (AbstractFooImpl)v; }
  }
}

class SomeFooImpl extends AbstractFooImpl {
  @XmlAttribute String name;
  ...
}

class AnotherFooImpl extends AbstractFooImpl {
  @XmlAttribute int id;
  ...
}

class Somewhere {
  public IFoo lhs;
  public IFoo rhs;
}
```

``` {.xml}
<somewhere>
  <lhs xsi:type="someFooImpl" name="...">
  </lhs>
  <rhs xsi:type="anotherFooImpl" id="3" />
</somewhere>
```

Note that `SomeFooImpl` and `AnotherFooImpl` must be submitted to
`JAXBContext.newInstance` one way or the other.

To take this example a bit further, you can use `Object` instead of
`AbstractFooImpl`. The following example illustarates this:

``` {.java}
@XmlJavaTypeAdapter(AnyTypeAdapter.class)
interface IFoo {
  ...
}
public class AnyTypeAdapter extends XmlAdapter<Object,Object> {
  Object unmarshal(Object v) { return v; }
  Object marshal(Object v) { return v; }
}

class SomeFooImpl implements IFoo {
  @XmlAttribute String name;
  ...
}

class Somewhere {
  public IFoo lhs;
  public IFoo rhs;
}
```

``` {.xml}
<xs:complexType name="somewhere">
  <xs:sequence>
    <xs:element name="lhs" type="xs:anyType" minOccurs="0"/>
    <xs:element name="rhs" type="xs:anyType" minOccurs="0"/>
  </xs:sequence>
</xs:complexType>
```

As you can see, the schema will generated to accept `xs:anyType` which
is more relaxed than what the Java code actually demands. The instance
will be the same as the above example. Starting from JAXB RI 2.1, we
bundle the `com.sun.xml.bind.AnyTypeAdapter` class in the runtime that
defines this adapter. So you won\'t have to write this adapter in your
code.

Use `@XmlElement` {#Use__XmlElement}
-----------------

If the use of interface is very little and there\'s 1-to-1 (or close to)
relationship between interfaces and implementations, then you might find
`XmlElement` to be the least amount of work.

``` {.java}
interface IFoo {
  ...
}
class FooImpl implements IFoo {
  ...
}

class Somewhere {
  @XmlElement(type=FooImpl.class)
  public IFoo lhs;
}
```

``` {.xml}
<somewhere>
  <lhs> ... </lhs>
</somewhere>
```

This effectively tells JAXB runtime that \"even though the field is
`IFoo`, it\'s really just `FooImpl`.

In this approach, a reference to an interface has to have knowledge of
the actual implementation class. So while this requires the least amount
of typing, it probably wouldn\'t work very well if this crosses module
boundaries.

Like the `XmlJavaTypeAdapter` approach, this can be used even when there
are multiple implementations, provided that they share the common
ancestor.

The extreme of this case is to specify `@XmlElement(type=Object.class)`.

Hand-write schema {#Hand_write_schema}
-----------------

Occasionally the above approaches cause the generated schema to become
somewhat ugly, even though it does make the JAXB runtime work correctly.
In such case you can choose not to use the generated schema and instead
manually modify/author schemas tht better match your needs.

Do schema-to-java {#Do_schema_to_java}
-----------------

With sufficient knowlege, one can also use `<jaxb:class
        ref="..."/>` annotation so that you can cause XJC to use the
classes you already wrote. See this thread for an example. TODO: more
details and perhaps an example.

DOESN\'T WORK: Have JAXB generate interaces and swap different implementations {#DOESN_T_WORK__Have_JAXB_generate_interaces_and_swap_different_implementations}
------------------------------------------------------------------------------

Some users attempted to use the \"`generateValueClass`\" customization
and see if they can completely replace the generated implementations
with other implementations. Unfortunately, this does not work.

Even with the interface/implementation mode, JAXB runtime still requires
that the implementation classes have all the JAXB annotations. So just
implementing interfaces is not sufficient. (This mode is mainly added to
simplify the migration from JAXB 1.0 to JAXB 2.0, and that\'s a part of
the reason why things are done this way.)
