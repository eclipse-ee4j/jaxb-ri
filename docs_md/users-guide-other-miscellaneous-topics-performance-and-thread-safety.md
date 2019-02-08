Performance and thread-safety {#other-miscellaneous-topics-performance-and-thread-safety}
=============================

The JAXBContext class is thread safe, but the Marshaller, Unmarshaller,
and Validator classes are not thread safe.

For example, suppose you have a multi-thread server application that
processes incoming XML documents by JAXB. In this case, for the best
performance you should have just one instance of JAXBContext in your
whole application like this:

``` {.java}
class MyServlet extends HttpServlet {
    static final JAXBContext context = initContext();

    private static JAXBContext initContext() {
        return JAXBContext.newInstance(Foo.class,Bar.class);
    }
}
```

And each time you need to unmarshal/marshal/validate a document. Just
create a new Unmarshaller/Marshaller/Validator from this context, like
this:

``` {.java}
    public void doGet( HttpServletRequest req, HttpServletResponse ) {
        Unmarshaller u = context.createUnmarshaller();
        u.unmarshal(...);
    }
```

This is the simplest safe way to use the JAXB RI from multi-threaded
applications.

If you really care about the performance, and/or your application is
going to read a lot of small documents, then creating Unmarshaller could
be relatively an expensive operation. In that case, consider pooling
Unmarshaller objects. Different threads may reuse one Unmarshaller
instance, as long as you don\'t use one instance from two threads at the
same time.
