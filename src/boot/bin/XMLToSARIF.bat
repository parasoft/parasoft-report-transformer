@echo off

set PARAM_NAME=%~1
set PARAM_VALUE=%~2
set XML_REPORT_PATH=
set SARIF_REPORT_PATH=
set JAVA_OR_PARASOFT_TOOL_ROOT_PATH=
set JAVA_PATH=

if "%PARAM_NAME%"=="" goto :required_param_error

:parse_args
REM Check if the parameter is empty or starts with "-" or "--"
if "%PARAM_VALUE%"=="" (
    goto :param_value_error
) else if "%PARAM_VALUE:~0,1%"=="-" (
    goto :param_value_error
)

REM Save option values into variables
if "%PARAM_NAME%"=="-i" (
    set XML_REPORT_PATH=%PARAM_VALUE%
) else if "%PARAM_NAME%"=="--inputXmlReport" (
    set XML_REPORT_PATH=%PARAM_VALUE%
) else if "%PARAM_NAME%"=="-o" (
    set SARIF_REPORT_PATH=%PARAM_VALUE%
) else if "%PARAM_NAME%"=="--outputSarifReport" (
    set SARIF_REPORT_PATH=%PARAM_VALUE%
) else if "%PARAM_NAME%"=="-t" (
    set JAVA_OR_PARASOFT_TOOL_ROOT_PATH=%PARAM_VALUE%
) else if "%PARAM_NAME%"=="--toolOrJavaHomeDir" (
    set JAVA_OR_PARASOFT_TOOL_ROOT_PATH=%PARAM_VALUE%
) else (
    echo Error: Invalid option "%PARAM_NAME%"
    goto :print_usage
)

REM Change to next parameter name and value
shift
shift
set PARAM_NAME=%~1
set PARAM_VALUE=%~2
if not "%PARAM_NAME%"=="" goto :parse_args

REM Validate option values
if "%XML_REPORT_PATH%"=="" goto :required_param_error

REM Find Java path
if "%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%"=="" (
    if not exist "%JAVA_HOME%" (
        echo Error: Java is not found. Please add Java in environment variable or provide "-t" or "--toolOrJavaHomeDir" option.
        exit /b 1;
    )
) else (
    if not exist "%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%" (
        echo Error: Tool or Java home directory: "%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%" is not found.
        exit /b 1
    )

    REM # Search for Java executable in following places:
    REM ## bin                       -- Java home directory
    REM ## bin/dottest/Jre_x64/bin   -- DotTest installation directory
    REM ## bin/jre/bin               -- Jtest, C++Test installation directory
	for %%p in (. bin\dottest\Jre_x64 bin\jre) do (
        if exist "%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%\%%p\bin\java.exe" (
            set JAVA_PATH="%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%\%%p"
            goto :generate_report
        )
    )
    echo Error: Tool or Java home directory is incorrect: "%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%". Please check "-t" or "--toolOrJavaHomeDir" value.
    exit /b 1
)

:generate_report
REM Set java environment if "-t" or "--toolOrJavaHomeDir" is set
if exist "%JAVA_PATH%" (
    set JAVA_HOME=%JAVA_PATH%
)
echo Using temporary JAVA_HOME: %JAVA_HOME%

REM Generate SARIF report
set COMMAND_ARGS=-i "%XML_REPORT_PATH%"
if not "%SARIF_REPORT_PATH%"=="" (
    set COMMAND_ARGS=%COMMAND_ARGS% -o "%SARIF_REPORT_PATH%"
)

call parasoft-report-transformer.bat xml2sarif %COMMAND_ARGS%
exit /b %errorlevel%

:required_param_error
echo Error: "-i" or "--inputXmlReport" is required.
goto :print_usage

:param_value_error
echo Error: Missing value for option: "%PARAM_NAME%"

:print_usage
echo Usage: XMLToSARIF.bat -i ^<inputXmlReport^> [-o ^<outputSarifReport^>] [-t ^<toolOrJavaHomeDir^>]
echo.
echo Options:
echo   -i, --inputXmlReport      Path to the input XML report. (required)
echo   -o, --outputSarifReport   Path to the output SARIF report.
echo   -t, --toolOrJavaHomeDir   Path to the tool or Java home directory.
echo.
exit /b 1
