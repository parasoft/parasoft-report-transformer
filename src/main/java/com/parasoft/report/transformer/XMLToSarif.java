/*
 * Copyright 2024 Parasoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parasoft.report.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;
import org.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

@Command(
    name = "xml2sarif",
    mixinStandardHelpOptions = true,
    description = "Convert Parasoft XML report of static analysis to SARIF report.",
    usageHelpAutoWidth = true
)
public class XMLToSarif implements Callable<Integer> {

    public static final String SARIF_XSL_RESOURCE_PATH = "/xsl/sarif.xsl";

    private static final String ABSOLUTE_PATH_REGEX = "^(?:[a-zA-Z]:/|/).*";

    @Option(names = {"--inputXmlReport", "-i"}, required = true, description = "Path to the input Parasoft XML report of static analysis.")
    private File inputXmlReport;

    @Option(names = {"--outputSarifReport", "-o"}, description = "Path to the output SARIF report.")
    private File outputSarifReport;

    @Option(names = {"--projectRootPaths", "-p"}, description = "Path(s) to the project root(s). Use semicolon to separate multiple paths.")
    private String projectRootPaths;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new XMLToSarif()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            this.checkInputAndOutputReportParams();
            this.checkProjectRootPathsParam();
            this.convertXmlToSarif();

            return 0;
        } catch (Exception e) { // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to prevent exceptions from bubbling up and causing the program to terminate."
            Logger.error(MessageFormat.format("ERROR: {0}", e.getMessage()));
            return 1;
        }
    }

    /**
     * For unit test
     */
    String getProjectRootPaths() {
        return projectRootPaths;
    }

    private void checkInputAndOutputReportParams() {
        if (this.inputXmlReport == null) {
            throw new IllegalArgumentException("Input Parasoft XML report is required.");
        }

        if (!this.inputXmlReport.exists()) {
            throw new IllegalArgumentException(MessageFormat.format("Input Parasoft XML report file does not exist: {0}.", this.inputXmlReport));
        }
        if (!this.inputXmlReport.isFile()) {
            throw new IllegalArgumentException(MessageFormat.format("Input Parasoft XML report is not a file: {0}.", this.inputXmlReport));
        }
        if (!this.inputXmlReport.getName().toLowerCase().endsWith(".xml")) {
            throw new IllegalArgumentException(MessageFormat.format("Input Parasoft XML report is not an XML file: {0}.", this.inputXmlReport));
        }
        if (!this.inputXmlReport.canRead()) {
            throw new IllegalArgumentException(MessageFormat.format("Input Parasoft XML report file is not readable {0}.", this.inputXmlReport));
        }

        if (this.outputSarifReport == null) {
            // Replace the .xml extension of the input XML report with .sarif
            String outputSarifFileName = this.inputXmlReport.getName().replaceAll("\\.xml$", ".sarif");
            this.outputSarifReport = new File(this.inputXmlReport.getParentFile(), outputSarifFileName);
        } else if(!this.outputSarifReport.getAbsolutePath().endsWith(".sarif")) {
            this.outputSarifReport = new File(this.outputSarifReport.getAbsolutePath() + ".sarif");
            Logger.warn("WARNING: Output file name does not end with .sarif, automatically appended the extension.");
        }
    }

    private void checkProjectRootPathsParam() {
        if (this.projectRootPaths != null && !this.projectRootPaths.trim().isEmpty()) {
            String[] pathsArray = this.projectRootPaths.trim().split(";");
            String[] processedPaths = Arrays.stream(pathsArray).map((path) -> path.trim().replace("\\", "/")).toArray(String[]::new);
            for (String path : processedPaths) {
                if (!this.isAbsolutePath(path)) {
                    throw new IllegalArgumentException(MessageFormat.format("Project root path must be an absolute path: {0}", path));
                }
            }
            processedPaths = this.avoidDuplicateProjectRootPaths(processedPaths);
            this.projectRootPaths = String.join(";", processedPaths);
        } else {
            this.projectRootPaths = null;
        }
    }

    private void convertXmlToSarif() {
        Map<QName, XdmValue> paramsMap = new LinkedHashMap<>();
        if (this.projectRootPaths != null) {
            QName paramName = new QName("projectRootPaths");
            XdmValue paramValue = new XdmAtomicValue(this.projectRootPaths);
            paramsMap.put(paramName, paramValue);
        }
        Logger.info(MessageFormat.format("Transforming Parasoft XML report to SARIF report: {0} -> {1}", this.inputXmlReport, this.outputSarifReport));
        try {
            XSLConverterUtil.transformReport(this.inputXmlReport, this.outputSarifReport, SARIF_XSL_RESOURCE_PATH, paramsMap);
        } catch (SaxonApiException e) {
            throw new IllegalArgumentException(MessageFormat.format("Transformation error: {0}", e.getMessage()), e);
        }
        logUnconvertedPathsInSarifReport();
        Logger.info(MessageFormat.format("SARIF report has been created: {0}", this.outputSarifReport.getAbsolutePath()));
    }

    private boolean isAbsolutePath(String path) {
        Pattern pattern = Pattern.compile(ABSOLUTE_PATH_REGEX);
        return pattern.matcher(path).matches();
    }

    private String[] avoidDuplicateProjectRootPaths(String[] paths) {
        ArrayList<String> uniquePaths = new ArrayList<>();
        for (String path : paths) {
            path = path.endsWith("/") ? path : path + "/";
            if (uniquePaths.contains(path)) {
                Logger.warn(MessageFormat.format("WARNING: Duplicate project root path found: {0}", path));
                continue;
            }
            for (String uniquePath : uniquePaths) {
                if (path.startsWith(uniquePath) || uniquePath.startsWith(path)) {
                    throw new IllegalArgumentException(MessageFormat.format("Project path conflict: Path ''{0}'' contains or is contained by ''{1}'', which is not supported.", path, uniquePath));
                }
            }
            uniquePaths.add(path);
        }
        return uniquePaths.toArray(new String[0]);
    }

    private void logUnconvertedPathsInSarifReport() {
        if (this.projectRootPaths != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode jsonNode = mapper.readTree(this.outputSarifReport);
                JsonNode runsNode = jsonNode.get("runs");
                Iterator<JsonNode> runsElements = runsNode.elements();
                Set<String> uriSet = new HashSet<>();
                runsElements.forEachRemaining(run -> {
                    run.get("artifacts").elements().forEachRemaining(artifact -> {
                        if (artifact.size() > 0) {
                            JsonNode locationNode = artifact.get("location");
                            if (!locationNode.has("uriBaseId")) {
                                String uri = locationNode.get("uri").asText();
                                uriSet.add(uri);
                            }
                        }
                    });
                });
                if (!uriSet.isEmpty()) {
                    List<String> uriList = new ArrayList<>(uriSet);
                    Collections.sort(uriList);
                    Logger.info("The following paths have not been converted to relative paths:");
                    for (String uri : uriList) {
                        Logger.info(MessageFormat.format("  {0}", uri));
                    }
                }
            } catch (IOException e) {
                Logger.error(MessageFormat.format("ERROR: Read SARIF report error: {0}", e.getMessage()));
            }
        }
    }
}
