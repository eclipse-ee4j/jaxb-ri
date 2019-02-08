Mapping your favorite class {#annotating-your-classes-mapping-your-favorite-class}
===========================

ResultSet {#ResultSet}
---------

JAXB (or any other databinding engine, for that matter) is for binding
strongly-typed POJO-like objects to XML, such as `AddressBook` class,
`PurchaseOrder` class, and so on, where you have fields and methods that
shape a class.

There are other kinds of classes that are more close to reflection.
Those classes don\'t have methods like `getAddress`, and instead you\'d
do `get("Address")`. JDBC ResultSet is one of those classes. It\'s one
class that represents million different data structures, be it a
customer table or a product table. Generally speaking, these classes
does not allow JAXB to statically determine what the XML representation
should look like. Instead, you almost always need to look at an instance
to determine the shape of XML.

These classes are not really suitable for binding in JAXB. If this is
the only object that you\'d want to write out, then you\'d be better off
using XMLStreamWriter or some such XML infoset writing API. There are [a
few online articles](http://www.google.com/search?q=ResultSet+XML) that
cover this topic. Also, many modern database offers a native ability to
export a query into XML, which is supposed to work a lot faster than
you\'d do in Java (and saves your time of writing code.)

If you are using ResultSet as a part of your object tree that you want
to marshal to JAXB, then you can use `XmlJavaTypeAdapter`.

HashMap {#HashMap}
-------

JAXB spec defines a special handling for `Map` when it\'s used as a
propety of a bean. For example, the following bean would produce XMLs
like the following:

``` {.java}
@XmlRootElement
class Foo {
  public HashMap<String,Integer> map;
}
```

``` {.xml}
<foo>
  <map>
    <entry>
      <key>a</key>
      <value>1</value>
    </entry>
    <entry>
      <key>b</key>
      <value>2</value>
    </entry>
  </map>
</foo>
```

Unfortunately, as of 2.1, this processing is only defined for bean
properties and not when you marshal `HashMap` as a top-level object
(such as a value in `JAXBElement`.) In such case, `HashMap` will be
treated as a Java bean, and when you look at `HashMap` as a bean it
defines no getter/setter property pair, so the following code would
produce the following XML:

``` {.java}
m = new HashMap();
m.put("abc",1);
marshaller.marshal(new JAXBElement(new QName("root"),HashMap.class,m),System.out);
```

``` {.xml}
<root />
```

This issue has been recorded as
[\#223](https://github.com/eclipse-ee4j/jaxb-ri/issues/223) and the fix
needs to happen in later versions of the JAXB spec.

In the mean time, such top-level objects have to be first adapted to a
bean that JAXB can process. This has added benefit of being able to
control XML representation better. The following code illustrates how to
do this:

``` {.java}
public class MyHashMapType {
    public List<MyHashMapEntryType> entry = new ArrayList<MyHashMapEntryType>();
    public MyHashMapType(Map<String,Integer> map) {
        for( Map.Entry<String,Integer> e : map.entrySet() )
            entry.add(new MyHashMapEntryType(e));
    }
    public MyHashMapType() {}
}

public class MyHashMapEntryType {
    @XmlAttribute // @XmlElement and @XmlValue are also fine
    public String key;

    @XmlAttribute // @XmlElement and @XmlValue are also fine
    public int value;

    public MyHashMapEntryType() {}
    public MyHashMapEntryType(Map.Entry<String,Integer> e) {
       key = e.getKey();
       value = e.getValue();
    }
}

marshaller.marshal(new JAXBElement(new QName("root"),MyHashMapType.class,new MyHashMapType(m)),System.out);
```

If you have a lot of difference kinds of `Map`, you can instead use
`Object` as the key and the value type. In that way, you\'ll be able to
use maps with different type parameters, at the expense of seeing
`xsi:type` attribute on the instance document.
