This example illustrates the javax.xml.bind.Binder use cases mentioned in Section 4.8.1 of JAXB 2.0 Specification.

- Updateable Partial Binding

The application receives an XML document that follows a later version of the schema than the application is aware 
of. The parts of the schema that the application needs to read and/or modify have not changed. Thus, the 
document can be read into an infoset preserving representation, such as DOM, only bind the part of the 
document that it does still have the correct schema for into the JAXB Java representation of the fragment of the 
document using Binder.unmarshal from the DOM to the JAXB view. Modify the partial Java representation of the 
document and then synchronize the modified parts of the Java representation back to the DOM view using 
Binder.updateXML method. 

In this sample's xml document, elements and attributes are
added to the shipTo/billTo elements that are not modified by the application. 
The new children elements and attributes introduced by schema evolution are 
preserved by updateable partial binding. Also note that XML comments that are 
typically lost by unmarshal/marshal roundtrip are not lost by using this technique.

- XPATH navigation

Given that binder maintains a relationship between XML infoset view of document and JAXB representation, one can 
use JAXP 1.3 XPATH on the XML infoset view and use the binder's associative mapping to get from the infoset node 
to JAXB representation. XPath is used in this example to identify the DOM nodes to bind to JAXB objects.


Note that JAXP 1.3 DOM L3 Load/Save  and XPath usage were adapted from JAXP 1.3 Samples.



