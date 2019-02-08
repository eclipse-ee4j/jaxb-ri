Develop Plugins {#jaxb-ri-extensions-develop-plugins}
===============

This document describes how to write an XJC plugin to extend the code
generation of XJC.

What Can A Plugin Do? {#section-5151430285858327}
---------------------

An XJC plugin participates in the code generation from a schema. It can
define its own customizations that users can use to control it, it can
access the code that the JAXB RI generates, it can generate additional
classes/methods/fields/annotations/comments, and it can also replace
some of the pluggability points in the compilation process, such as XML
name -\> Java name conversion.

As a show case of what a plugin can do, take a look at plugins hosted at
JAXB2-commons.

### Quick Start {#section-909129508009087}

To write a plugin, do the following simple steps.

1.  Write a class, say, `org.acme.MyPlugin` by extending
    `com.sun.tools.xjc.Plugin`. See javadoc for how to implement
    methods.

2.  Write the name of your plugin class in a text file and put it as
    `/META-INF/services/com.sun.tools.xjc.Plugin` in your jar file.

Users can then use your plugins by declaring an XJC ant task with your
jar files.

::: {.informalexample}
``` {.xml}
<taskdef name="xjc" classname="com.sun.tools.xjc.XJCTask">
    <classpath>
        <fileset dir="jaxb-ri/lib" includes="*.jar"/>
        <fileset dir="your-plugin" includes="*.jar"/>
    </classpath>
</taskdef>
```
:::

### Resources {#section-5979897283139746}

Although we will do our best to maintain the compatibility of the
interfaces, it is still subject to change at this point.
