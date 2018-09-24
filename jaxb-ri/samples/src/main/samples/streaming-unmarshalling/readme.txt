This example is similar in purpose to the partial-unmarshalling sample: it allows
access to single purchase order objects before the whole input document is
unmarshalled. This reduces the memory consumption and speeds up the turn around
time.

While the partial-unmarshalling sample feeds the input document piecemeal to the
unmarshaller this sample works notification style. Instead of storing all purchase
orders in the parent object, a listener is called for each child. The listener
logic and installation can be found in the classes primer.PurchaseOrders and Main.

This approach is in some ways more flexible than StAX, as the boundary of the
chunking does not have to necessarily correspond with the schema element definition
boundary. The downside of this is that it requires some manual modifications to
the generated Java source files.

(Contributed by Matthias Ernst)