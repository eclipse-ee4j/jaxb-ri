#!/bin/bash
#
# Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause
#

if [ "$#" -eq 2 ]; then
    DEVELOPER_VERSION=$1
    RELEASE_VERSION=$2
fi

if [ "$#" -eq 1 ]; then
    CURRENT_VERSION=$1

    echo "No version specified, reading release version from pom file"
    CURRENT_VERSION=`cat pom.xml | grep '<version' -m 1 | cut -d ">" -f 2 | cut -d "<" -f 1 | cut -d "-" -f 1`

    echo "Major release version found: $CURRENT_VERSION"

    DATESTAMP=`date +%y%m%d.%H%M`
    BUILD_NUMBER=b${DATESTAMP}
    DEVELOPER_VERSION=${CURRENT_VERSION}-SNAPSHOT
    RELEASE_QUALIFIER=${BUILD_NUMBER}
    RELEASE_VERSION=${CURRENT_VERSION}-${RELEASE_QUALIFIER}
fi

SCRIPT_DIR=$(cd $(dirname $0); pwd -P)

cd $SCRIPT_DIR/../.. || {
    echo >&2 "Cannot change to top working directory"
    exit 1
}

command -v git > /dev/null 2>&1 || {
    echo >&2 "Cannot locate git executable"
    exit 1
}

GIT=$(command -v git 2>&1)

cleanup()
{
	${GIT} clean -d -f -x
	exit 1
}

edit_poms()
{
	TMPFILE=`mktemp $TMPDIR/${RELEASE_VERSION}.XXXXXXXX`
	find \
		$SCRIPT_DIR/../ \
		-name pom.xml \
		>> $TMPFILE

	echo "Updating pom files to have release versions ..."
	while read line
	do
		echo -n "Editing $line..."
		perl -i -pe "s|${DEVELOPER_VERSION}|${RELEASE_VERSION}|g" $line
		if [ $? -ne 0 ]; then
			echo "FAILED."
			echo "Replace versions failed for $line: $!"
			cleanup
		fi
		echo "DONE."
	done < "$TMPFILE"
	echo "DONE."
}

#############

echo "Updating JAXB POM Files"
echo "DEVELOPER_VERSION = ${DEVELOPER_VERSION}"
echo "RELEASE_VERSION = ${RELEASE_VERSION}"

edit_poms

exit 0
