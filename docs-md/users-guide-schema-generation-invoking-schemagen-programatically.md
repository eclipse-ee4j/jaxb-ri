Invoking schemagen programatically {#schema-generation-invoking-schemagen-programatically}
==================================

Schemagen tools by default come in as CLI, ant task, and Maven plugin.
These interfaces allow you to invoke schemagen functionality from your
program.

At runtime {#At_runtime}
----------

If the classes you\'d like to generate schema from are already available
as `java.lang.Class` objects (meaning they are already loaded and
resolved in the current JVM), then the easiest way to generate a schema
is to use the JAXB API:

``` {.java}
File baseDir = new File(".");

class MySchemaOutputResolver extends SchemaOutputResolver {
    public Result createOutput( String namespaceUri, String suggestedFileName ) throws IOException {
        return new StreamResult(new File(baseDir,suggestedFileName));
    }
}

JAXBContext context = JAXBContext.newInstance(Foo.class, Bar.class, ...);
context.generateSchema(new MySchemaOutputResolver());
```

CLI interface {#CLI_interface}
-------------

The CLI interface (`public static int
        com.sun.tools.jxc.SchemaGenerator.run(String[])`) is the easiest
API to access. You can pass in all the schemagen command-line arguments
as a string array, and get the exit code as an int value. Messages are
sent to `System.err` and `System.out`.

Ant interface {#Ant_interface}
-------------

Ant task can be invoked very easily from a non-Ant program. The
schemagen ant task is defined in the SchemaGenTask class,

Native Java API {#Native_Java_API}
---------------

The above two interfaces are built on top of externally committed
contracts, so they\'ll evolve only in a compatibile way. The downside is
that the amount of control you can exercise over them would be limited.

So yet another approach to invoke schemagen is to use JAXB RI\'s
internal interfaces. But be warned that those interfaces are subject to
change in the future versions, despite our best effort to preserve them.
This is the API that the JAX-WS RI uses to generate schema inside WSDL
when they generate WSDL, so does some other web services toolkits that
work with the JAXB RI.

Most of those interfaces are defined and well-documented in the
com.sun.tools.xjc.api package. You can see how the schemagen tools are
eventually calling into this API at the implementaion of SchemaGenerator
class.
