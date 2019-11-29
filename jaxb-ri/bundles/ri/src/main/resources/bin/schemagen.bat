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
if not "%JAXB_HOME%" == "" goto SETCLASSPATH

rem Try to locate JAXB_HOME
set JAXB_HOME=%~dp0
set JAXB_HOME=%JAXB_HOME%\..
if exist %JAXB_HOME%\mod\jaxb-xjc.jar goto SETCLASSPATH

rem Unable to find it
echo JAXB_HOME must be set before running this script
goto END

:SETCLASSPATH
set JAXB_PATH=%JAXB_HOME%/mod/jakarta.xml.bind-api.jar;%JAXB_HOME%/mod/jaxb-jxc.jar;%JAXB_HOME%/mod/jaxb-xjc.jar;%JAXB_HOME%/mod/jaxb-impl.jar;%JAXB_HOME%/mod/jakarta.activation.jar

if "%CLASSPATH%" == "" goto NOUSERCLASSPATH
set LOCALCLASSPATH=%JAXB_PATH%;%CLASSPATH%
goto CHECKJAVAHOME

:NOUSERCLASSPATH
set LOCALCLASSPATH=%JAXB_PATH%
goto CHECKJAVAHOME

:CHECKJAVAHOME
if not "%JAVA_HOME%" == "" goto USE_JAVA_HOME

set JAVA=java
for /f "" %%i in ('where java') do set BINDIR=%%~dpi
set TOOLS_PATH=%BINDIR%/../lib/tools.jar;%LOCALCLASSPATH%
goto LAUNCHSCHEMAGEN

:USE_JAVA_HOME
set JAVA="%JAVA_HOME%\bin\java"
set TOOLS_PATH=%JAVA_HOME%/lib/tools.jar;%LOCALCLASSPATH%
goto LAUNCHSCHEMAGEN

:LAUNCHSCHEMAGEN
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

if not "%SCHEMAGEN_OPTS%" == "" goto LAUNCHSCHEMAGENWITHOPTS
%JAVA% -cp %TOOLS_PATH% com.sun.tools.jxc.SchemaGeneratorFacade %*
goto END

:LAUNCHSCHEMAGENWITHOPTS
%JAVA% %SCHEMAGEN_OPTS% -cp %TOOLS_PATH% com.sun.tools.jxc.SchemaGeneratorFacade %*
goto END

:JDK11_OR_GREATER
rem module path only
%JAVA% %SCHEMAGEN_OPTS% --module-path %LOCALCLASSPATH% -m com.sun.tools.jxc %*
goto END

:END
%COMSPEC% /C exit %ERRORLEVEL%
