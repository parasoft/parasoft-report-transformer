# Parasoft Report Transformer
The parasoft-report-transformer project is designed to transform Parasoft XML reports into other formats.

Supported formats:
- SARIF

## Build instructions

1. Navigate to the project root directory.
2. Run:
   ```
    gradlew clean build
   ``` 
3. Find the distribution in build/distributions.

## Transform Parasoft Reports

### Transform Parasoft Static Analysis XML Report to SARIF Report
Run the *XMLToSARIF* script in *bin* folder with Parasoft XML report:
   * For Windows:
      ```cmd
      path\to\XMLToSARIF.bat -i <path\to\Parasoft\XML\report> [-o <path\to\output\SARIF\report>] [-t <path\to\parasoft\tool\installation\or\Java\home\directory>] [-p <path\to\project\root\directory>]
      ```
   * For Linux:
      ```shell
      path/to/XMLToSARIF.sh -i <path/to/Parasoft/XML/report> [-o <path/to/output/SARIF/report>] [-t <path/to/parasoft/tool/installation/or/Java/home/directory>] [-p <path/to/project/root/directory>]
      ```

#### Command line options:
| Parameter                          | Description                                                                                                                                                                                          |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| -i, --inputXmlReport (required)    | The path to Parasoft XML report that you want to transform to SARIF report.                                                                                                                          |
| -o, --outputSarifReport (Optional) | The path to output SARIF report. If not specified, the SARIF report will be generated in the same directory as input XML report.                                                                     |
| -t, --toolOrJavaHomeDir (Optional) | The path to Parasoft tools installation or Java home directory. If not specified, transformer will try to use Java from system environment variables.                                                |
| -p, --projectRootPaths (Optional)  | The absolute path to the project root directory, used to obtain relative file location paths. Use semicolons to separate multiple paths. If not specified, the file location paths will be absolute. |

<br/>If the XML reports generated by Parasoft Jtest, dotTEST, and C/C++test contain sufficient relevant information, the resulting SARIF reports will include files that were scanned during static analysis. For example:
```code
"runs" [
  {
    "tool": { ... }
    "results": [ 
      {
        "ruleId": "APSC_DV.003215.MCH",
        "level": "warning",
        "message": {
            "text": "This source file does not include a file header comment"
        },
        "locations": [
          {
            "physicalLocation": {
              "artifactLocation": {
                  "uri": "file:/C:/workspace/project/example/src/main/java/Example.java"
              }
            }
          }
        ]
      },
      {
        "ruleId": "NAMING.UHN",
        "level": "warning",
        "message": {
            "text": "Variable 'a' does not end with 'int'"
        },
        "locations": [
          {
            "physicalLocation": {
              "artifactLocation": {
                  "uri": "file:/C:/workspace/project/demo/src/main/java/Demo.java"
              }
            }
          }
        ],
        "suppressions": [
          {
            "kind": "external",
            "justification": "The reason of suppression."
          }
        ]
      }
    ],
    "artifacts": [
      {
        "location": {
            "uri": "file:/C:/workspace/project/example/src/main/java/Example.java"
         } 
      },
      {
        "location": {
            "uri": "file:/C:/workspace/project/demo/src/main/java/Demo.java"
        }
      }
    ]
  }
]
```
For C/C++test Professional 2023.1 or earlier, to include sufficient relevant information in static analysis reports, the reports must be generated with the **Add absolute file paths to XML data** option enabled. You can enable this option on the command line by setting the `report.location_details=true` property or in the settings file.

<br/>For the `justification` property in the SARIF report indicates why a result was suppressed, when using C/C++test Professional, you must use version 2024.1 or later and include the command-line option `-property report.additional.report.dir=<REPORT_DIR>` when generating reports. Additionally, ensure you use XML reports from the specified directory for transformation.

<br/>For a file location path (uri property) in the SARIF report, if its project root path is specified in `-p` option, its relative path will be shown; otherwise, its absolute path will be shown. For example, when the value of `-p` option is *"C:/workspace/project/example"*:
```code
"runs" [
  {
    "tool": { ... }
    "originalUriBaseIds": {
      "PROJECTROOT-1": {
          "uri": "file:/C:/workspace/project/example/"
      }
    },
    "results": [
      {
        "ruleId": "APSC_DV.003215.MCH",
        "level": "warning",
        "message": {
            "text": "This source file does not include a file header comment"
        },
        "locations": [
          {
            "physicalLocation": {
              "artifactLocation": {
                  "uri": "src/main/java/Example.java"
                  "uriBaseId": "PROJECTROOT-1"
              }
            }
          }
        ]
      },
      {
        "ruleId": "NAMING.UHN",
        "level": "warning",
        "message": {
            "text": "Variable 'a' does not end with 'int'"
        },
        "locations": [
          {
            "physicalLocation": {
              "artifactLocation": {
                  "uri": "file:/C:/workspace/project/demo/src/main/java/Demo.java"
              }
            }
          }
        ]
      }
    ],
    "artifacts": [
      {
        "location": {
            "uri": "src/main/java/Example.java"
            "uriBaseId": "PROJECTROOT-1"
         }
      },
      {
        "location": {
            "uri": "file:/C:/workspace/project/demo/src/main/java/Demo.java"
        }
      }
    ]
  }
]
```