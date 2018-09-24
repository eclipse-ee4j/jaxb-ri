-- @XmlAttribute Usage

-- Synopsis
    This sample demonstrates the use of annotation @XmlAttribute
    to define a property or field to be treated as an XML attribute.


-- Details

Annotation @XmlAttribute maps a field or JavaBean property
to an XML attribute.  The following rules are imposed.

    * A static final field is mapped to a XML fixed attribute
    * When the field or property is a collection type, the items
      of the collection type must map to a schema simple type.
    * When the field or property is other than a collection type,
      the type must map to a schema simple type.

When following the JavaBean programming paradigm a propery is
defined by a 'get' and 'set' prefix on a field name.

    int zip;
    public int getZip(){return zip;}
    public void setZip(int z){zip=z;}

Within a bean class the user has the choice of setting the 
@XmlAttribute on one of three components, the field, the setter method, 
or the getter method. If @XmlAttribute is set on the field then the 
setter method will need to be renamed otherwise there will be a naming 
conflict at compile time.  If @XmlAttribute is set on one of the methods, 
then it must be set on either the setter or getter method but not both.


-- Example

The example shows @XmlAttribute used on a static final field, on a field 
rather than on one of the corresponding bean methods, on a bean property
(method), and on a field that is other than a collection type.  In class 
USAddress, fields, country and zip are tagged as attributes.  The setZip 
method was disabled to avoid the compile error.  Property state was
tagged as an attribute on the setter method.  The getter method could have
been used instead.  In class PurchaseOrderType, field cCardVendor is a
non-collection type.  It meets the requirment of being a simple type.  It
is an enum type. 
