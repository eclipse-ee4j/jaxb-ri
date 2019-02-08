Changelog {#jaxb-2-0-changelog}
=========

The JAXB 2.x RI is a major reimplementation to meet the requirements of
the 2.x specifications.

Please refer to the jaxb-1-0-x-changelog for older releases.

Changes between 2.3.1 and 2.3.2 {#a-2-3-2}
-------------------------------

-   First release under Eclipse Jakarta EE Platform:

    -   Uptake of renamed Jakarta APIs.

Changes between 2.3.0.1 and 2.3.1 {#a-2-3-1}
---------------------------------

-   JAXB RI is now JPMS modularized:

    -   All modules have native module descriptor.

    -   Removed jaxb-core module, which caused split package issue on
        JPMS.

    -   RI binary bundle now has single jar per dependency instead of
        shaded fat jars.

    -   Removed runtime class weaving optimization.

Changes between 2.3.0 and 2.3.0.1 {#a-2-3-0-1}
---------------------------------

-   Removed legacy technology dependencies:

    -   com.sun.xml.bind:jaxb1-impl

    -   net.java.dev.msv:msv-core

    -   net.java.dev.msv:xsdlib

    -   com.sun.xml.bind.jaxb:isorelax

Changes between 2.2.11 and 2.3.0 {#a-2-3-0}
--------------------------------

-   Adopt Java SE 9:

    -   JAXB api can now be loaded as a module.

    -   JAXB RI is able to run on Java SE 9 from the classpath.

    -   Addes support for java.util.ServiceLoader mechanism.

    -   Security fixes

Changes between 2.2.10 to 2.2.11 {#a-2-2-11}
--------------------------------

-   Bug fixes:

    -   Fixed split Message bundles between modules

    -   Changed Codemodel to allow correct functioning of BeanValidation
        Plugin for XJC

    -   Security fixes

    -   Fixed `'-disableXmlSecurity'` flag usage

    -   Fixed redundant namespace prefixes

    -   Fixed mixed content for StAX

    -   Now we generate OSGI manifests for jaxb-core, jaxb-impl,
        jaxb-xjc anc jaxb-jxc

    -   Marked `com.sun.org.apache.xml.internal.resolver` and
        `com.sun.org.apache.xml.internal.resolver.tools` as optional.
        They are part of JDK.

    -   [JAXB-973](https://github.com/eclipse-ee4j/jaxb-ri/issues/973):
        generated code should be compilable with `'-Xdoclint:all'`

    -   [JAXB-598](https://github.com/eclipse-ee4j/jaxb-ri/issues/598):
        XJC generates ordered ObjectFactories, `@XmlSeeAlso` annotations
        and episode files.

Changes between 2.2.9 to 2.2.10 {#a-2-2-10}
-------------------------------

-   Bug fixes:

    -   Fixed for preserving whitespaces within xsd:any mixed element

    -   Security fixes

    -   Fixed circular dependency between SAAJ and JAXB; stax-ex changed
        not to use SAAJ-API

    -   Fixed catalog logging. Allow user to disable usage of system
        proxies. Force CatalogResolver to set verbosity level.

    -   Fixed stream handling

    -   Fixed EnvelopeFactory

Changes between 2.2.8 to 2.2.9 {#a-2-2-9}
------------------------------

-   Bug fixes:

    -   Security fixes.

    -   JAXB-926
        \- Fixed optional property behavior for PRIMITIVE value.
    -   Fixed wrong ref element generation

    -   Fixed OSGi bug:
        `NoClassDefFoundError javax/xml/soap/soapelement`

Changes between 2.2.7 to 2.2.8 {#a-2-2-8}
------------------------------

-   This release is integrated into JDK 8. (Build `2.2.8-b130911.1802`)

-   JAXB RI project is now mavenized. New maven groupId introduced:
    org.glassfish.jaxb So if you are use maven you can simply add
    dependency block:

                            <dependency>
                                <groupId>org.glassfish.jaxb</groupId>
                                <artifactId>jaxb-bom</artifactId>
                                <version>2.2.8</version>
                                <type>pom</type>
                            </dependency>
                        

-   From now JAXB RI project uses GIT VCS. You can get our sources from:

                            GitHub:     git clone https://github.com/eclipse-ee4j/jaxb-ri.git
                        

-   Bug fixes:

    -   Integrated new osgi-fied api

    -   Fixed the bug with namespaces. Namespace must match in order to
        be able to generate ref otherwise the generated element ref
        belongs to different namespace. Ref generation is isolated only
        to cases where package mapping prevails

    -   Updated access to JAX-WS

    -   New JAXP version support added.

    -   Fixed broken links in documentation

    -   Fixed userguide

    -   \[14606308, 14743364\] - Fix resolution of referenced simpleType
    -   JXC findBugs fixes.

    -   Added txw to jaxb source repo

    -   Fixed bug when JAXB marshaller setting xsi:nil as true for
        non-nillable elements

    -   Fixed event handling for missing xsi type

        -   added errorCounter

        -   added logging level check

    -   Updated JAXB API with licence/fbugs fixes, and osgi fix

    -   [JAX\_WS-1114](https://github.com/javaee/metro-jax-ws/issues/1114):
        Fixed marshalling of gMonth type.

        -   To enable old behavior use switch:
            jaxb.ri.useOldGmonthMapping

    -   [JAXB-937](https://github.com/eclipse-ee4j/jaxb-ri/issues/937):
        Fixed Coordinator ThreadLocal

    -   Fixed invalid absolute URL

    -   Removed the need for synchronization - tailor can still happen
        multiple times under specific circumstances, but it\'s better
        than contention within multiple threads on one class

    -   Fixed SimpleNameClass dependency

    -   Fixed managing of nested collections.

    -   Fixed maven configuration: made JDK deps required, map
        jaxb-extra-osgi exports by version, removed dep on `com.sun.nio`

    -   `Codemodel` is imported into JAXB project tree

    -   Security fixes

    -   Added JAXB OSGi test.

Changes between 2.2.6 to 2.2.7 {#a-2-2-7}
------------------------------

-   JAXB 1.0 runtime is restored. It\'s bundled into the
    `jaxb-extra-osgi.jar` now. Required libraries are located in
    `$JAXB_HOME/tools/jaxb1_libs` folder.

-   The processing of -source XJC option is removed. We use 2.0 version
    of the schema compiler. For generating 1.0.x code, please use an
    installation of the 1.0.x codebase.

-   `resolver.jar` is not bundled by JAXB anymore. It\'s already part of
    JDK. If you are using JDK which doesn\'t contain it - you have to
    download it manually (e.g. from maven:
    `com.sun.org.apache.xml.internal:resolver`).

-   JAXB project was split into several modules: core, impl, xjc and
    jxc. So now it is possible to use XJC with other JAXB runtime
    implementation (e.g. EclipseLink MOXy). While using XJC be aware of
    having any runtime JAXB implementation (respectively `jaxb-impl.jar`
    ) on classpath.

-   Bug fixes:

    -   [JAXB-452](https://github.com/eclipse-ee4j/jaxb-ri/issues/452) -
        Embedded DOM Elements may lose locally defined namespace
        prefixes

    -   [JAXB-915](https://github.com/eclipse-ee4j/jaxb-ri/issues/915) -
        Cannot generate java classes from xjc

    -   [JAXB-919](https://github.com/eclipse-ee4j/jaxb-ri/issues/919) -
        xjc - proxy authentication fails

    -   [JAXB-922](https://github.com/eclipse-ee4j/jaxb-ri/issues/922) -
        JAXB samples fail

    -   [JAXB-939](https://github.com/eclipse-ee4j/jaxb-ri/issues/939) -
        \'\#\' at the end of a namespace : different of behavior,
        sometimes a underscore is added, sometimes not.

    -   [JAXB-940](https://github.com/eclipse-ee4j/jaxb-ri/issues/940) -
        wrong default value for \"target\" parameter for xjc ant task

    -   [JAXB-948](https://github.com/eclipse-ee4j/jaxb-ri/issues/948) -
        JAXB differences in JDK 7 in
        `com/sun/xml/internal/bind/v2/runtime/reflect/Lister.java#Lister.CollectionLister.endPacking`

-   Improvement:

    -   [JAXB-912](https://github.com/eclipse-ee4j/jaxb-ri/issues/912) -
        XJC split

Changes between 2.2.5-2 to 2.2.6 {#a-2-2-6}
--------------------------------

-   Project cleaning done:

    -   JAXB 1.0 is not bundled anymore.

    -   Libraries `jaxb1-xjc.jar` , `msv.jar` was removed.

    -   Package com/sun/msv/datatype was extracted to
        `jaxb-extra-osgi.jar`

-   Bug fixes:

    -   [JAXB-890](https://github.com/eclipse-ee4j/jaxb-ri/issues/890) -
        JAXB Unmarshaller tries to instantiate abstract class ignoring
        xsi:type if nillable=\"true\".

    -   [JAXB-871](https://github.com/eclipse-ee4j/jaxb-ri/issues/871) -
        Disabled fields and multiple-inherence (override once =\>
        override for sub-classes).

    -   [JAXB-900](https://github.com/eclipse-ee4j/jaxb-ri/issues/900) -
        MarshalException for XmlIDREF used on interfaces.

Changes between 2.2.5-1 to 2.2.5-2 {#a-2-2-5u2}
----------------------------------

-   Fixed version number in OSGi bundles

Changes between 2.2.5 to 2.2.5-1 {#a-2-2-5u1}
--------------------------------

-   [JAX\_WS-1059](https://github.com/javaee/metro-jax-ws/issues/1059) -
    wsimport Ant tasks causes NoClassDefFoundError

Changes between 2.2.4-1 to 2.2.5 {#a-2-2-5}
--------------------------------

-   [JAXB-415](https://github.com/eclipse-ee4j/jaxb-ri/issues/415) -
    Marshaller.marshall throws NPE if an adapter adapts a non-null bound
    value to null.

-   [JAXB-488](https://github.com/eclipse-ee4j/jaxb-ri/issues/488) -
    compatibility issue with JAXB 2.0

-   [JAXB-608](https://github.com/eclipse-ee4j/jaxb-ri/issues/608) -
    javax.xml.bind.DatatypeConverter.parseBoolean incorrect

-   [JAXB-616](https://github.com/eclipse-ee4j/jaxb-ri/issues/616) -
    XMLCatalog not used from xjc/xjctask when strict validation is
    enabled

-   [JAXB-617](https://github.com/eclipse-ee4j/jaxb-ri/issues/617) -
    setting Marshaller CharacterEncodingHandler with utf-8 does not work

-   [JAXB-790](https://github.com/eclipse-ee4j/jaxb-ri/issues/790) -
    Whitespace processing for xs:anyURI type

-   [JAXB-794](https://github.com/eclipse-ee4j/jaxb-ri/issues/794) -
    Classes annotated with `@XmlTransient` are still mapped to XML

-   [JAXB-795](https://github.com/eclipse-ee4j/jaxb-ri/issues/795) -
    Catalog passed to XJC is not used during the schema correctness
    check

-   [JAXB-814](https://github.com/eclipse-ee4j/jaxb-ri/issues/814) -
    Failing with \'no-arg default constructor\' on `@XmlTransient`

-   [JAXB-825](https://github.com/eclipse-ee4j/jaxb-ri/issues/825) -
    Attribute with default generates different types for get and set
    methods

-   [JAXB-831](https://github.com/eclipse-ee4j/jaxb-ri/issues/831) -
    Memory leak in `com.sun.xml.bind.v2.ClassFactory`

-   [JAXB-834](https://github.com/eclipse-ee4j/jaxb-ri/issues/834) -
    Cannot use binding file on Windows

-   [JAXB-836](https://github.com/eclipse-ee4j/jaxb-ri/issues/836) -
    CLONE -Marshaller.marshall throws NPE if an adapter adapts a
    non-null bound value to null.

-   [JAXB-837](https://github.com/eclipse-ee4j/jaxb-ri/issues/837) - XJC
    generated code misses out some fields from XML

-   [JAXB-843](https://github.com/eclipse-ee4j/jaxb-ri/issues/843) -
    Marshaller holds reference after marshalling for the writer that was
    used to marshal into

-   [JAXB-844](https://github.com/eclipse-ee4j/jaxb-ri/issues/844) -
    Memory Leak in com.sun.xml.bind.v2.runtime.Coordinator

-   [JAXB-847](https://github.com/eclipse-ee4j/jaxb-ri/issues/847) -
    DataTypeConverterImpl patch causes corrupted stream generation in
    some cases

-   [JAXB-849](https://github.com/eclipse-ee4j/jaxb-ri/issues/849) -
    JAXB:Invalid boolean values added to lists as \'false\'

-   [JAXB-856](https://github.com/eclipse-ee4j/jaxb-ri/issues/856) -
    xs:import namespace=\"http://www.w3.org/2005/05/xmlmime\" is not
    generated

-   [JAXB-858](https://github.com/eclipse-ee4j/jaxb-ri/issues/858) - xjc
    generates no JAXBElement for nillable element with required
    attribute

-   [JAXB-859](https://github.com/eclipse-ee4j/jaxb-ri/issues/859) -
    Corrupt license file in the distribution

-   [JAXB-860](https://github.com/eclipse-ee4j/jaxb-ri/issues/860) -
    `NullPointerException com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.checkOverrideProperties(ClassBeanInfoImpl.java`:205)

-   [JAXB-867](https://github.com/eclipse-ee4j/jaxb-ri/issues/867) -
    jax-ws / jax-b / glassfish 3.1.1 web services fail to send data from
    beans based on variable name.

-   [JAXB-868](https://github.com/eclipse-ee4j/jaxb-ri/issues/868) -
    Escape policy for quote (\") is different when the serialization is
    performed to OutputStream or Writer

-   [JAXB-869](https://github.com/eclipse-ee4j/jaxb-ri/issues/869) -
    Multiple \<any /\> elements on a schema : second element is not
    loaded

-   [JAXB-882](https://github.com/eclipse-ee4j/jaxb-ri/issues/882) -
    Marshalling Objects extending `JAXBElement` linked by
    `@XmlElementRef`

-   [JAXB-445](https://github.com/eclipse-ee4j/jaxb-ri/issues/445) -
    Generated episode bindings should contain target package name

-   [JAXB-499](https://github.com/eclipse-ee4j/jaxb-ri/issues/499) -
    Umbrella issue for all XJC related encoding issues wrt xjc itself
    and maven plugins/ant tasks

-   [JAXB-839](https://github.com/eclipse-ee4j/jaxb-ri/issues/839) -
    More Schema Annotations/Documentation to Javadoc

Changes between 2.2.4 to 2.2.4-1 {#a-2-2-4u1}
--------------------------------

-   [JAXB-834](https://github.com/eclipse-ee4j/jaxb-ri/issues/834) -
    Cannot use binding file on Windows

-   [JAXB-835](https://github.com/eclipse-ee4j/jaxb-ri/issues/835) - XJC
    fails with project path that contains spaces

Changes between 2.2.3u2 to 2.2.4 {#a-2-2-4}
--------------------------------

-   [JAXB-413](https://github.com/eclipse-ee4j/jaxb-ri/issues/413) -
    Redundant cast to `byte[]` in code generated by XJCTask

-   [JAXB-416](https://github.com/eclipse-ee4j/jaxb-ri/issues/416) - Ant
    XJC task problems with spaces in paths

-   [JAXB-450](https://github.com/eclipse-ee4j/jaxb-ri/issues/450) -
    Reusing unmarshaller results in an unexpected result

-   [JAXB-549](https://github.com/eclipse-ee4j/jaxb-ri/issues/549) -
    Unescaped javadoc in `@XmlElements`

-   [JAXB-602](https://github.com/eclipse-ee4j/jaxb-ri/issues/602) -
    Different unmarshalling behavior between primitive type and
    simpletype with enumeration under restriction

-   [JAXB-618](https://github.com/eclipse-ee4j/jaxb-ri/issues/618) - XJC
    generates certain code lines in a random order

-   [JAXB-620](https://github.com/eclipse-ee4j/jaxb-ri/issues/620) -
    Problems with abstract classes and `@XMLValue`

-   [JAXB-652](https://github.com/eclipse-ee4j/jaxb-ri/issues/652) -
    JAXB 2.2 API should be changed to work with old 2.1 jaxb
    implementation in JDK

-   [JAXB-696](https://github.com/eclipse-ee4j/jaxb-ri/issues/696) -
    Incorrect implementation/documentation of
    javax.xml.bind.annotation.adapters.NormalizedStringAdapter

-   [JAXB-726](https://github.com/eclipse-ee4j/jaxb-ri/issues/726) -
    Missing keyword \'throw\'

-   [JAXB-761](https://github.com/eclipse-ee4j/jaxb-ri/issues/761) -
    DatatypeConverterInterface.printDate inconsistencies

-   [JAXB-774](https://github.com/eclipse-ee4j/jaxb-ri/issues/774) -
    Extra slash in XSD location prevents customization

-   [JAXB-803](https://github.com/eclipse-ee4j/jaxb-ri/issues/803) -
    2.2.2 strips schemaLocation in binding files

-   [JAXB-804](https://github.com/eclipse-ee4j/jaxb-ri/issues/804) -
    JAXB 2.x : How to override an XmlElement annotation from parent
    class - Mission Impossible?

-   [JAXB-813](https://github.com/eclipse-ee4j/jaxb-ri/issues/813) -
    Preserving white-space in XML (xs:string enumeration value) does not
    work

-   [JAXB-815](https://github.com/eclipse-ee4j/jaxb-ri/issues/815) -
    Binding file cannot refer to schema file with space in file name.

-   [JAXB-816](https://github.com/eclipse-ee4j/jaxb-ri/issues/816) -
    Incorrect System property to define the provider factory class

-   [JAXB-821](https://github.com/eclipse-ee4j/jaxb-ri/issues/821) -
    Global customization are not applied to xjc when input document is
    WSDL

-   [JAXB-824](https://github.com/eclipse-ee4j/jaxb-ri/issues/824) - API
    files in javax.xml.bind need to be updated for references to JLS
    with editions/hyperlinks

-   [JAXB-826](https://github.com/eclipse-ee4j/jaxb-ri/issues/826) -
    JAXB fails to unmarshal attributes as properties on aix

-   [JAXB-268](https://github.com/eclipse-ee4j/jaxb-ri/issues/268) - Map
    handling broken

-   [JAXB-470](https://github.com/eclipse-ee4j/jaxb-ri/issues/470) -
    Potential changes to make JAXB work better with OSGi

-   [JAXB-478](https://github.com/eclipse-ee4j/jaxb-ri/issues/478) -
    jaxb2-sources : Allow compilation under Java 6

-   [JAXB-633](https://github.com/eclipse-ee4j/jaxb-ri/issues/633) -
    JDefinedClass getMods()

-   [JAXB-768](https://github.com/eclipse-ee4j/jaxb-ri/issues/768) -
    Mailing list consolidation suggestions

-   [JAXB-784](https://github.com/eclipse-ee4j/jaxb-ri/issues/784) -
    JAnnotationUse should provide getters for clazz and memberValues
    properties

-   [JAXB-406](https://github.com/eclipse-ee4j/jaxb-ri/issues/406) -
    Allow setting of access modifiers in JMods

-   [JAXB-769](https://github.com/eclipse-ee4j/jaxb-ri/issues/769) -
    Update to command-line help text

-   [JAXB-772](https://github.com/eclipse-ee4j/jaxb-ri/issues/772) -
    Updates to XJC.html page and -Xpropertyaccessors plugin

-   [JAXB-783](https://github.com/eclipse-ee4j/jaxb-ri/issues/783) -
    I18N: xjc generates localized strings of AM/PM so compilation fails

Notable Changes between 2.2.3u1 to 2.2.3u2 {#a-2-2-3u2}
------------------------------------------

-   [JAXB-826](https://github.com/eclipse-ee4j/jaxb-ri/issues/826) -
    JAXB fails to unmarshal attributes as properties on AIX

Notable Changes between 2.2.3 to 2.2.3u1 {#a-2-2-3u1}
----------------------------------------

-   [JAXB-805](https://github.com/eclipse-ee4j/jaxb-ri/issues/805) -
    SpecJ performance regression

Notable Changes between 2.2.2 to 2.2.3 {#a-2-2-3}
--------------------------------------

-   6975714 - JAXB 2.2 throws IAE for invalid Boolean values

-   [JAXB-620](https://github.com/eclipse-ee4j/jaxb-ri/issues/620) -
    Problems with abstract classes (xsi type processing)

-   Regression in Jersey JSON mapping

Notable Changes between 2.2.1.1 to 2.2.2 {#a-2-2-2}
----------------------------------------

-   [Specification
    changelog](http://jcp.org/aboutJava/communityprocess/maintenance/jsr222/index2.html)

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.2.1)

Notable Changes between 2.2.1 to 2.2.1.1 {#a-2-2-1-1}
----------------------------------------

-   This minor-minor release contains only changes relevant to GlassFish
    OSGi environment

Notable Changes between 2.2 to 2.2.1 {#a-2-2-1}
------------------------------------

-   [Specification
    changelog](http://jcp.org/aboutJava/communityprocess/maintenance/jsr222/index2.html)

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.2)

Notable Changes between 2.1.12 to 2.2 {#a-2-2}
-------------------------------------

-   [Specification
    changelog](http://jcp.org/aboutJava/communityprocess/maintenance/jsr222/index2.html)

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.12)

Notable Changes between 2.1.12 to 2.1.13 {#a-2-1-13}
----------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.12)

Notable Changes between 2.1.11 to 2.1.12 {#a-2-1-12}
----------------------------------------

-   <http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6846148>

-   <https://github.com/jersey/jersey/issues/385>

-   <https://github.com/jersey/jersey/issues/544>

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.11)

Notable Changes between 2.1.10 to 2.1.11 {#a-2-1-11}
----------------------------------------

-   Minor licencing and legal fixes. No changes to source code.

Notable Changes between 2.1.9 to 2.1.10 {#a-2-1-10}
---------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.9)

Notable Changes between 2.1.8 to 2.1.9 {#a-2-1-9}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.8)

Notable Changes between 2.1.7 to 2.1.8 {#a-2-1-8}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.7)

Notable Changes between 2.1.6 to 2.1.7 {#a-2-1-7}
--------------------------------------

-   Fixed documentation that incorrectly showed that JAXB RI is CDDL
    only (it\'s actually CDDL/GPLv2+classpath dual license)

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.6)

Notable Changes between 2.1.5 to 2.1.6 {#a-2-1-6}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.5)

Notable Changes between 2.1.4 to 2.1.5 {#a-2-1-5}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.4)

Notable Changes between 2.1.3 to 2.1.4 {#a-2-1-4}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.3)

Notable Changes between 2.1.2 to 2.1.3 {#a-2-1-3}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.2)

Notable Changes between 2.1.1 to 2.1.2 {#a-2-1-2}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1.1)

Notable Changes between 2.1 First Customer Ship (FCS) to 2.1.1 {#a-2-1-1}
--------------------------------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.1)

-   [???](#substitutable)

Notable Changes between 2.1 Early Access 2 to 2.1 First Customer Ship (FCS) {#a-2-1}
---------------------------------------------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A%222.1+EA2%22)

-   Bug fix: [\#6483368 javax.xml.bind.Binder.marshal() doesn\'t throw
    expected MarshalException](http://find.me/id/6483368)

-   Bug fix: [\#6483953 javax.xml.bind.Binder.getJAXBNode(null) doesn\'t
    throw documented exception](http://find.me/id/6483953)

-   More bug fixes

Notable Changes between 2.0.2 to 2.0.3 {#a-2-0-3}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.0.2)

-   JavaSE 6 release is expected to include this version of the JAXB RI
    (certainly as of build 102.)

Notable Changes between 2.0.1 to 2.0.2 {#a-2-0-2}
--------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.0.1)

-   Bug fix: [\#6372392 Unmarshaller should report validation error for
    elements with xsi:nil=\"true\" and
    content](http://find.me/id/6372392)

-   Bug fix: [\#6449776 ClassCastException in JAXB when using interfaces
    as parameters](http://find.me/id/6449776)

-   More bug fixes

Notable Changes between 2.0 to 2.0.1 {#a-2-0-1}
------------------------------------

-   [Fixes to
    bugs](https://github.com/eclipse-ee4j/jaxb-ri/issues?q=is%3Aissue+is%3Aclosed+milestone%3A2.0)

-   More bug fixes

-   The simpler and better binding mode is improved

-   JAXB unofficial user\'s guide is available now, though it\'s still a
    work in progress

Notable Changes between 2.0 Early Access 3 and 2.0 First Customer Ship (FCS) {#a-2-0}
----------------------------------------------------------------------------

-   Java to schema samples are added

-   Added \<xjc:javaType\> vendor customization

-   Added experimental \<xjc:simple\> vendor customization, which brings
    in a new simpler and better binding mode

-   The spec has renamed `AccessType` to`XmlAccessType`, and
    `@AccessorType` to`@XmlAccessorType`.

-   Various error handling improvements

-   Experimental canonicaliztion support is added.

-   The \'-b\' option can now take a directory and recursively search
    for all \"\*.xjb\" files.

-   Fixed various issues regarding using JAXB from codef inside a
    restricted security sandbox.

-   Added more pluggability points for plugins to customize the code
    generation behavior.

-   Some of the code is split into a separate `istack-commons` project
    to promote more reuse among projects.

-   Made a few changes so that RetroTranslator can translate the JAXB RI
    (and its generated code) to run it on JDK 1.4 and earlier

-   Improved the quality of the generated code by removing unnecessary
    imports.

-   Other countless bug fixes

Notable Changes between 2.0 Early Access 2 and 2.0 Early Access 3 {#a-2-0ea3}
-----------------------------------------------------------------

-   Map property can be now correctly bound to XML Schema

-   Default marshaller error handling behavior became draconian
    (previously errors were ignored.)

-   \@link to a parameterized type is now correctly generated

-   started producing architecture document for those who want to build
    plugins or play with the RI internal.

-   XJC now uses the platform default proxy setting by default.

-   `@XmlAccessorOrder`, `@XmlSchemaType` and `@XmlInlineBinaryData` are
    implemented

-   `@XmlJavaTypeAdapter` on a class/package is implemented

-   Marshaller life-cycle events are implemented

-   Integration to FastInfoset is improved in terms of performance

-   XJC can generate `@Generated`

-   The unmarshaller is significantly rewritten for better performance

-   Added schemagen tool and its Ant task

-   Various improvements in error reporting during
    unmarshalling/marshalling

-   JAXB RI is now under CDDL

Notable Changes between 2.0 Early Access and 2.0 Early Access 2 {#a-2-0ea2}
---------------------------------------------------------------

-   The default for `@XmlAccessorType` was changed to PUBLIC\_MEMBER

-   Optimized binary data handling enabled by callbacks in package
    javax.xml.bind.attachment. Standards supported include MTOM/XOP and
    WS-I AP 1.0 ref:swaRef.

-   Unmarshal/marshal support of element defaulting

-   Improved the quality of the generated Java code

-   Fixed bugs in default value handling

-   Runtime performance improvements, memory usage improvements

-   Added support for \<xjc:superInterface\> vendor extension

-   Migrated source code to http://jaxb2-sources.dev.java.net

-   Published NetBeans project file for JAXB RI

-   Added more support to the schema generator: anonymous complex types,
    attribute refs, ID/IDREF, etc

-   Implemented `javax.xml.bind.Binder` support (not 100% done yet)

-   Implemented marshal-time validation

-   Improved xjc command line interface - better support for proxy
    options, more options for specifying schema files

-   Added schema-2-Java support for simple type substitution

-   Added support for the new
    `<jaxb:globalBindings localScoping="nested" | "toplevel">`
    customization which helps control deeply nested classes

-   Made the default `ValidationEventHandler` more forgiving in 2.0 than
    it was in 1.0 (The class still behaves the same as it did when used
    by a 1.0 app)

-   Added wildcard support for DTD

-   Numerous other small changes and bugfixes\....

Notable Changes between 1.0.x FCS and 2.0 Early Access {#a-2-0ea}
------------------------------------------------------

-   Support for 100% W3C XML Schema (not all in EA, but planned for FCS)

-   Support for binding Java to XML

-   Addition of `javax.xml.bind.annotation` package for controling the
    binding from Java to XML

-   Significant reduction in the number of generated schema-derived
    classes

    -   Per complex type definition, generate one value class instead of
        an interface and implementation class.

    -   Per global element declaration, generate a factory method
        instead of generating a schema-derived interface and
        implementation class.

-   Replaced the validation capabilities in 1.0 with JAXP 1.3 validation
    API\'s

-   Smaller runtime libraries

-   Numerous other changes\...
