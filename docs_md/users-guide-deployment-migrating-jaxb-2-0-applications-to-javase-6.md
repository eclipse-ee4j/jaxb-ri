Older versions of JAXB and JavaSE {#deployment-migrating-jaxb-2-0-applications-to-javase-6}
=================================

JavaSE 6 ships with its own JAXB 2.0 implementation. This implementation
is based on the JAXB RI, where the only differences are:

-   **No RI-specific vendor extensions are supported:** This is so that
    portability across different JavaSE 6 implementations will be
    guaranteed.

-   **Code in JavaSE 6 is hosted in its own packages to avoid
    conflicts:** This allows applications to continue to use a specific
    version of the JAXB RI that they choose to use.

Therefore, if you develop an application that uses JAXB 2.0 for JavaSE 5
today, the easiest way to upgrade to JavaSE 6 is to do nothing. You
should keep the JAXB RI in your development environment, keep bundling
the JAXB RI runtime jars to your app, just like you do that today.

Reducing footprint {#Reducing_footprint}
------------------

If you\'d like to reduce the footprint of your application by taking
advantage of a JAXB implementation in JavaSE, you can take the following
steps:

1.  You will no longer have to ship `jaxb-api.jar` in your application.
    This doesn\'t require any change to your code.

2.  If your application does not use any of the vendor extension
    features of the JAXB RI runtime (such as unmarshaller/marshaller
    properties whose names start with \"`com.sun.`\"), then you will no
    longer have to ship `jaxb-impl.jar` (nor `jaxb1-impl.jar`,
    `jaxb-libs.jar`.) When you do this, be sure to test your application
    since it\'s not very easy to find all such dependencies.

Using JAXB with Java SE {#Using_JAXB_with_JavaSE}
-----------------------

JavaSE comes with JAXB 2.x API/implementation in `rt.jar`. Each version
of JavaSE (6, 7, 8, \...) contains different version of JAXB 2.x API.
Therefore, if you want to use different version of JAXB
API/implementation than the one present in your version of JDK, you are
required to override a portion of `rt.jar` with the new API. There are
several ways to achieve this:

1.  Place the `jaxb-api.jar` into `$JRE_HOME/lib/endorsed`. **Do not put
    other JAXB jars into the endorsed directory.** And put jaxb-impl,
    jaxb-core to classpath of your application. This essentially makes
    your JRE to \"JRE X + JAXB 2.y\". This would affect any other
    applications that use this JRE, and it\'s easy. On the other hand,
    in various scenarios you may not be able to alter the JRE.

2.  Use the system property `java.endorsed.dirs` when you launch your
    application, and have it point to the directory which contains the
    `jaxb-api.jar` only. **The directory must not contain any other jaxb
    artifacts (like jaxb-impl.jar or jaxb-xjc.jar).** This allows you
    use to use different version of JAXB for different applications.

No matter which approach you take, make sure not to include jar files
other than `jaxb-api.jar`. Doing so, for example including
`jaxb-xjc.jar`, may result in classloading related errors such as
\"taskdef A class needed by class com.sun.tools.xjc.XJCTask cannot be
found: org/apache/tools/ant/\....\"

See [the endorsed directory
mechanism](http://docs.oracle.com/javase/6/docs/technotes/guides/standards/)
for more details.

Where\'s the XJC ant task? {#Where_s_the_XJC_ant_task_}
--------------------------

JavaSE has never shipped an Ant task implementation, so we are just
following that tradition. There\'s an (process-wise) overhead of adding
additional dependencies during the JavaSE build, and there would likely
be some runtime dependency issues in having a class in `tools.jar` that
would require the ant classes, due to class loader delegation.

We are thinking about perhaps releasing a small jar that only contains
the ant task for JDK6.

Please also note that the syntax of `<xjc>` task is neither defined in
the JAXB spec nor in the JavaSE spec. Therefore other JavaSE vendors may
not implement that at all, or do so in a different class name, etc.
Therefore, from a portability perspective, if you choose to depend on
the `<xjc>` task you should bundle the JAXB RI.
