Using different datatypes {#customization-of-schema-compilation-using-different-datatypes}
=========================

JAXB has a built-in table that determines what Java classes are used to
represent what XML Schema built-in types, but this can be customized.

One of the common use cases for customization is to replace the
`XMLGregorianCalendar` with the friendlier `Calendar` or `Date`.
`XMLGregorianCalendar` is designed to be 100% compatible with XML
Schema\'s date/time system, such as providing infinite precision in
sub-seconds and years, but often the ease of use of those familiar Java
classes win over the precise compatibility.

One very easy way to do this is to simply use your IDE (or even
\"`sed`\") to replace all the references to `XMLGregorianCalendar` by
`Calendar`. This is of course not a very attractive option if your build
process runs XJC as a part of it.

Alternatively, the following customization file can be used to do this.
When using external customization file, the JAXB spec requires you to
use XPath as a means to specify what your customization is attached to.
For example, if you want to change the class name generated from a
complex type, you\'d use the following customization:

``` {.xml}
<bindings xmlns="http://java.sun.com/xml/ns/jaxb" version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <globalBindings>
    <javaType name="java.util.Calendar" xmlType="xs:date"
      parseMethod="javax.xml.bind.DatatypeConverter.parseDate"
      printMethod="javax.xml.bind.DatatypeConverter.printDate"
    />
  </globalBindings>
</bindings>
```

Save this in a file and specify this to JAXB with the \"-b\" option.

To use the `Date` class, you\'ll need to do a bit more work. First, put
the following class into your source tree:

``` {.java}
package org.acme.foo;
public class DateAdapter {
  public static Date parseDate(String s) {
    return DatatypeConverter.parseDate(s).getTime();
  }
  public static String printDate(Date dt) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(dt);
    return DatatypeConverter.printDate(cal);
  }
}
```

\... then your binding file will be the following:

``` {.xml}
<bindings xmlns="http://java.sun.com/xml/ns/jaxb" version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <globalBindings>
    <javaType name="java.util.Date" xmlType="xs:date"
      parseMethod="org.acme.foo.DateAadpter.parseDate"
      printMethod="org.acme.foo.DateAdapter.printDate"
    />
  </globalBindings>
</bindings>
```
