@echo off

REM
REM  Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
REM
REM  This program and the accompanying materials are made available under the
REM  terms of the Eclipse Public License v. 2.0, which is available at
REM  http://www.eclipse.org/legal/epl-2.0.
REM
REM  This Source Code may also be made available under the following Secondary
REM  Licenses when the conditions for such availability set forth in the
REM  Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
REM  version 2 with the GNU Classpath Exception, which is available at
REM  https://www.gnu.org/software/classpath/license.html.
REM
REM  SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
set JAXB_PATH=%JAXB_HOME%/mod/jakarta.xml.bind-api.jar;%JAXB_HOME%/mod/jaxb-jxc.jar;%JAXB_HOME%/mod/jaxb-xjc.jar;%JAXB_HOME%/mod/jaxb-runtime.jar;%JAXB_HOME%/mod/stax-ex.jar;%JAXB_HOME%/mod/istack-commons-runtime.jar;%JAXB_HOME%/mod/istack-commons-tools.jar;%JAXB_HOME%/mod/FastInfoset.jar;%JAXB_HOME%/mod/dtd-parser.jar;%JAXB_HOME%/mod/rngom.jar;%JAXB_HOME%/mod/codemodel.jar;%JAXB_HOME%/mod/xsom.jar;%JAXB_HOME%/mod/txw2.jar;%JAXB_HOME%/mod/relaxng-datatype.jar;%JAXB_HOME%/mod/jakarta.activation-api.jar

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

if %JAVA_VERSION% GEQ 9 goto JDK9_OR_GREATER

if not "%SCHEMAGEN_OPTS%" == "" goto LAUNCHSCHEMAGENWITHOPTS
%JAVA% -cp %TOOLS_PATH% com.sun.tools.jxc.SchemaGeneratorFacade %*
goto END

:LAUNCHSCHEMAGENWITHOPTS
%JAVA% %SCHEMAGEN_OPTS% -cp %TOOLS_PATH% com.sun.tools.jxc.SchemaGeneratorFacade %*
goto END

:JDK9_OR_GREATER
if %JAVA_VERSION% GTR 10 goto JDK11_OR_GREATER
rem module path + upgrade
%JAVA% --upgrade-module-path %JAXB_HOME%/mod/jakarta.xml.bind-api.jar %SCHEMAGEN_OPTS% --module-path %LOCALCLASSPATH% -m com.sun.tools.jxc/com.sun.tools.jxc.SchemaGeneratorFacade %*
goto END

:JDK11_OR_GREATER
rem module path only
%JAVA% %SCHEMAGEN_OPTS% --module-path %LOCALCLASSPATH% -m com.sun.tools.jxc/com.sun.tools.jxc.SchemaGeneratorFacade %*
goto END

:END
%COMSPEC% /C exit %ERRORLEVEL%
