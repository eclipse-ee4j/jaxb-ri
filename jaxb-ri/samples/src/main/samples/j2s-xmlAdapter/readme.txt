-- Adapters for customized marshaling/unmarshaling

-- Synopsis
    This sample demonstrates the use of interface XmlAdapter 
    and annotation @XmlJavaTypeAdapter to provided a custom
    mapping of XML content into and out of a HashMap (field)
    that uses an 'int' as the key and a 'string' as the value.



-- Details
Interface XmlAdapter and annotation @XmlJavaTypeAdapter are provided
for special processing of datatypes during the unmarshalling/marshalling
process.  There are a variety of XML datatypes for which the representation 
does not map easily into Java (e.g.,xs:DateTime and xs:Duration), and Java 
types which do not map conveniently into XML representations for example
implementations of java.util.Collection (e.g., List) and java.util.Map 
(e.g., HashMap) or for non-JavaBean classes.  It is for these cases that 
XmlAdapter and @XmlJavaTypeAdapter are provided.  They provide a portable 
mechanism for reading/writing XML content into and out of Java applications.


The XmlAdapter interface defines the methods for data reading/writing.  

/*
 *  ValueType - Java class that provides an XML representation
 *              of the data. It is the object that is used for 
 *              marshalling and unmarshalling. 
 *
 *  BoundType - Java class that is used to process XML content.
 */
public abstract class XmlAdapter<ValueType,BoundType> { 
    // Do-nothing constructor for the derived classes. 
    protected XmlAdapter() {} 

    // Convert a value type to a bound type. 
    public abstract BoundType unmarshal(ValueType v);
 
    // Convert a bound type to a value type. 
    public abstract ValueType marshal(BoundType v); 
}


Annotation @XmlJavaTypeAdapter is used to associate a particular
XmlAdapter implementation with a Target type, PACKAGE,FIELD,
METHOD,TYPE,or PARAMETER.


-- Example 
    This example demonstrates an XmlAdapter for mapping XML content
    into and out of a (custom) HashMap.  The HashMap object, basket
    in class KitchenWorldBasket, uses a key of type 'int' and a value 
    of type 'String'.  We want these datatypes to be reflected in the 
    XML content that is read and written. The XML content should
    look like this.

    <basket>
         <entry key="9027">glasstop stove in black</entry>
         <entry key="288">wooden spoon</entry>
   </basket>


    The default schema generated for Java type HashMap does not reflect 
    the desired format.  
  
     <xs:element name="basket">
        <xs:complexType>
          <xs:sequence>
            <xs:element name="entry" minOccurs="0" maxOccurs="unbounded">
              <xs:complexType>
                <xs:sequence>
                  <xs:element name="key" minOccurs="0" type="xs:anyType"/>
                  <xs:element name="value" minOccurs="0" type="xs:anyType"/>
                </xs:sequence>
              </xs:complexType>
            </xs:element>
          </xs:sequence>
        </xs:complexType>
      </xs:element>

    In the default HashMap schema, key and value are both elements and are 
    of datatype anyType.  The XML content would look like this.

    <basket>
         <entry>
            <key>9027</>
            <value>glasstop stove in black</>
         </entry>
         <entry>
            <key>288</>
            <value>wooden spoon</>
        </entry>
   </basket>

   To resolve this issue two Java classes were written that reflect the 
   needed schema format for unmarshalling/marshalling the content, 
   PurchaseList and PartEntry.  Here is the XML schema generated for these 
   classes.

    <xs:complexType name="PurchaseListType">
        <xs:sequence>
            <xs:element name="entry" type="partEntry" nillable="true" maxOccurs="unbounded"
                minOccurs="0"/>
            </xs:sequence>
        </xs:complexType>

    <xs:complexType name="partEntry">
        <xs:simpleContent>
            <xs:extension base="xs:string">
                <xs:attribute name="key" type="xs:int" use="required"/>
            </xs:extension>
        </xs:simpleContent>
    </xs:complexType>


    Class AdapterPurchaseListToHashMap implements the XmlAdapter interface.  
    In class KitchenWorldBasket @XmlJavaTypeAdapter is to used to pair 
    AdapterPurchaseListToHashMap with field HashMap basket.  This pairing will
    cause AdapterPurchaseListToHashMap's marshal/unmarshal method to be called
    for any corresponding marshal/unmarshal action on KitchenWorldBasket.

