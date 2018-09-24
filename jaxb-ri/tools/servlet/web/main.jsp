<%--

    Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

--%>

<%@ taglib prefix="xjc" uri="http://java.sun.com/xml/ns/jaxb/xjc/ontheweb" %>
<%@ taglib prefix="com" uri="http://java.sun.com/xml/ns/jaxb/webapp/commons" %>
<%
	// referer check
	
	String head = request.getRequestURL().toString();
	head = head.substring(0,head.length()-9 /*"/main.jsp"*/);
	
	String referer = request.getHeader("Referer");
	if( referer==null )		referer = "";
	if( !referer.startsWith(head) ) {
		response.sendRedirect("index.jsp");
	}
%>
<html>
<head>
	<title>XJC servlet</title>
	<meta http-equiv="Content-type" content="text/html; charset=iso-8859-1"/>
	<style>
		em {
			color: red;
			font-weight: bold;
		}
	</style>
</head>
<body>
<xjc:header title="JAXB on the Web!">
	<div align=right style="font-size:smaller">
	Build: <xjc:version />
	</div>
</xjc:header>

<h2>Compile Your Schema into Java</h2>

<com:tabSheet shadowColor="#e0e0ff">
  <com:tabPage name="upload from your disk" default="true">
    <!-- upload -->
		<form action="compiler-entry"
		      enctype="multipart/form-data"
		      method="POST">
			<table><tr>
				<td align=right>
					schema file:
				</td><td>
					<input type="file" size="60" name="schema">
				</td>
			</tr><tr>
				<td align=right>
					ext. binding:
				</td><td>
					<input type="file" size="60" name="binding">
				</td>
			</tr></table>
			<%@ include file="parameters.jsp"%>
			<input type="submit" value="compile">
		</form>
  </com:tabPage>
  <com:tabPage name="use publicly available schema">
    <!-- from URL -->
		<form action="compiler-entry"
		      enctype="multipart/form-data"
		      method="POST">
			
			<table><tr>
				<td align=right>
					URL to schema:
				</td><td>
					<input type="text" size="60" name="schemaURL">
				</td>
			</tr><tr>
				<td align=right>
					URL to ext. binding:
				</td><td>
					<input type="text" size="60" name="bindingURL">
				</td>
			</tr></table>
			<%@ include file="parameters.jsp"%>
			<input type="submit" value="compile">
		</form>
  </com:tabPage>
  <com:tabPage name="type in a schema">
    <!-- interactive -->
		<form action="compiler-entry"
		      enctype="multipart/form-data"
		      method="POST">
			Schema:<br>
			<textarea rows=10 cols=80 name=schemaLiteral>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
</xs:schema>
</textarea><br>
			External Binding:<br>
			<textarea rows=10 cols=80 name=bindingLiteral></textarea><br>
			<%@ include file="parameters.jsp"%>
			<input type="submit" value="compile">
		</form>
  </com:tabPage>
  <com:tabPage name="sample PO schema">
    <!-- PO schema -->
		<form action="compiler-entry"
		      enctype="multipart/form-data"
		      method="POST">
			Schema:<br>
			<textarea rows=10 cols=80 name=schemaLiteral>
<%@ include file="po.xsd" %>
</textarea><br>
			External Binding:<br>
			<textarea rows=10 cols=80 name=bindingLiteral>
<%@ include file="po.xjb" %>
</textarea><br>
			<%@ include file="parameters.jsp"%>
			<input type="submit" value="compile">
		</form>
  </com:tabPage>
</com:tabSheet>


<xjc:if mode="StandAlone">
	<p><em>
		We don't recommend you to use this for a product. This technology is an early access release to JAXB RI. It doesn't go through the rigorous testing the formal releases go through, and therefore it may contain bugs. We may change the behavior of the compiler and that can break your code.
	</em></p>
</xjc:if>

<h2>Useful Links</h2>
<dl>
<xjc:if mode="StandAlone">
	<dt><a href="http://java.sun.com/webservices/downloads/webservicespack.html">
		Java Web Services Developer Pack
	</a>
	<dd>
		Want to run the compiler locally?
		Download Java Web Services Developer Pack today.
</xjc:if>
	
	<dt><a href="http://archives.java.sun.com/jaxb-interest.html">
		jaxb-interest mailing list
	</a>
	<dd>
		Got questions? Join the mailing list for JAXB and share your experiences.
	
	<dt><a href="http://java.sun.com/webapps/bugreport/">
		BugParade
	</a>
	<dd>
		Found a bug? Report a bug through BugParade and get it fixed.
</dl>
</body>
</html>
