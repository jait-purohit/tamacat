@echo off

set JAVA_OPT=

if not "%JAVA_HOME%" == "" goto OkJHome
echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
SET JAVA_EXE="%JAVA_HOME%\bin\java.exe"

if "%OS%"=="Windows_NT" SET SERVER_HOME=%~dp0\..

set CLASSPATH_JAR=%SERVER_HOME%\conf

for %%i in ("%SERVER_HOME%"\lib\*.jar) do call classpath_jar.bat %%i
rem echo %CLASSPATH_JAR%

%JAVA_EXE% %JAVA_OPT% -classpath %CLASSPATH_JAR% org.tamacat.httpd.Httpd

