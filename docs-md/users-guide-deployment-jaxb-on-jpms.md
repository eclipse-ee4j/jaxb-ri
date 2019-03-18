Using JAXB on JPMS {#deployment-jaxb-on-jpms}
==================

JavaSE 9 features [JSR 376 Java Platform Module System](#). Starting
from 2.4.0 JAXB supports JPMS and can be loaded and used from module
path. There are only a few things to be aware of.

JAXB classes openness {#Jaxb_pojo_openness}
---------------------

JAXB does reflectively access private members of the class, so client
application if loaded from module path needs to \"open\" packages
containing jaxb classes to JAXB. There are alternative JAXB
implementations, which will have different module names, JAXB requires
pojo classes to be open only to API module.

``` {.java}
                //JPMS module descriptor
                module com.example.jaxbclasses {

                //jaxb-api module name
                requires java.xml.bind;

                //open jaxb pojo package to make accessing private members possible for JAXB.
                opens com.example.jaxbclasses.pojos to java.xml.bind;

             
```

JAXB API will delegate openness to implementation module after resolving
it with service discovery mechanism.

Upgrading JavaSE modules on Java 9 and 10. {#Jaxb_api_upgrade}
------------------------------------------

Prior to Java 11, JavaSE contains JAXB bundled inside JDK. Module
`java.xml.bind` contains JAXB API and runtime.

-   If bundled JavaSE JAXB is used, there is no need to provide
    standalone JAXB jars to java runtime.

-   If standalone JAXB is used, JavaSE bundled java.xml.bind module
    should be replaced with JAXB API.

Similar to [endorsed mechanism](#) prior to Java 9, starting from 9
there is an \"upgrade module\" mechanism which can replace content of
JDK module. JavaSE bundled `java.xml.bind` module contains both API and
Runtime classes and should be replaced with JAXB API module, which has
the same `java.xml.bind` module name.

``` {.cli}
                    # Using JAXB standalone jars
                    # Replace JavaSE bundled java.xml.bind with standalone API jar
                    user@host: java com.example.jaxb.Main -cp jaxbclient.jar --upgrade-module-path path/to/jakarta.xml.bind-api.jar
                    --module-path path/to/jaxb-runtime.jar --add-modules com.sun.xml.bind

                    #Same as above with client on module path
                    user@host: java -m com.example.jaxbclasses/com.example.jaxb.Main --upgrade-module-path path/to/jakarta.xml.bind-api.jar
                    --module-path path/to/jaxb-runtime.jar:jaxbclient.jar --add-modules com.sun.xml.bind

             
```

\--upgrade-module-path will replace JavaSE java.xml.bind (runtime and
API) with jakarta.xml.bind-api.jar contents.

Since java.xml.bind module [is removed starting from Java 11, there is
no need to upgrade this module on 11 and later.](#)

``` {.cli}

                    # Using JAXB bundled in JDK
                    # No need for standalone jars here
                    user@host: java com.example.jaxb.Main -cp jaxbclient.jar

                    user@host: java -m com.example.jaxbclasses/com.example.jaxb.Main
                    --module-path jaxbclient.jar

             
```
