
This sample illustrates how to resolve naming collisions for Schema
to Java databinding. JAXB schema annotations are used to
customize the XSD to Java databinding to resolve the naming collisions.

When this example is compiled without the binding file, 
the following errors are generated. 

      [xjc] [ERROR] Property name "Class" is reserved by java.lang.Object.
      [xjc]   line 19 of file:fix-collides/example
.xsd
      [xjc] [ERROR] Property "Zip" is already defined.
      [xjc]   line 20 of file:fix-collides/example
.xsd
      [xjc] [ERROR] The following location is relevant to the above error
      [xjc]   line 22 of file:fix-collides/example
.xsd

The ant task for this sample includes the binding file, binding.xjb,
that contains JAXB schema annotations that resolve the name
collisions that occur in this sample. The naming collisions occur
as part of databinding since XML Schema defines 6 unique namespaces
and these are being mapped into one unique namespace within Java
program elements. The JAXB specification details additional 
reasons for naming collisions in Appendix Section D.2 "Collisions and 
Conflicts".

