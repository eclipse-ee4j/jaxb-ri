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

REM
REM Make sure that WEBSERVICES_LIB, CLASSPATH, and JAVA_HOME are set
REM

set PRG=%0
set WEBSERVICES_LIB=%PRG%\..\..\..


:CHECKJAVAHOME
if not "%JAVA_HOME%" == "" goto USE_JAVA_HOME

echo.
echo Warning: JAVA_HOME environment variable is not set.
echo   If compile fails because sun.* classes could not be found
echo   you will need to set the JAVA_HOME environment variable
echo   to the installation directory of java.
echo.

set JAVA=java
goto LAUNCHSCHEMAGEN

:USE_JAVA_HOME
set JAVA=%JAVA_HOME%\bin\java
goto LAUNCHSCHEMAGEN

:LAUNCHSCHEMAGEN
"%JAVA%" %SCHEMAGEN_OPTS% -cp "%WEBSERVICES_LIB%\jaxb2\lib\jakarta.xml.bind-api.jar;%WEBSERVICES_LIB%\jaxb2\lib\jaxb-core.jar;%WEBSERVICES_LIB%\jaxb2\lib\jaxb-xjc.jar;%WEBSERVICES_LIB%\jaxb2\lib\jaxb-jxc.jar;%WEBSERVICES_LIB%\jaxb2\lib\jaxb-impl.jar;%WEBSERVICES_LIB%\jaxp\lib\jsr173_api.jar;%WEBSERVICES_LIB%\jaf\lib\activation.jar;%WEBSERVICES_LIB%\jaxp\lib\resolver.jar" com.sun.tools.jxc.SchemaGeneratorFacade %*
goto END

:END
%COMSPEC% /C exit %ERRORLEVEL%
