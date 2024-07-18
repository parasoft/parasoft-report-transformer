#!/bin/bash
print_usage() {
    echo "Usage: XMLToSARIF.sh -i <inputXmlReport> [-o <outputSarifReport>] [-t <toolOrJavaHomeDir>] [-p <projectRootPaths>]"
    echo ""
    echo "Options:"
    echo "  -i, --inputXmlReport      Path to the input Parasoft XML report. (required)"
    echo "  -o, --outputSarifReport   Path to the output SARIF report."
    echo "  -t, --toolOrJavaHomeDir   Path to the tool or Java home directory."
    echo "  -p, --projectRootPaths    Semicolon-separated paths to the project roots."
    echo ""
}

check_param() {
    # check if the parameter is empty or starts with "--" or "-"
    local param_name="$1"
    local param_value="$2"
    if [[ -z "$param_value" || "$param_value" == --* || "$param_value" == -* ]]; then
        echo "Error: Missing value for option: \"$param_name\" "
        print_usage
        exit 1
    fi
}

getJavaPath() {
    local javaOrParasoftToolRootPath="$1"

    if [ ! -d "$javaOrParasoftToolRootPath" ]; then
        echo "Error: \"$javaOrParasoftToolRootPath\" is not found."
        exit 1;
    fi

    local javaFileName="java"
    # Search for Java executable in following places:
    ## bin                       -- Java home directory
    ## bin/dottest/Jre_x64/bin   -- DotTest installation directory
    ## bin/jre/bin               -- Jtest, C++Test installation directory
    local javaPaths=("bin" "bin/dottest/Jre_x64/bin" "bin/jre/bin")

    for path in "${javaPaths[@]}"; do
        local javaFilePath="$javaOrParasoftToolRootPath/$path/$javaFileName"
        if [ -f "$javaFilePath" ]; then
            echo "$javaFilePath"
            return 0
        fi
    done

    return 1
}

# 1.Initialize variables:
BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
BIN_DIR="$BASE_DIR/bin"
export PATH="$BIN_DIR:$PATH"

parasoft_tool_or_java_root_path=""
xml_report_path=""
sarif_report_path=""
project_root_paths=""

# 2. Save option values into variables
while [[ $# -gt 0 ]] ; do
  param="$1"
  value="$2"
  case "$param" in
    -i|--inputXmlReport)
        check_param "$param" "$value"
        xml_report_path="$value"
        shift 2 ;;
    -o|--outputSarifReport)
        check_param "$param" "$value"
        sarif_report_path="$value"
        shift 2 ;;
    -t|--toolOrJavaHomeDir)
        check_param "$param" "$value"
        parasoft_tool_or_java_root_path="$value"
        shift 2 ;;
    -p|--projectRootPaths)
        check_param "$param" "$value"
        project_root_paths="$value"
        shift 2 ;;
    --) shift ; break ;;
    *)
        echo "Error: Invalid option \"$param\""
        print_usage
        exit 1 ;;
  esac
done

# 3. Validate option values

# Validate option values
if [ -z "$xml_report_path" ]; then
    echo "Error: \"-i\" or \"--inputXmlReport\" is required."
    print_usage
    exit 1
fi

# Find Java path and set temporary JAVA_HOME if necessary
if [ -z "$parasoft_tool_or_java_root_path" ]; then
    java_path=$(which java)
    if [ -z "$java_path" ]; then
        echo "Error: Java is not found. Please add Java in environment variable or provide \"-t\" or \"--toolOrJavaHomeDir\" option."
        exit 1;
    fi
else
    if java_path=$(getJavaPath "$parasoft_tool_or_java_root_path"); then
        java_home="${java_path%bin/java}"
        export JAVA_HOME=$java_home
        echo "Java home directory set to: $JAVA_HOME"
    else
        echo "Error: Tool or Java home directory is incorrect: \"$parasoft_tool_or_java_root_path\". Please check \"-t\" or \"--toolOrJavaHomeDir\" value."
        exit 1;
    fi
fi

## 4. Generate SARIF report
args=(
  -i "$xml_report_path"
)
if [ -n "$sarif_report_path" ]; then
  args+=(-o "$sarif_report_path")
fi
if [ -n "$project_root_paths" ]; then
  args+=(-p "$project_root_paths")
fi

parasoft-report-transformer xml2sarif "${args[@]}"

