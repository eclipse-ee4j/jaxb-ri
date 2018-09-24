#!/bin/sh
#
# Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause
#

#
# Script to run schemagen
#

# Resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

WEBSERVICES_LIB=$PRG/../../..

if [ -n "$JAVA_HOME" ]
then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA=java
fi
 
$JAVA $SCHEMAGEN_OPTS -cp "$WEBSERVICES_LIB/jaxb/lib/jaxb-core.jar:$WEBSERVICES_LIB/jaxb/lib/jaxb-xjc.jar:$WEBSERVICES_LIB/jaxb/lib/jaxb-jxc.jar:$WEBSERVICES_LIB/jaxb/lib/jaxb-impl.jar" com.sun.tools.jxc.SchemaGeneratorFacade "$@"
