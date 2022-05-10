@echo off

REM
REM  Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
REM
REM  This program and the accompanying materials are made available under the
REM  terms of the Eclipse Distribution License v. 1.0, which is available at
REM  http://www.eclipse.org/org/documents/edl-v10.php.
REM
REM  SPDX-License-Identifier: BSD-3-Clause
REM

rem
rem Make sure that JAXB_HOME and JAVA_HOME are set
rem
if not "%JAXB_HOME%" == "" goto SETMODULEPATH

rem Try to locate JAXB_HOME
set JAXB_HOME=%~dp0
set JAXB_HOME=%JAXB_HOME%\..
if exist "%JAXB_HOME%\mod\jaxb-xjc.jar" goto SETMODULEPATH

rem Unable to find it
echo JAXB_HOME must be set before running this script
goto END

:SETMODULEPATH
rem XJC module path
set JAXB_PATH=^
%JAXB_HOME%/mod/jakarta.xml.bind-api.jar;^
%JAXB_HOME%/mod/jaxb-xjc.jar;^
%JAXB_HOME%/mod/jaxb-core.jar;^
%JAXB_HOME%/mod/jaxb-impl.jar;^
%JAXB_HOME%/mod/jakarta.activation-api.jar
goto CHECKJAVAHOME

:CHECKJAVAHOME
if not "%JAVA_HOME%" == "" goto USE_JAVA_HOME

set JAVA=java
goto LAUNCHXJC

:USE_JAVA_HOME
set JAVA="%JAVA_HOME%\bin\java"
goto LAUNCHXJC

:LAUNCHXJC
rem module path
%JAVA% --module-path %JAXB_PATH% --add-modules com.sun.xml.bind %XJC_OPTS% -m com.sun.tools.xjc %*
GOTO END

:END
%COMSPEC% /C exit %ERRORLEVEL%
