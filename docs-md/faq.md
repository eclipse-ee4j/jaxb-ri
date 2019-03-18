Frequently Asked Questions {#faq}
==========================

JAXB 2.0
========

**Q:** Which version of J2SE does JAXB 2.0 require?

**A:** Java SE 6 or higher.

**Q:** Can I run my existing JAXB 1.0.x applications on the JAXB 2.0
runtime?

**A:** This is no longer supported. However, you should be able to
deploy
`http://search.maven.org/remotecontent?filepath=com/sun/xml/bind/jaxb1-impl/2.2.5-1/jaxb1-impl-2.2.5-1.jar`
with your with your application application.

**Q:** What if I want to port my JAXB 1.0.x application to JAXB 2.0?

**A:** You need to recompile your schema with the newer JAXB 2.0 xjc and
modify your application code to work with the new bindings.

**Q:** Are the JAXB runtime API\'s thread safe?

**A:** The JAXB Specification currently does not address the thread
safety of any of the runtime classes. In the case of the Oracle JAXB RI,
the `JAXBContext` class **is** thread safe, but the `Marshaller`,
`Unmarshaller`, and `Validator` classes **are not** thread safe.

For example, suppose you have a multi-thread server application that
processes incoming XML documents by JAXB. In this case, for the best
performance you should have just one instance of `JAXBContext` in your
whole application like this:

::: {.informalexample}
``` {.java}
class MyServlet extends HttpServlet {
    static final JAXBContext context = initContext();

    private static JAXBContext initContext() {
        return JAXBContext.newInstance("....", MyServlet.class.getClassLoader());
    }
}
```
:::

And each time you need to unmarshal/marshal/validate a document. Just
create a new `Unmarshaller`/`Marshaller`/`Validator` from this context,
like this:

::: {.informalexample}
``` {.java}
public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    Unmarshaller u = context.createUnmarshaller();
    u.unmarshal(...);
}
```
:::

This is the simplest safe way to use the JAXB RI from multi-threaded
applications.

If you really care about the performance, and/or your application is
going to read a lot of small documents, then creating `Unmarshaller`
could be relatively an expensive operation. In that case, consider
pooling `Unmarshaller` objects. Different threads may reuse one
`Unmarshaller` instance, as long as you don\'t use one instance from two
threads at the same time.

**Q:** Why can\'t I cast the unmarshalled object into the generated
type.

**A:** When you invoke `JAXBContext.newInstance("aaa.bbb.ccc")`, it
tries to load classes and resources using the same classloader used to
load the `JAXBContext` class itself. This classloader may be different
from the classloader which was used to load your application (see the
picture [figure\_title](#faq-figure-1)). In this case, you\'ll see the
above error. This problem is often seen with application servers, J2EE
containers, Ant, JUnit, and other applications that use sophisticated
class loading mechanisms.

![Parent/Child classloader](figures/classLoaderFAQ.gif){width="100%"}

With some applications, things get even more complicated when the
JAXB-generated code can be loaded by either classloader. In this case,
`JAXBContext.newInstance("aaa.bbb.ccc")` will work but the JVM ends up
loading two copies of the generated classes for each class loader. As a
result, unmarshalling works but an attempt to cast the returned object
into the expected type will fail, even though its `getClass().getName()`
returns the expected name.

The solution for both situations is to pass your curent class loader
like this:

::: {.informalexample}
``` {.java}
JAXBContext.newInstance("aaa.bbb.ccc", this.getClass().getClassLoader());
```
:::

In general, if you are writing code that uses JAXB, it is always better
to explicitly pass in a class loader, so that your code will work no
matter where it is deployed.

**Q:** Which jar files do I need to distribute with my application that
uses the JAXB RI?

**A:** For JAXB 2.2.x:

::: {.informalexample}
    $JAXB_HOME/mod/jakarta.xml.bind-api.jar
    $JAXB_HOME/mod/jaxb-impl.jar
:::

**Q:** How can I cause the `Marshaller` to generate `CDATA` blocks?

**A:** This functionality is not available from JAXB directly, but you
can configure an Apache Xerces-J `XMLSerializer` to produce `CDATA`
blocks. Please review the
[JaxbCDATASample.java](download/JaxbCDATASample.java) sample app for
more detail.

**Q:** Can I access \<xs:any/\> as a DOM node?

**A:** In JAXB 2.0, \<xs:any/\> is handled correctly without any
customization.

1.  If it\'s `strict`, it will map to `Object` or `List<Object>` and
    when you unmarshal documents, you\'ll get objects that map to
    elements (such as `JAXBElements` or classes that are annotated with
    `XmlRootElement`).

2.  If it\'s `skip`, it will map to `org.w3c.dom.Element` or
    `List<Element>` and when you unmarshal documents, you\'ll get DOM
    elements.

3.  If it\'s `lax`, it will map to the same as with `strict`, and when
    you unmarshal documents, you\'ll get either:

    1.  `JAXBElement`s

    2.  classes that are annotated with `XmlRootElement`

    3.  DOM elements

**Q:** How do I find out which version of the JAXB RI I\'m using?

**A:** Run the following command

::: {.informalexample}
``` {.cli}
$ java -jar jaxb-xjc.jar -version
```
:::

Alternatively, each JAXB jar has version information in its
`META-INF/MANIFEST.MF`, such as this:

::: {.informalexample}
    Manifest-Version: 1.0
    Ant-Version: Apache Ant 1.8.2
    Created-By: 1.6.0_29-b11 (Sun Microsystems Inc.)
    Specification-Title: Java Architecture for XML Binding
    Specification-Version: 2.2.6
    Specification-Vendor: Oracle Corporation
    Implementation-Title: JAXB Reference Implementation 
    Implementation-Version: 2.2.5-SNAPSHOT
    Implementation-Vendor: Oracle Corporation
    Implementation-Vendor-Id: com.sun
    Extension-Name: com.sun.xml.bind
    Build-Id: 02/09/2012 01:42PM (hudson)
    Class-Path: jakarta.xml.bind-api.jar
:::
