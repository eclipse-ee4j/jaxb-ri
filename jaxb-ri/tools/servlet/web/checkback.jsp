<%--

    Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

--%>

<%@ taglib prefix="xjc" uri="http://java.sun.com/xml/ns/jaxb/xjc/ontheweb" %>
<%
	String comebackto = (String)request.getAttribute("comebackto");
	String title = (String)request.getAttribute("title");
	String message = (String)request.getAttribute("message");
%>
<html>
<head>
	<title><%= title %></title>
	<meta http-equiv="Content-type" content="text/html; charset=iso-8859-1"/>
	<META HTTP-EQUIV=Refresh CONTENT="3; URL=<%=comebackto%>">
	<script>
	  // this will make the back button work correctly.
	  // if the user disables JavaScript, the meta tag will refresh the page
		setTimeout('self.location.replace("<%=comebackto%>")', 2800);
	</script>
</head>
<body>
<xjc:header title="<%= title %>">
	<p>
		<%= message %>
	</p>
</xjc:header>
</body>
</html>
