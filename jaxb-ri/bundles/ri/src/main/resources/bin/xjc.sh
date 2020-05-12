#!/bin/bash
#
# Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause
#


#
# Make sure that JAXB_HOME and JAVA_HOME are set
#
if [ -z "$JAXB_HOME" ]
then
    # search the installation directory

    PRG=$0
    progname=`basename $0`
    saveddir=`pwd`

    cd "`dirname $PRG`"

    while [ -h "$PRG" ] ; do
        ls=`ls -ld "$PRG"`
        link=`expr "$ls" : '.*-> \(.*\)$'`
        if expr "$link" : '.*/.*' > /dev/null; then
            PRG="$link"
        else
            PRG="`dirname $PRG`/$link"
        fi
    done

    JAXB_HOME=`dirname "$PRG"`/..

    # make it fully qualified
    cd "$saveddir"
    JAXB_HOME=`cd "$JAXB_HOME" && pwd`

    cd "$saveddir"
fi

[ `expr \`uname\` : 'CYGWIN'` -eq 6 ] &&
{
    JAXB_HOME=`cygpath -w "$JAXB_HOME"`
}

if [ -n "$JAVA_HOME" ]
then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=java
fi

#JXC module path
JAXB_PATH=${JAXB_HOME}/mod/jaxb-xjc.jar:\
${JAXB_HOME}/mod/jakarta.xml.bind-api.jar:\
${JAXB_HOME}/mod/jaxb-core.jar:\
${JAXB_HOME}/mod/jaxb-impl.jar:\
${JAXB_HOME}/mod/jakarta.activation.jar:\

if [ -n "$JAVA_TOOL_OPTIONS" ]
then
    _OPTS=$JAVA_TOOL_OPTIONS
    unset JAVA_TOOL_OPTIONS
fi

JAVA_VERSION=$("$JAVA" -version 2>&1 | head -n 1 | cut -d'"' -f2 | sed -E 's/^(1\.)?([0-9]+).+$/\2/')
echo "Java major version: ${JAVA_VERSION}"

if [ -n "$_OPTS" ]
then
    export JAVA_TOOL_OPTIONS=$_OPTS
fi

# Check if supports module path
if [[ ${JAVA_VERSION} -lt 9 ]] ;
then
  #classpath
  exec "${JAVA}" -cp "${JAXB_PATH}" ${XJC_OPTS} com.sun.tools.xjc.XJCFacade "$@"
else
  #module path
  exec "${JAVA}" --module-path "${JAXB_PATH}" ${XJC_OPTS} -m com.sun.tools.xjc "$@"
fi
