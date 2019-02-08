Unmarshalling is not working! Help! {#unmarshalling-unmarshalling-is-not-working-help}
===================================

There are a few common causes for this problem. These causes often
exhibit similar symptoms:

1.  Instance documents are invalid

2.  `JAXBContext` is not created correctly.

Make sure your instance document is valid {#Make_sure_your_instance_document_is_valid}
-----------------------------------------

First, use an independent schema validator to check if your document is
really valid with respect to the schema you compiled. When the root
element of a document is invalid, then the unmarshaller will issue
\"unexpected element\" errors. When a portion of a document is invalid,
JAXB skips that portion, so the end result is that the unmarshalling
returns normally, yet you notice that a part of the content tree is
missing. This is often the desirable behavior, but it sometimes ends up
masking a problem.

Also, try to install `ValidationEventHandler` on the unmarshaller. When
a portion of a document is skipped, the unmarshaller notifies a
`ValidationEventHandler`, so it allows you to see what\'s going on.

``` {.java}
Unmarshaller u = ...;
// this implementation is a part of the API and convenient for trouble-shooting,
// as it prints out errors to System.out
u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

u.unmarshal(new File("foo.xml"));
```

Also consider installing a `Schema` object to the unmarshaller, so that
the unmarshaller performs a schema validation while unmarshalling.
Earlier I suggested that you try an independent schema validator, but
for various reasons (not all tools are reliable, you might have made an
error and used a different schema/instance), using validating
unmarshalling is a better way to guarantee the validity of your instance
document being unmarshalled. Please follow the [JAXP
tutorial](http://java.sun.com/j2ee/1.4/docs/tutorial/doc/JAXPIntro.html#wp65584)
for more about how to construct a `Schema` object from your schema.

If you are unmarshalling from XML parser APIs (such as DOM, SAX, StAX),
then also make sure that the parser/DOM is configured with the namespace
enabled.

Check if your JAXBContext is correct {#Check_if_your_JAXBContext_is_correct}
------------------------------------

(TODO: This also applies to the marshaller. Think about moving it.)

The other possibility is that `JAXBContext` is not set up correctly.
`JAXBContext` \"knows\" a set of classes, and if it doesn\'t know a
class that it\'s supposed to know, then the unmarshaller may fail to
perform as you expected.

To verify that you created `JAXBContext` correctly, call
`JAXBContext.toString()`. It will output the list of classes it knows.
If a class is not in this list, the unmarshaller will never return an
instance of that class. Make you see all the classes you expect to be
returned from the unmarshaller in the list. When dealing with a large
schema that spans across a large number of classes and packages, this is
one possible cause of a problem.

If you noticed that a class is missing, explicitly specify that to
`JAXBContext.newInstance`. If you are binding classes that are generated
from XJC, then the easiest way to include all the classes is to specify
the generated `ObjectFactory` class(es).
