<?xml version="1.0" encoding="utf-8"?>
<!--

    Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

    <xsl:import href="common.xsl"/>
    <xsl:import href="foProcessing.xsl"/>

    <xsl:attribute-set name="formal.object.properties">
        <xsl:attribute name="keep-together.within-column">auto</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="monospace.verbatim.properties">
        <xsl:attribute name="font-size">
            <xsl:choose>
                <xsl:when test="processing-instruction('db-font-size')">
                    <xsl:value-of select="processing-instruction('db-font-size')"/>
                </xsl:when>
                <xsl:otherwise>90%</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
        <xsl:attribute name="hyphenation-character">\</xsl:attribute>
    </xsl:attribute-set>
    
</xsl:stylesheet>
