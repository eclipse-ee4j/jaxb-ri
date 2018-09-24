This example demonstrates how one can use one communication channel
(such as a socket) to send multiple XML messages, and how it can be
combined with JAXB.

XML1.0 requires a conforming parser to read the entire data till end
of the stream (because a parser needs to handle documents like
<root/><!-- post root comment -->). As a result, a naive attempt to
keep one OutputStream open and marshal objects multiple times fails.

This example shows you how to work around this limitation. In this
example, the data on the wire will look like the following:

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

The <conversation> start tag is sent immediately after the socket
is opened. This works as a container to send multiple "messages",
and this is also an excellent opportunity to do the hand-shaking
(e.g., protocol-version='1.0' attribute.)

Once the <conversation> tag is written, multiple "documents" can
be marshalled as a tree into the channel, possibility with a large
time lag in between. In this example, each message is written by
one marshaller invocation.

When the client wants to disconnect the channel, it can do so by
sending the </conversation> end tag, followed by the socket
disconnection. 