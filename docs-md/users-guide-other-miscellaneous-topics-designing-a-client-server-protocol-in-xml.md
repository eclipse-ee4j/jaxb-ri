Designing a client/server protocol in XML {#other-miscellaneous-topics-designing-a-client-server-protocol-in-xml}
=========================================

Occasionally, people try to define a custom protocol that allows
multiple XML requests/responses to be sent over a single transport
channel. This section discusses the non-trivial interaction between XML
and sockets, and how you can design a protocol correctly.

XML1.0 requires a conforming parser to read the entire data till end of
the stream (because a parser needs to handle documents like
`<root/><!-- post root comment -->`). As a result, a naive attempt to
keep one `OutputStream` open and marshal objects multiple times fails.

``` {.xml}
<conversation>
  <!-- message 1 -->
  <message>
    ...
  </message>

  <!-- message 2 -->
  <message>
    ...
  </message>

  ...
</conversation>
```

The `<conversation>` start tag is sent immediately after the socket is
opened. This works as a container to send multiple \"messages\", and
this is also an excellent opportunity to do the hand-shaking (e.g.,
`protocol-version='1.0'` attribute.) Once the `<conversation>` tag is
written, multiple messages can be marshalled as a tree into the channel,
possibly with a large time lag in between. You can use the JAXB
marshaller to produce such message. When the sender wants to disconnect
the channel, it can do so by sending the `</conversation>` end tag,
followed by the socket disconnection.

Of course, you can choose any tag names freely, and each message can
have different tag names.

The receiver would use the StAX API and use `XMLStreamReader` to read
this stream. You\'d have to use this to process the first
`<conversation>` start tag. After that, every time you call a JAXB
unmarshaller, you\'ll get the next message.

For the concrete code, see the `xml-channel` example in the JAXB RI
distribution.
