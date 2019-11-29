@echo off

REM
REM  Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
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
if not "%JAXB_HOME%" == "" goto CHECKJAVAHOME

rem Try to locate JAXB_HOME
set JAXB_HOME=%~dp0
set JAXB_HOME=%JAXB_HOME%\..
if exist "%JAXB_HOME%\mod\jaxb-xjc.jar" goto CHECKJAVAHOME

rem Unable to find it
echo JAXB_HOME must be set before running this script
goto END

:CHECKJAVAHOME
if not "%JAVA_HOME%" == "" goto USE_JAVA_HOME

set JAVA=java
goto LAUNCHXJC

:USE_JAVA_HOME
set JAVA="%JAVA_HOME%\bin\java"
goto LAUNCHXJC

:LAUNCHXJC
rem JXC module path
set JAXB_PATH=%JAXB_HOME%/mod/jaxb-xjc.jar;%JAXB_HOME%/mod/jakarta.xml.bind-api.jar;%JAXB_HOME%/mod/jaxb-impl.jar;%JAXB_HOME%/mod/jakarta.activation.jar

rem store and reset JAVA_TOOL_OPTIONS to avoid additional output from `java --version`
IF DEFINED JAVA_TOOL_OPTIONS (
  SET _OPTS=%JAVA_TOOL_OPTIONS%
  SET JAVA_TOOL_OPTIONS=
)

rem Set Java Version
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| %SystemRoot%\system32\find.exe "version"') do (
  set JAVA_VERSION1=%%i
)
for /f "tokens=1,2 delims=." %%j in ('echo %JAVA_VERSION1:~1,-1%') do (
  if "1" EQU "%%j" (
    set JAVA_VERSION2=%%k
  ) else (
    set JAVA_VERSION2=%%j
  )
)

rem Remove -ea
for /f "delims=-" %%i in ('echo %JAVA_VERSION2%') do set JAVA_VERSION=%%i
echo Java major version: %JAVA_VERSION%

if DEFINED _OPTS (
  SET JAVA_TOOL_OPTIONS=%_OPTS%
)

if %JAVA_VERSION% GEQ 9 goto JDK11_OR_GREATER
%JAVA% -cp %JAXB_PATH% %XJC_OPTS% com.sun.tools.xjc.XJCFacade %*
GOTO END

:JDK11_OR_GREATER
rem module path
%JAVA% --module-path %JAXB_PATH% %XJC_OPTS% -m com.sun.tools.xjc %*
GOTO END

:END
%COMSPEC% /C exit %ERRORLEVEL%
