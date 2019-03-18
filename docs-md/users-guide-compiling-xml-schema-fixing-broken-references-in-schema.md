Fixing broken references in schema {#compiling-xml-schema-fixing-broken-references-in-schema}
==================================

Sometimes a schema may refer to another schema document without
indicating where the schema file can be found, like this:

``` {.xml}
<xs:import namespace="http://www.w3.org/1999/xlink" />
```

In other cases, a schema may refer to another schema on the network,
which often slows down your compilation process and makes it unreliable.
Yet in some other cases, a schema may reference another schema in
relative path, and that may not match your directory structure.

XJC bundles a [catalog
resolver](http://xml.apache.org/commons/components/resolver/resolver-article.html)
so that you can work around these situations without changing the schema
documents. The main idea behind the catalog is \"redirection\" \-\--
when XJC is about to fetch resources, it will consult the catalog
resolver to see if it can find the resource elsewhere (which is usually
your local resources.)

Catalog format {#Catalog_format}
--------------

The catalog resolver supports many different formats, but the easiest
one is a line based `*.cat` format. Other than comments and empty lines,
the file mainly consists of two kinds of declarations, `SYSTEM`, and
`PUBLIC`.

``` {.xml}
--
  sample catalog file.

  double hyphens are used to begin and end a comment section.
--

SYSTEM "http://www.w3.org/2001/xml.xsd" "xml.xsd"

PUBLIC "-//W3C//DTD XMLSCHEMA 200102//EN" "s4s/XMLSchema.dtd"
```

Resolve by system ID {#Resolve_by_system_ID}
--------------------

The SYSTEM entry has the format of \"SYSTEM *REFERENCE*
*ACTUAL-LOCATION*\", which defines a simple redirection. Every time XJC
loads any resource (be it schemas, DTDs, any entities referenced
within), it will first resolve relative paths to absolute paths, then
looks for a matching *REFERENCE* line. If it is found, the specified
actual location is read instead. Otherwise XJC will attempt to resolve
the absolutepath.

*ACTUAL-LOCATION* above accepts relative paths, and those are resolved
against the catalog file itself (so in the above example, `xml.xsd` is
assumed to be in the same directory with `sample-catalog.cat`.

What you need to be careful is the fact that the *REFERENCE* portion
must be absolute, and when XJC finds a reference in schema, it will
first convert that to the absolute path before checking the catalog. So
what this means is that if your schema is written like this:

``` {.xml}
<xs:import namespace="http://www.w3.org/1999/xlink" schemaLocation="xlink.xsd" />
```

Then your catalog entry would have to look like this:

``` {.xml}
-- this doesn't work because xlink.xsd will be turned into absolute path --
SYSTEM "xlink.xsd" "http://www.w3.org/2001/xlink.xsd"

-- this will work, assuming that the above schema is in /path/to/my/test.xsd --
SYSTEM "/path/to/my/xlink.xsd" "http://www.w3.org/2001/xlink.xsd"
```

Resolve by public ID / namespace URI {#Resolve_by_public_ID___namespace_URI}
------------------------------------

Another kind of entry has the format of \"PUBLIC *PUBLICID*
*ACTUAL-LOCATION*\" or \"PUBLIC *NAMESPACEURI* *ACTUAL-LOCATION*\".

The \"`PUBLICID`\" version is used to resolve DTDs and entities in DTDs.
But this type of entry is also used to resolve `<xs:import>` statements.
XJC will match the value of the namespace attribute and see if there\'s
any matching entry. So given a schema like this:

``` {.xml}
<xs:import namespace="http://www.w3.org/1999/xlink" schemaLocation="xlink.xsd" />
<xs:import namespace="http://www.w3.org/1998/Math/MathML" />
```

The following catalog entries will match them.

``` {.xml}
PUBLIC "http://www.w3.org/1999/xlink" "http://www.w3.org/2001/xlink.xsd"
PUBLIC "http://www.w3.org/1998/Math/MathML" "/path/to/my/mathml.xsd"
```

As you can see, XJC will check the PUBLIC entries regardless of whether
`<xs:import>` has the schemaLocation attribute or not. As with the case
with the SYSTEM entry, the ACTUAL-LOCATION part can be relative to the
location of the catalog file.

Specifying the catalog file {#Specifying_the_catalog_file}
---------------------------

Once you write a catalog file, you\'d need to specify that when you
invoke XJC.

CLI

:   To do this from the CLI, use the `-catalog` option. See [`xjc
                        -help` for more
    details](/nonav/2.1.4/docs/xjc.html).

Ant

:   Use the catalog attribute on the `<xjc>` task. See [XJC ant task
    documentation](/nonav/2.1.4/docs/xjcTask.html) for more details.

Maven

:   For the Maven plugin, use the `<catalog>` element in the
    configuration:

    ::: {.informalexample}
        <plugin>
            <groupId>org.jvnet.jaxb2.maven2</groupId>
            <artifactId>maven-jaxb2-plugin</artifactId>
            <configuration>
                <!-- relative to the POM file -->
                <catalog>mycatalog.cat</catalog>
            </copnfiguration>
        </plugin>
    :::

Debugging catalog file {#Debugging_catalog_file}
----------------------

If you are trying to write a catalog file and banging your head against
a wall because it\'s not working, you should enable the verbose option
of the catalog resolver. How you do this depends on what interface you
use:

CLI

:   Specify `export
                        XJC_OPTS="-Dxml.catalog.verbosity=999"` then run
    XJC.

Ant/Maven

:   Add `-Dxml.catalog.verbosity=999` as a command line option to
    Ant/Maven.

If you are otherwise invoking XJC programmatically, you can set the
above system property before invoking XJC.
