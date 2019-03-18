SchemaGen Ant Task {#tools-schemagen-ant-task}
==================

`schemagen` Task Overview
-------------------------

The `jaxb-xjc.jar` file contains the `SchemaGenTask.class` file, which
allows the schema generator to be invoked from the
[Ant](http://jakarta.apache.org/ant) build tool. To use `SchemaGenTask`,
include the following statement in your `build.xml` file:

::: {.informalexample}
``` {.xml}
<taskdef name="schemagen"
         classname="com.sun.tools.jxc.SchemaGenTask">
    <classpath>
        <fileset dir="path/to/jaxb/lib" includes="*.jar"/>
    </classpath>
</taskdef>
```
:::

This maps `SchemaGenTask` to an Ant task named `schemagen`. For detailed
examples of using this task, refer to the `build.xml` files used by the
java to schema [???](#jaxb-2-0-sample-apps).

`schemagen` Task Attributes {#section-417846742205862}
---------------------------

### Environment Variables {#section-077165761436022}

-   [ANT\_OPTS](http://wiki.apache.org/ant/TheElementsOfAntStyle) -
    command-line arguments that should be passed to the JVM. For
    example, you can define system properties or set the maximum Java
    heap size here.

### Parameter Attributes {#section-914482811856355}

`schemagen` supports most of the attributes defined by [the javac
task](http://ant.apache.org/manual/CoreTasks/javac.html), plus the
following parameter attributes.

  **Attribute**   **Description**                                                                                                       **Required**
  --------------- --------------------------------------------------------------------------------------------------------------------- --------------
  `destdir`       Base directory to place the generated schema files                                                                    No
  `classpath`     Works just like the nested \<classpath\> element                                                                      No
  `episode`       If specified, generate an episode file in the specified name. For more about the episode file, see [???](#episode).   No

### Nested Elements {#section-107210544982149}

`xjc` supports all the nested elements defined by [the javac
task](http://ant.apache.org/manual/CoreTasks/javac.html), the following
nested element parameters.

#### `schema` {#section-1989087498722346}

Control the file name of the generated schema. This element takes a
mandatory `namespace` attribute and a mandaotry `file` attribute. When
this element is present, the schema document generated for the specified
namespace will be placed in the specified file name.

The file name is interpreted as relative to the destdir attribute. In
the absence of the destdir attribute, file names are relative to the
project base directory. This element can be specified multiple times.

#### `classpath` {#section-652997891191299}

A [path-like structure](http://ant.apache.org/manual/using.html#path)
that represents the classpath. If your Java sources/classes depend on
other libraries, they need to be available in the classpath.

`schemagen` Examples {#section-9298229576523986}
--------------------

Generate schema files from source files in the `src` dir and place them
in the `build/schemas` directory.

::: {.informalexample}
``` {.xml}
<schemagen srcdir="src" destdir="build/schemas">
```
:::

Compile a portion of the source tree.

::: {.informalexample}
``` {.xml}
<schemagen destdir="build/schemas">
    <src path="src"/>
    <exclude name="Main.java"/>
</schemagen>
```
:::

Set schema file names.

::: {.informalexample}
``` {.xml}
<schemagen srcdir="src" destdir="build/schemas">
    <schema namespace="http://myschema.acme.org/common"
            file="myschema-common.xsd"/>
    <schema namespace="http://myschema.acme.org/onion"
            file="myschema-onion.xsd"/>
</schemagen>
```
:::
