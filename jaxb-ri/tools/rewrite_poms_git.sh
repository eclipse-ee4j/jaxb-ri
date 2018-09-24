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


# if option -n ... do not commit
COMMIT=Y
RELEASE=false

while getopts ":nv:r" opt; do
  case $opt in
    n)
      COMMIT=N
      ;;
    v)
      CUSTOM_VERSION=${OPTARG}
      echo "Using custom version: ${CUSTOM_VERSION}"
      ;;
    r)
      RELEASE=true
      echo "Using release mode, to append buildnumber remove -r flag."
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
  esac
done
echo "Script will commit changes: [$COMMIT] (pass option -n not to commit)"

CURRENT_VERSION=`cat pom.xml | grep '<version' -m 1 | cut -d ">" -f 2 | cut -d "<" -f 1 | cut -d "-" -f 1`
echo "Current version: ${CURRENT_VERSION}"

SCRIPT_DIR=$(cd $(dirname $0); pwd -P)

cd $SCRIPT_DIR/.. || {
	echo >&2 "Cannot change to top of GIT working directory"
	exit 1
}

command -v git > /dev/null 2>&1 || {
	echo >&2 "Cannot locate git executable"
	exit 1
}

GIT=$(command -v git 2>&1)
#GIT=$(command -v echo 2>&1)
LAST_GIT_COMMIT=$(${GIT} rev-parse --short HEAD) || exit 1

DATESTAMP=`date +%y%m%d.%H%M`
BUILD_NUMBER=b${DATESTAMP}
DEVELOPER_VERSION=${CURRENT_VERSION}-SNAPSHOT
RELEASE_QUALIFIER=${BUILD_NUMBER}

if [ -z "${CUSTOM_VERSION}" ]; then
  echo "No version specified, reading release version from pom file"
  RELEASE_VERSION=${CURRENT_VERSION}
else
  RELEASE_VERSION=${CUSTOM_VERSION}
fi;

if [ "${RELEASE}" = true ]; then
  echo "Release version: ${RELEASE_VERSION}"
else
  RELEASE_VERSION="${RELEASE_VERSION}-${RELEASE_QUALIFIER}"
  echo "Pre-release version: ${RELEASE_VERSION}"
fi;



RELEASE_TAG=${RELEASE_VERSION}

cleanup()
{
	${GIT} clean -d -f -x
	exit 1
}

edit_poms()
{
	TMPFILE=`mktemp $TMPDIR/${RELEASE_VERSION}.XXXXXXXX` || cleanup
	find \
		$SCRIPT_DIR/../ \
		-name pom.xml \
		>> $TMPFILE

	echo "Updating pom files to have release versions ..."
	while read line
	do
		echo -n "Editing $line..."
		perl -i -pe "s|<version>${DEVELOPER_VERSION}|<version>${RELEASE_VERSION}|g" $line
		if [ $? -ne 0 ]; then
			echo "FAILED."
			echo "Replace versions failed for $line: $!"
			cleanup
		fi
		echo "DONE."

		echo -n "Adding $line to git index..."
		${GIT} add $line
		if [ $? -ne 0 ]; then
			echo "FAILED."
			echo "git add failed for $line: $!"
			cleanup
		fi
		echo "DONE."
	done < "$TMPFILE"
}

function commit_changes() {
	echo -n "Committing rewritten POMs to git..."
	${GIT} commit --verbose -m "Preparing for release ${RELEASE_VERSION}"
	if [ $? -ne 0 ]; then
		echo "FAILED."
		echo "git commit failed: $!"
		cleanup
	fi
	echo "DONE."

	echo -n "Preparing tag ${RELEASE_TAG}..."
	${GIT} tag -m "Tagging for Release ${RELEASE_VERSION}" ${RELEASE_TAG}
	if [ $? -ne 0 ]; then
		echo "FAILED."
		echo "git tag failed: $!"
		cleanup
	fi
	echo "DONE."

	echo -n "Reverting to developer versions..."
	${GIT} revert --no-edit --no-commit HEAD
	if [ $? -ne 0 ]; then
		echo "FAILED."
		echo "git revert failed: $!"
		cleanup
	fi
	echo "DONE."

	echo -n "Committing rewritten POMs to git..."
	${GIT} commit --verbose -m "Preparing for development ${DEVELOPER_VERSION}"
	if [ $? -ne 0 ]; then
		echo "FAILED."
		echo "git commit failed: $!"
		cleanup
	fi
	echo "DONE."
}

push_changes()
{

	echo -n "Pushing changes..."
	${GIT} push 
	if [ $? -ne 0 ]; then
		${GIT} pull --rebase
		${GIT} push
		if [ $? -ne 0 ]; then
			echo "FAILED."
			echo "git push failed: $!"
			cleanup
		fi
	fi
	echo "DONE."

	echo -n "Pushing tag..."
	${GIT} push origin ${RELEASE_TAG}
	if [ $? -ne 0 ]; then
		${GIT} pull --rebase
		${GIT} push
		if [ $? -ne 0 ]; then
			echo "FAILED."
			echo "git push failed: $!"
			cleanup
		fi
	fi
	echo "DONE."

}

checkout_tag()
{
	echo -n "Checking out tag ${RELEASE_TAG}..."
	${GIT} checkout ${RELEASE_TAG}
	if [ $? -ne 0 ]; then
		echo "FAILED."
		echo "git checkout failed: $!"
		cleanup
	fi
	echo "DONE."
}

#############

echo "Rewriting Web Services POM Files"
echo "DEVELOPER_VERSION = ${DEVELOPER_VERSION}"
echo "RELEASE_VERSION = ${RELEASE_VERSION}"

${GIT} clean -d -f -x

edit_poms

if [ "$COMMIT" = "Y" ]; then
    commit_changes
    push_changes
    checkout_tag
fi

exit 0
