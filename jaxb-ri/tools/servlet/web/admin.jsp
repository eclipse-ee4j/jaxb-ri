<%--

    Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

--%>

<%@ taglib prefix="xjc" uri="http://java.sun.com/xml/ns/jaxb/xjc/ontheweb" %>
<%@ page import = "com.sun.tools.xjc.servlet.*"%>

<html>
<head>
	<title>XJC servlet admin</title>
	<meta http-equiv="Content-type" content="text/html; charset=iso-8859-1"/>
</head>
<body>
	<xjc:header title="XJC servlet admin">
	</xjc:header>

	<form action="admin" method="POST">
		Admin password:<br>
		<input type="password" size="60" name="password"><br>
		<input type="checkbox" name="canUseDisk" value="true" <%= Mode.canUseDisk?"checked":"" %> >
		Enable features that use disk:<br>
		SMTP server name:<br>
		<input type="text" size="60" name="mailServer" value="<%= Mode.mailServer %>"><br>
		Home e-mail address:<br>
		<input type="text" size="60" name="homeAddress" value="<%= Mode.homeAddress %>"><br>
		<input type="submit" value="submit">
	</form>
</body>
</html>
