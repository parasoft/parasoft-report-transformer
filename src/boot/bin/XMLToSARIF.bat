@echo off

setlocal enabledelayedexpansion

for %%i in ("%~dp0..") do set "BASE_DIR=%%~fi"
set "BIN_DIR=%BASE_DIR%\bin"
set "PATH=%BIN_DIR%;%PATH%"

if "%~1"=="" goto :required_param_error

set "XML_REPORT_PATH="
set "SARIF_REPORT_PATH="
set "JAVA_OR_PARASOFT_TOOL_ROOT_PATH="
set "PROJECT_ROOT_PATHS="

:parse_args
set "PARAM_NAME=%~1"
set "PARAM_VALUE=%~2"
if "%PARAM_NAME%"=="" goto :end_parse_args

REM Check if the parameter is empty or starts with "-" or "--"
if "%PARAM_VALUE%"=="" (
    goto :param_value_error
) else if "%PARAM_VALUE:~0,1%"=="-" (
    goto :param_value_error
)

REM Save option values into variables
if "%PARAM_NAME%"=="-i" (
    set "XML_REPORT_PATH=%PARAM_VALUE%"
) else if "%PARAM_NAME%"=="--inputXmlReport" (
    set "XML_REPORT_PATH=%PARAM_VALUE%"
) else if "%PARAM_NAME%"=="-o" (
    set "SARIF_REPORT_PATH=%PARAM_VALUE%"
) else if "%PARAM_NAME%"=="--outputSarifReport" (
    set "SARIF_REPORT_PATH=%PARAM_VALUE%"
) else if "%PARAM_NAME%"=="-t" (
    set "JAVA_OR_PARASOFT_TOOL_ROOT_PATH=%PARAM_VALUE%"
) else if "%PARAM_NAME%"=="--toolOrJavaHomeDir" (
    set "JAVA_OR_PARASOFT_TOOL_ROOT_PATH=%PARAM_VALUE%"
) else if "%PARAM_NAME%"=="-p" (
    set "PROJECT_ROOT_PATHS=%PARAM_VALUE%"
) else if "%PARAM_NAME%"=="--projectRootPaths" (
    set "PROJECT_ROOT_PATHS=%PARAM_VALUE%"
) else (
    echo Error: Invalid option "%PARAM_NAME%"
    goto :print_usage
)
shift
shift
goto :parse_args

:end_parse_args

REM Validate option values
if "%XML_REPORT_PATH%"=="" goto :required_param_error

REM Find Java path
if "%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%"=="" (
    where java >nul 2>&1
    if %errorlevel% neq 0 (
        echo Error: Java is not found. Please add Java in environment variable or provide "-t" or "--toolOrJavaHomeDir" option.
        exit /b 1
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
	for %%p in (bin bin\dottest\Jre_x64\bin bin\jre\bin) do (
        if exist "%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%\%%p\java.exe" (
            set "java_path=%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%\%%p"
            set "JAVA_HOME=!java_path:~0,-4!"
            echo Java home directory set to: "!JAVA_HOME!"
            goto :generate_report
        )
    )
    echo Error: Tool or Java home directory is incorrect: "%JAVA_OR_PARASOFT_TOOL_ROOT_PATH%". Please check "-t" or "--toolOrJavaHomeDir" value.
    exit /b 1
)

:generate_report
REM Generate SARIF report
set COMMAND_ARGS=-i "%XML_REPORT_PATH%"
if not "%SARIF_REPORT_PATH%"=="" (
    set COMMAND_ARGS=%COMMAND_ARGS% -o "%SARIF_REPORT_PATH%"
)
if not "%PROJECT_ROOT_PATHS%"=="" (
    :remove_trailing_backslashes
    if "%PROJECT_ROOT_PATHS:~-1%"=="\" (
        set "PROJECT_ROOT_PATHS=%PROJECT_ROOT_PATHS:~0,-1%"
        goto :remove_trailing_backslashes
    )
    set COMMAND_ARGS=%COMMAND_ARGS% -p "%PROJECT_ROOT_PATHS%"
)

parasoft-report-transformer xml2sarif %COMMAND_ARGS%

:required_param_error
echo Error: "-i" or "--inputXmlReport" is required.
goto :print_usage

:param_value_error
echo Error: Missing value for option: "%PARAM_NAME%"

:print_usage
echo Usage: XMLToSARIF.bat -i ^<inputXmlReport^> [-o ^<outputSarifReport^>] [-t ^<toolOrJavaHomeDir^>] [-p ^<projectRootPaths^>]
echo.
echo Options:
echo   -i, --inputXmlReport      Path to the input XML report. (required)
echo   -o, --outputSarifReport   Path to the output SARIF report.
echo   -t, --toolOrJavaHomeDir   Path to the tool or Java home directory.
echo   -p, --projectRootPaths    Semicolon-separated paths to the project roots.
echo.
exit /b 1

endlocal