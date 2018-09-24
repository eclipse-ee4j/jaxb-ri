<%--

    Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

--%>

<%@ taglib prefix="xjc" uri="http://java.sun.com/xml/ns/jaxb/xjc/ontheweb" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
	<title>Javadoc failure</title>
</head>
<body>
<xjc:header title="Unable to generate javadoc">
</xjc:header>
<p>
	See the following error messages for details:
</p>
<pre style="background: rgb(240,240,255)"><xmp>
<%= (String)request.getAttribute("msg") %>
</xmp></pre>
</body>
</html>
