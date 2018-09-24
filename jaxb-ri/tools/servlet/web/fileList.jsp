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
	<title>file list</title>
	<meta http-equiv="Content-type" content="text/html; charset=iso-8859-1"/>
	<base target="main">
</head>
<body>
	<xjc:zipForEach>
		<a href="java/<xjc:zipFileName />">
			<xjc:zipFileName />
		</a><br>
	</xjc:zipForEach>
</body>
</html>
