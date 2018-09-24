This example illustrates java to schema databinding. 
It demonstrates marshalling and unmarshalling of JAXB annotated
classes. Additionally, it demonstrates how to enable JAXP 1.3
validation at unmarshal time using a schema file generated
from the JAXB mapped classes.

The schema file, bc.xsd, was generated with the following commands:
% schemagen src/cardfile/*.java 
% cp schema1.xsd bc.xsd

