Dealing with large documents {#unmarshalling-dealing-with-large-documents}
============================

JAXB API is designed to make it easy to read the whole XML document into
a single tree of JAXB objects. This is the typical use case, but in some
situations this is not desirable. Perhaps:

1.  A document is huge and therefore the whole may not fit the memory.

2.  A document is a live stream of XML (such as
    [XMPP](http://www.xmpp.org/)) and therefore you can\'t wait for the
    EOF.

3.  You only need to databind the portion of a document and would like
    to process the rest in other XML APIs.

This section discusses several advanced techniques to deal with these
situations.

Processing a document by chunk {#Processing_a_document_by_chunk}
------------------------------

When a document is large, it\'s usually because there\'s repetitive
parts in it. Perhaps it\'s a purchase order with a large list of line
items, or perhaps it\'s an XML log file with large number of log
entries.

This kind of XML is suitable for chunk-processing; the main idea is to
use the StAX API, run a loop, and unmarshal individual chunks
separately. Your program acts on a single chunk, and then throws it
away. In this way, you\'ll be only keeping at most one chunk in memory,
which allows you to process large documents.

See the streaming-unmarshalling example and the partial-unmarshalling
example in the JAXB RI distribution for more about how to do this. The
streaming-unmarshalling example has an advantage that it can handle
chunks at arbitrary nest level, yet it requires you to deal with the
push model \-\-- JAXB unmarshaller will \"`push`\" new chunk to you and
you\'ll need to process them right there.

In contrast, the partial-unmarshalling example works in a pull model
(which usually makes the processing easier), but this approach has some
limitations in databinding portions other than the repeated part.

Processing a live stream of XML {#Processing_a_live_stream_of_XML}
-------------------------------

The techniques discussed above can be used to handle this case as well,
since they let you unmarshal chunks one by one. See the xml-channel
example in the JAXB RI distribution for more about how to do this.

Creating virtual infosets {#Creating_virtual_infosets}
-------------------------

For further advanced cases, one could always run a streaming infoset
conversion outside JAXB API and basically curve just the portion of the
infoset you want to data-bind, and feed it as a complete infoset into
JAXB API. JAXB API accepts XML infoset in many different forms (DOM,
SAX, StAX), so there\'s a fair amount of flexibility in choosing the
right trade off between the development effort in doing this and the
runtime performance.

For more about this, refer to the respective XML infoset API.
