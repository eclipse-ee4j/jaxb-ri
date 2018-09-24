#!/bin/sh +x
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
# Make sure that JAXB_HOME and JAVA_HOME are set
#
if [ -z "$JAXB_HOME" ]
then
    # search the installation directory
    
    PRG=$0
    progname=`basename $0`
    saveddir=`pwd`
    
    cd `dirname $PRG`
    
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
    
    cd $saveddir
fi


if [ -n "$JAVA_HOME" ]
then
    JAVA="$JAVA_HOME"/bin/java
else
    JAVA=apt
fi

DEBUG_OPTS="-J-Xdebug -J-Xnoagent -J-Djava.compiler=NONE -J-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000'"

if [ `expr \`uname\` : 'CYGWIN'` -eq 6 ]
then
    JAXB_HOME="`cygpath -w "$JAXB_HOME"`"
fi


exec "$JAVA" $DEBUG_OPTS $SCHEMAGEN_OPTS -cp "$JAXB_HOME"/dist/lib/jaxb-core.jar:$JAXB_HOME"/dist/lib/jaxb-xjc.jar:$JAXB_HOME"/dist/lib/jaxb-jxc.jar:$JAXB_HOME"/dist/lib/jaxb-impl.jar com.sun.tools.jxc.SchemaGeneratorFacade "$@"
