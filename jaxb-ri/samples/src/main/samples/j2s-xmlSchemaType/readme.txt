-- @XmlSchemaType Usage

-- Synopsis
    This sample demonstrates the use of annotation @XmlSchemaType
    to customize the mapping of a property or field to an XML built-in
    type.


-- Details
@XmlSchemaType can be used to map a Java type to one of the XML 
built-in types.  This annotation is most useful in mapping a Java 
type to one of the nine date/time primitive datatypes.

When @XmlSchemaType is defined at the package level the identification
requires both the XML built-in type name and the corresponding Java
type class.  A @XmlSchemaType definition on a field or property takes
precedence over a package definition.
 
-- Example
The example shows @XmlSchemaType being used at the package level,
on a field and on a property.  File TrackingOrder has two fields
orderDate and deliveryDate which are defined to be of type 
XMLGregorianCalendar. The generated schema will define these elements
to be of XML build-in type gMonthDay.  This relationship was defined 
on the package in file package-info.java.  Field shipDate in file 
TrackingOrder is also defined to be of type XMLGregorianCalendar,
however the @XmlSchemaType statements overrides the package definition
and specifies the field to be of type date.  Property method
getTrackingDuration defines the schema element to be defined as 
primitive type duration and not Java type String.


