-- Ordering of properties/fields

-- Synopsis
    This sample demonstrates the use of annotations @XmlAccessorOrder
    and @XmlType.propOrder to dictate the order in which XML content
    is marshalled/unmarshaled by a Java type.



-- Details

Java to Schema maps a JavaBean's properties and fields
to an XML Schema type. The class elements are mapped 
to either an XML Schema complex type or an XML Schema simple 
type. The default element order for a generated schema type 
is currently unspecified, because Java reflection does not 
impose a return order.  Lack of reliable element ordering 
impacts application portability.  Two annotations for defining 
schema element ordering are provided for applications that 
wish to remain portable across JAXB Providers, @XmlAccessorOrder 
and @XmlType.propOrder. 


-- @XmlAccessorOrder

@XmlAccessorOrder annotation imposes one of two element ordering 
algorithms, XmlAccessOrder.UNDEFINED or XmlAccessOrder.ALPHABETICAL.  
XmlAccessOrder.UNDEFINED is the default setting.  The order is dependent 
on the system's reflection implementation.  XmlAccessOrder.ALPHABETICAL 
orders the elements in lexicographic order as determined by 
java.lang.String.CompareTo(String anotherString).

@XmlAccessorOrder can be defined for annotation type ElementType.PACKAGE
or on a class object.  When @XmlAccessorOrder is defined on a package, the 
scope of the formatting rule is active for every class in the package.  
When defined on a class the rule is active on the contents of that class. 

There can be multiple @XmlAccessorOrder annotations within a package.  
The order of precedence is the inner most (class) annotation 
takes precedence over the outter annotation.  For example if 
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL) is defined on a package 
and @XmlAccessorOrder(XmlAccessOrder.UNDEFINED) is defined on a class
in that package the contents of the generated schema type for the class 
would be in an unspecified order and the contents of the generated schema 
type for evey other class in the package would be alphabetical order.  



-- @XmlType.propOrder

@XmlType can be defined for a class.  The annotation element propOrder() 
in @XmlType allows the specification of the content order in the generated 
schema type.  When @XmlType.propOrder is used on a class to specify content 
order, all public properties and public fields in the class must be specified 
in the parameter list. Any public property or field not desired in the parameter 
list must be annotated with @XmlAttribute or @XmlTransient.

The default content order for @XmlType.propOrder is {} or {""}; not active.  
In such cases the active @XmlAccessorOrder annotation takes precedence.  When 
class content order is specified by @XmlType.propOrder, it takes precedence 
over any active @XmlAccessorOrder annotation on the class or package.  If
@XmlAccessorOrder and @XmlType.propOrder(A, B, ...) annotations are specified 
on a class, the propOrder always takes precedence no matter the order of 
the annontation statements.  For example in CODE A below the @XmlAccessorOrder
annotation preceeds @XmlType.propOrder and in CODE B @XmlType.propOrder preceeds 
@XmlAccessorOrder. In both scenarios propOrder takes precedence and the identical
schema content is generated. 


    CODE A:
        @XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
        @XmlType(propOrder={"name", "city"})
        public class USAddress {
                :
            public String getCity() {return city;}
            public void setCity(String city) {this.city = city;}

            public String getName() {return name;}
            public void setName(String name) {this.name = name;}
                :
        }

 
    CODE B:
        @XmlType(propOrder={"name", "city"})
        @XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
        public class USAddress {
                :
            public String getCity() {return city;}
            public void setCity(String city) {this.city = city;}

            public String getName() {return name;}
            public void setName(String name) {this.name = name;}
                :
        }


    SCHEMA GENERATE
        <xs:complexType name="usAddress">
           <xs:sequence>
              <xs:element name="name" type="xs:string" minOccurs="0"/>
              <xs:element name="city" type="xs:string" minOccurs="0"/>
           </xs:sequence>
        </xs:complexType>


-- Code example

The purchase order code example demonstrates the affects of schema content 
ordering using @XmlAccessorOrder annotation at the package and class level, 
and @XmlType.propOrder on a class.

Class package-info.java defines @XmlAccessorOrder to be ALPHABETICAL for
the package.  The public fields shipTo and billTo in class PurchaseOrderType
will be affected in the generated schema content order by this rule.  Class 
USAddress defines @XmlType.propOrder on the class.  This demonstates user 
defined property order superseding ALPHABETICAL order in the generated schema. 

The generated schema file can be found in directory schemas. 
