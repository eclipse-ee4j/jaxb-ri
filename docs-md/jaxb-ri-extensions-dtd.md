DTD {#jaxb-ri-extensions-dtd}
===

DTD {#section-117866877581027}
---

The JAXB RI is shipped with experimental DTD support, which lets you
compile XML DTDs.

To compile a DTD `test.dtd`, run the XJC binding compiler as follows:

::: {.informalexample}
``` {.cli}
$ xjc.sh -dtd test.dtd
```
:::

All the other command-line options of the XJC binding compiler can be
applied. Similarly, the `xjc` [ant](http://jakarta.apache.org/ant/) task
supports DTD. The generated code will be no different from what is
generated from W3C XML Schema. You\'ll use the same JAXB API to access
the generated code, and it is portable in the sense that it will run on
any JAXB 2.0 implementation.

### Customization {#section-249814783079567}

The customization syntax for DTD is roughly based on the ver.0.21
working draft of the JAXB specification, which is available at
[xml.coverpages.org](http://xml.coverpages.org/jaxb0530spec.pdf). The
deviations from this document are:

-   The `whitespace` attribute of the `conversion` element takes \"
    `preserve`\", \" `replace`\", and \" `collapse`\" instead of \"
    `preserve`\",\" `normalize`\", and \" `collapse`\" as specified in
    the document.

-   The `interface` customization just generates marker interfaces with
    no method.
