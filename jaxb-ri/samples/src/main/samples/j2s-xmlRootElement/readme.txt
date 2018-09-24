-- @XmlRootElement Usage

-- Synopsis
    This sample demonstrates the use of annotation @XmlRootElement
    to define an XML element name for the XML schema type of the 
    corresponding class. 


-- Details

Annotation @XmlRootElement maps a class or an enum type to 
an XML element.  At least one element definition is needed
per (top level) Java type used for unmarshalling/marshaling.
Without it there is no starting location for XML content
processing.

Annotation @XmlRootElement uses the class name as the default element 
name.  The user can change the default name with the annotation attribute 
name.  The specified name will be used as the element name and the type 
name. It is common schema practice for the element name and type name to 
be different.  Annotation @XmlType can be used to set the element type name.
 
The namespace attribute of @XmlRootElement enables the defining 
of a namespace for the element. 
