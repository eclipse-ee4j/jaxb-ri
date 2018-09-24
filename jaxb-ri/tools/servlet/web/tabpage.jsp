<%--

    Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

--%>

<%@ taglib prefix="xjc" uri="http://java.sun.com/xml/ns/jaxb/xjc/ontheweb" %>

<html>
<head>
	<title>test</title>
	<meta http-equiv="Content-type" content="text/html; charset=iso-8859-1"/>
</head>
<body>
<xjc:header title="XJC Servlet">
	tab page test
</xjc:header>

<xjc:tabSheet shadowColor="#e0e0ff">
  <xjc:tabPage name="page1" default="true">
    page 1
  </xjc:tabPage>
  <xjc:tabPage name="page2">
    page 2
  </xjc:tabPage>
</xjc:tabSheet>
</body>
</html>
