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
    <xsl:import href="highlight.xsl"/>
    <xsl:import href="htmlProcessing.xsl"/>

    <xsl:template name="user.header.content">

    <small class="small">Links: <a href="index.html">Table of Contents</a> | <a href="release-documentation.html">Single HTML</a> | <a href="release-documentation.pdf">Single PDF</a></small>
    </xsl:template>

    <xsl:template name="user.head.content">
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-2105126-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
    </xsl:template>
</xsl:stylesheet>
