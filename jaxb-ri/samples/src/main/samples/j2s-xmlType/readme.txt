-- @XmlType Usage

-- Synopsis
    This sample demonstrates the use of annotation @XmlType


-- Details

Annotation @XmlType maps a class or an enum type to a XML Schema type.

A class must have either a public zero arg constructor or a static zero arg 
factory method in order to be mapped by this annotation.  One of these methods
is used during unmarshalling to create an instance of the class. The factory
method may reside within in a factory class or the existing class.  There is
an order of presedence as to which method is used for unmarshalling.  

	* If a factory class is identified in the annotation, a corresponding
	  factory method in that class must also be identified and that method
	  will be used.

	* If a factory method is identified in the annotation but no
	  factory class is identified then the factory method must reside
	  in the current class.  The factory method is used even if there
	  is a public zero arg constructor method present.

	* If no factory method is identified in the annotation then the
	  class must contain a public zero arg constructor method.


-- Example

	In this example a factory class provides zero arg factory methods
	for several classes.  The XmlType annotation on class OrderContext
	references the factory class.  The unmarshaller will use the 
	identified factroy method in this class.

	public class OrderFormsFactory {
		public OrderContext newOrderInstance() { 
	                return new OrderContext();
	        }
	
		public PurchaseOrderType newPurchaseOrderType() {
			return new newPurchaseOrderType();
		} 
	
	}
	
	@XmlType(name="oContext", factoryClass="OrderFormsFactory", 
	factoryMethod="newOrderInstance")
	public class OrderContext {
	
	        public OrderContext(){ ..... }
	    }
	
	

	In this example a factory method is defined in a class which
	also contains a standard class constructure.  Because the
	factoryMethod value is define and no factoryClass is defined
	The factory method newOrderInstance is used during unmarshalling.
	
	@XmlType(name="oContext", factoryMethod="newOrderInstance")
	public class OrderContext {
	
		public OrderContext(){ ..... }
	
		public OrderContext newOrderInstance() { 
			return new OrderContext();
		}
	   
	    }
	
	
	
