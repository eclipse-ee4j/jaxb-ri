XML layout and in-memory data layout {#annotating-your-classes-xml-layout-and-in-memory-data-layout}
====================================

Your program sometimes needs to have a different in-memory data
structure from its XML representation. JAXB has a few different ways to
achieve this.

XmlJavaTypeAdapter {#XmlJavaTypeAdapter}
------------------

`XmlJavaTypeAdapter` allows you to de-couple the in-memory
representation and the XML representation by introducing an intermediate
representation. The basic model is as follows:

::: {.informalexample}
    In-memory objects  <===>  Intermediate objects   <===>
    XML
                      adapter                         JAXB
:::

Your adapter code will be responsible for converting in-memory objects
to/from intermediate objects. Intermediate objects are then bound to XML
by following the standard JAXB rules. See `XmlAdapter` for a general
description of how adapters works.

Adapters extend from the `XmlAdapter` class and provide two methods
\"unmarshal\" and \"marshal\" that converts values in both directions,
and then the `XmlJavaTypeAdapter` annotation is used to tell JAXB where
and what adapters kick in.

(TODO: more info about XmlJavaTypeAdapter needed)

1.  adapting a class

2.  adapting a property

3.  adapting an external class

4.  adapting a collection and its effect

5.  adapting and using interfaces

Using XmlJavaTypeAdapter for element/attribute values {#Using_XmlJavaTypeAdapter_for_element_attribute_values}
-----------------------------------------------------

One of the common use cases of `XmlJavaTypeAdapter` is to map a \"value
object\" to a string in XML. The following example illustrates how to do
this, by using `java.awt.Color` as an example.

``` {.java}
@XmlRootElement
class Box {
  @XmlJavaTypeAdapter(ColorAdapter.class)
  @XmlElement
  Color fill;
}

class ColorAdapter extends XmlAdapter<String,Color> {
  public Color unmarshal(String s) {
    return Color.decode(s);
  }
  public String marshal(Color c) {
    return "#"+Integer.toHexString(c.getRGB());
  }
}
```

This maps to the following XML representation:

``` {.xml}
<box>
  <fill>#112233</fill>
</box>
```

Since `XmlJavaTypeAdapter` is on a field, this adapter only kicks in for
this particular field. If you have many `Color` fields and would like
them all to use the same adapter, you can move the annotation to a
package:

``` {.java}
@XmlJavaTypeAdapter(type=Color.class,value=ColorAdapter.class)
package foo;
```

``` {.java}
@XmlRootElement
class Box {
  @XmlElement Color fill;
  @XmlElement Color border;
}
```

This causes all the fields in the classes in the `foo` package to use
the same specified adapter.

Also see the `DatatypeConverter` class that defines a series of basic
conversion routines that you may find useful.

Pair property {#Pair_property}
-------------

Another useful technique is to define two properties, one for JAXB and
the other for your application. See the following example:

``` {.java}
@XmlRootElement
class Person {
  private int age;

  // This public property is for users
  @XmlTransient
  public int getAge() {
    return age;
  }
  public void setAge(int age) {
    this.age = age;
  }

  // This property is for JAXB
  @XmlAttribute(name="age")
  private String getAge_() {
    if(age==-1)  return "dead";
    else         return String.valueOf(age);
  }
  private void setAge_(String v) throws NumberFormatException {
    if(v.equals("dead"))   this.age=-1;
    else                   this.age=Integer.parseInt(age);
}
```

The main \"`age`\" property is public, but marked as `XmlTransient`, so
it\'s exposed in your program, but JAXB will not map this to XML.
There\'s another private \"`age_`\" property. Since this is marked with
`XmlAttribute`, this is what JAXB is going to use to map to the
attribute. The getter and setter methods on this property will handle
the conversion between the in-memory representation and the XML
representation.
