#!/bin/bash
#
# Copyright (c) 2020 Oracle and/or its affiliates.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause
#

# utility to help importing release documentation

VERSION=4.0.1
ARTIFACT=https://jakarta.oss.sonatype.org/content/groups/staging/com/sun/xml/bind/jaxb-release-documentation//$VERSION/jaxb-release-documentation-$VERSION-docbook.zip

wget -O release-documentation.zip $ARTIFACT

mkdir -p $VERSION/docs

unzip -d $VERSION/docs release-documentation.zip

find $VERSION/docs -name "*.html" -exec sed -i '' $'1s/^/---\\\nlayout: content\\\n---\\\n/' {} \;

rm release-documentation.zip
