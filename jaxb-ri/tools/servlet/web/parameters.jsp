<%--

    Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

--%>

<div style="padding: 0.5em">
	<div>
		<input type="radio" name="language" value="xsd" checked="true">XML Schema
		<input type="radio" name="language" value="rng">RELAX NG
		<input type="radio" name="language" value="dtd">DTD
	</div>
	<div style="font-size:smaller; padding-left: 3em">
		* RELAX NG and DTD supports are experimental and unsupported.
	</div>
	<div style="padding-top: 0.5em">
		<input type="checkbox" name="extension">Extension mode (the -extension switch)
	</div>
	<div style="padding-top: 0.5em">
		Target package: <input type="text" name="package" value="generated" size="32">
	</div>
<%-- xjc:if mode="StandAlone">
	<div style="padding-top: 0.5em">
		<input type="checkbox" name="contrib" checked="true">Allow Sun to use the schema for testing.
	</div>
</xjc:if --%>
</div>
