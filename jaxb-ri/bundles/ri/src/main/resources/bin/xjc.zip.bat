@echo off

REM
REM  Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

REM
REM Make sure that WEBSERVICES_LIB, CLASSPATH, and JAVA_HOME are set
REM

set PRG=%0
set WEBSERVICES_LIB=%PRG%\..\..\..

REM
REM
set CLASSPATH=%WEBSERVICES_LIB%\jaxb2\lib\jakarta.xml.bind-api.jar;%WEBSERVICES_LIB%\jaxb2\lib\jaxb-core.jar;%WEBSERVICES_LIB%\jaxb2\lib\jaxb-xjc.jar;%WEBSERVICES_LIB%\jaxb2\lib\jaxb-impl.jar;%WEBSERVICES_LIB%\jaf\lib\activation.jar;%WEBSERVICES_LIB%\jaxp\lib\jsr173_api.jar;%WEBSERVICES_LIB%\jaxp\lib\resolver.jar


:CHECKJAVAHOME
if not "%JAVA_HOME%" == "" goto USE_JAVA_HOME

echo.
echo Warning: JAVA_HOME environment variable is not set.
echo   If compile fails because sun.* classes could not be found
echo   you will need to set the JAVA_HOME environment variable
echo   to the installation directory of java.
echo.

set JAVA=java
goto LAUNCHXJC

:USE_JAVA_HOME
set JAVA=%JAVA_HOME%\bin\java
goto LAUNCHXJC

:LAUNCHXJC
"%JAVA%" %XJC_OPTS% -cp "%CLASSPATH%" com.sun.tools.xjc.Driver %*
goto END

:END
%COMSPEC% /C exit %ERRORLEVEL%
