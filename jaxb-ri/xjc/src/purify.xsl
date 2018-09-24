<?xml version="1.0"?>
<!--

    Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:rng="http://relaxng.org/ns/structure/1.0"
	xmlns:p="post-processor-to-build-schema-for-validation">
	
	<xsl:output encoding="UTF-8" />
	<xsl:strip-space elements="rng:*"/>
	<xsl:preserve-space elements="rng:value rng:param"/>
	
	<xsl:template match="/">
		<xsl:comment>THIS IS A GENERATED FILE. DO NOT MODIFY.</xsl:comment>
		<xsl:apply-templates />
	</xsl:template>
	
	<!--
		if a RELAX NG pattern contains <p:fallback>,
		that pattern is replaced by its contents.
		
		This is used to handle features that RelaxNGCC can't handle.
	-->
	<xsl:template match="rng:*[p:fallback]">
		<xsl:apply-templates select="p:fallback/*"/>
	</xsl:template>
	
	<xsl:template match="rng:*">
		<xsl:copy>
			<xsl:for-each select="@*">
				<xsl:if test="namespace-uri(.)=''"><xsl:copy-of select="."/></xsl:if>
			</xsl:for-each>
			<xsl:apply-templates select="*|text()"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="p:ignore">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	
	<xsl:template match="rng:name/text()"><xsl:copy /></xsl:template>
	<xsl:template match="rng:value/text()"><xsl:copy /></xsl:template>
	<xsl:template match="rng:param/text()"><xsl:copy /></xsl:template>
	<xsl:template match="text()"/>
	
	<xsl:template match="*|@*"/><!-- ignore -->
	
</xsl:stylesheet>
