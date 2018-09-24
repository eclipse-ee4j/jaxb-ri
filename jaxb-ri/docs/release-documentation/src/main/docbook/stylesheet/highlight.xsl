<?xml version='1.0'?>
<!--

    Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xslthl="http://xslthl.sf.net"
                exclude-result-prefixes="xslthl"
                version='1.0'>

    <xsl:import href="urn:docbkx:stylesheet/highlight.xsl"/>

    <xsl:template match='xslthl:keyword' mode="xslthl">
        <span class="ReservedWord"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:comment' mode="xslthl">
        <span class="Comment"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:oneline-comment' mode="xslthl">
        <span class="Comment"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:multiline-comment' mode="xslthl">
        <span class="DocComment"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:tag' mode="xslthl">
        <span class="ReservedWord"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:attribute' mode="xslthl">
        <span class="Identifier"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:value' mode="xslthl">
        <span class="String"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:string' mode="xslthl">
        <span class="String"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:annotation' mode="xslthl">
        <span class="Annotation"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:number' mode="xslthl">
        <span class="Numeric"><xsl:value-of select='.'/></span>
    </xsl:template>

    <xsl:template match='xslthl:doccomment|xslthl:doctype' mode="xslthl">
        <span class="Comment"><xsl:apply-templates mode="xslthl"/></span>
    </xsl:template>

</xsl:stylesheet>
