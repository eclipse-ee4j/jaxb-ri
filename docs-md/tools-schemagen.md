SchemaGen {#tools-schemagen}
=========

`schemagen` Overview
--------------------

The current schema generator can process either Java source files or
class files.

We also provide an Ant task to run the schema generator - see the
instructions for [???](#tools-schemagen-ant-task).

Launching `schemagen` {#section-182504251480881}
---------------------

The schema generator can be launched using the appropriate `schemagen`
shell script in the `bin` directory for your platform.

If your java sources/classes reference other classes, they must be
accessable on your system CLASSPATH environment variable, or they need
to be given to the tool by using the `-classpath`/ `-cp` options.
Otherwise you will see errors when generating your schema.

-   **Solaris/Linux**

    ``` {.cli}
    % path/to/jaxb/bin/schemagen.sh Foo.java Bar.java ...
    Note: Writing schema1.xsd
    ```

-   **Windows**

    ``` {.cli}
    > path\to\jaxb\bin\schemagen.bat Foo.java Bar.java ...
    Note: Writing schema1.xsd
    ```

`schemagen` Syntax
------------------

schemagen

OPTION

java files

::: {.informalexample}
    Usage: schemagen [-options ...] <java files> 

    Options: 
        -d <path>         :  Specify where to place processor and javac generated class files 
        -cp <path>        :  Specify where to find user specified files  
        -classpath <path> :  Specify where to find user specified files  
        -episode <file>   :  generate episode file for separate compilation
        -version          :  display version information
        -help             :  Display this usage message
:::

### Summary of Command Line Options {#switches-1}

**-episode**

:   Generates the \"episode file\", which is really just a JAXB
    customization file (but with vendor extensions specific to the JAXB
    RI, as of 2.1.) When people develop additional schemas that depend
    on what this schemagen invocation produces, they can use this
    episode file to have their generated code refer to your classes.

Generated Resource Files {#section-906211973953818}
------------------------

The current schema generator simply creates a schema file for each
namespace referenced in your Java classes. There is no way to control
the name of the generated schema files at this time. Use
[???](#tools-schemagen-ant-task) for that purpose.
