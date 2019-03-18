Adding behaviors {#compiling-xml-schema-adding-behaviors}
================

*Adding behaviors to the generated code is one area that still needs
improvement. Your feedback is appreciated.*

Suppose if JAXB generated the following classes.

``` {.java}
package org.acme.foo;

@XmlRootElement
class Person {
  private String name;

  public String getName() { return name; }
  public void setName(String) { this.name=name; }
}

@XmlRegistry
class ObjectFactory {
  Person createPerson() { ... }
}
```

To add a behavior, first write a class that extends from `Person`. You
also need to extend ObjectFactory to return this new class. Notice that
neither classes have any JAXB annotation, and I put them in a separate
package. This is because we\'d like `PersonEx` class to be used in place
of `Person`, and we don\'t want `PersonEx` to be bound to its own XML
type.

``` {.java}
package org.acme.foo.impl;

class PersonEx extends Person {
  @Override
  public void setName(String name) {
    if(name.length()<3) throw new IllegalArgumentException();
    super.setName(name);
  }
}

@XmlRegistry
class ObjectFactoryEx extends ObjectFactory {
  @Override
  Person createPerson() {
    return new PersonEx();
  }
}
```

At runtime, you can create `JAXBContext` normally, like this.

``` {.java}
JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
// or JAXBContext.newInstance("org.acme.foo");
```

`PersonEx` can be marshalled out just like `Person`:

``` {.java}
Person p = new PersonEx();
context.createMarshaller().marshal(p,System.out);
// this will produce <person />
```

To unmarshal XML documents into `PersonEx`, you\'ll need to configure
the unmarshaller to use your `ObjectFactoryEx` as the factory, like
this:

``` {.java}
Unmarshaller u = context.createUnmarshaller();
u.setProperty("com.sun.xml.bind.ObjectFactory",new ObjectFactoryEx());
PersonEx p = (PersonEx)u.unmarshal(new StringReader("<person />"));
```

If you have multiple packages and thus multiple `ObjectFactory`s, you
can pass in an array of them (`new
    Object[]{new OFEx1(),new OFEx2(),...}`.)

Inserting your class in the middle {#Inserting_your_class_in_the_middle}
----------------------------------

If you have a type hierarchy and would like to insert your class in the
middle, you can use the combination of `XmlTransient` and `@implClass`
of `<class>` customization. See the following example:

``` {.xml}
<xs:schema ...>
  <xs:complexType name="vehicle">
    <xs:annotation><xs:appinfo>
      <jaxb:class implClass="MyVehicle" />
    </xs:appinfo></xs:annotation>
  </xs:complexType>

  <xs:complexType name="car">
    <xs:complexContent>
      <xs:extension base="vehicle" />
    </xs:complexContent>
  </xs:complexType>

  <xs:complexType name="bicycle">
    <xs:complexContent>
      <xs:extension base="vehicle" />
    </xs:complexContent>
  </xs:complexType>
</xs:schema>
```

                Vehicle
                   ^
                   |
                MyVehicle
                   ^
              _____|______
             |            |
            Car          Bicycle

You\'ll then manually write `MyVehicle` class that extends from
`Vehicle`. Annotate this class with `XmlTransient` to achieve the
desired effect.
