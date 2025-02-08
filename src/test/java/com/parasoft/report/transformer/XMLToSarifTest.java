package com.parasoft.report.transformer;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.startsWith;

public class XMLToSarifTest {

    private static final String TEST_RESOURCES_LOC = "src/test/resources/com/parasoft/report/transformer/XMLToSarifTest/xml";

    @Test
    public void testXMLToSarif_jtest202402_normal_1() throws IOException {
        this.testXMLToSarif(
                "jtest-report-202401.xml",
                "jtest-report-202401.sarif",
                "jtest-report-202401.sarif",
                "D:/JavaProjectTemplate/",
                "D:/JavaProjectTemplate/");
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_2() throws IOException {
        this.testXMLToSarif(
                "jtest-report 202401.xml",
                "jtest-report 202401.sarif",
                "jtest-report-202401.sarif",
                "D:\\JavaProjectTemplate",
                "D:/JavaProjectTemplate/");
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_noOutputSarifReportParam() throws IOException {
        this.testXMLToSarif(
                "jtest-report-202401.xml",
                null,
                "jtest-report-202401.sarif",
                "D:/JavaProjectTemplate",
                "D:/JavaProjectTemplate/");
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_noProjectRootPathsParam() throws IOException {
        this.testXMLToSarif(
                "jtest-report-202401.xml",
                "jtest-report-202401-1.sarif",
                "jtest-report-202401-1.sarif",
                "",
                null);
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_multipleProjectRootPaths() throws IOException {
        this.testXMLToSarif(
                "jtest-report-202401.xml",
                "jtest-report-202401-2.sarif",
                "jtest-report-202401-2.sarif",
                "D:/JavaProjectTemplate/; D:/JavaProjectTemplate-1; D:\\JavaProjectTemplate-2",
                "D:/JavaProjectTemplate/;D:/JavaProjectTemplate-1/;D:/JavaProjectTemplate-2/");
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_projectRootPathsCanNotBeMatched() throws IOException {
        this.testXMLToSarif(
                "jtest-report-202401.xml",
                "jtest-report-202401-3.sarif",
                "jtest-report-202401-3.sarif",
                "/JavaProjectTemplate/; D:/JavaProjectTemplate-1",
                "/JavaProjectTemplate/;D:/JavaProjectTemplate-1/");
    }

    @Test
    public void testXMLToSarif_jtest20230201_normal_multipleProjects() throws IOException {
        this.testXMLToSarif(
                "jtest_report-20230201-multiple_projects.xml",
                "jtest_report-20230201-multiple_projects.sarif",
                "jtest_report-20230201-multiple_projects.sarif",
                "D:/test/soavirt-someip_2; E:/Parasoft/testMultipleProjects/jtest/javaprojecttemplate",
                "D:/test/soavirt-someip_2/;E:/Parasoft/testMultipleProjects/jtest/javaprojecttemplate/");
    }

    @Test
    public void testXMLToSarif_cpptest_pro202302_normal_multipleProjects() throws IOException {
        this.testXMLToSarif(
                "cpptest-pro_report-202302-multiple_projects.xml",
                "cpptest-pro_report-202302-multiple_projects.sarif",
                "cpptest-pro_report-202302-multiple_projects.sarif",
                "E:/Parasoft/testMultipleProjects/cppPro/flowanalysiscpp_2; D:/test/flowanalysiscpp",
                "E:/Parasoft/testMultipleProjects/cppPro/flowanalysiscpp_2/;D:/test/flowanalysiscpp/");
    }

    @Test
    public void testXMLToSarif_cpptest_std202302_normal_multipleProjects() throws IOException {
        this.testXMLToSarif(
                "cpptest-std_report-202302-multiple_projects.xml",
                "cpptest-std_report-202302-multiple_projects.sarif",
                "cpptest-std_report-202302-multiple_projects.sarif",
                "E:/Parasoft/testMultipleProjects/cppstand/flowanalysiscpp_2; D:/test/flowanalysiscpp",
                "E:/Parasoft/testMultipleProjects/cppstand/flowanalysiscpp_2/;D:/test/flowanalysiscpp/");
    }

    @Test
    public void testXMLToSarif_cpptest_pro202401_normal_additionalReport() throws IOException {
        this.testXMLToSarif(
                "cpptest-pro-report-202401-additional-report.xml",
                "cpptest-pro-report-202401-additional-report.sarif",
                "cpptest-pro-report-202401-additional-report.sarif",
                "D:\\reports\\projects\\flowanalysiscpp%20(1)",
                "D:/reports/projects/flowanalysiscpp%20(1)/");
    }

    @Test
    public void testXMLToSarif_cpptest_pro202401_normal_additionalWithFilterReport_1() throws IOException {
        this.testXMLToSarif(
                "cpptest-pro-report-202401-additional-with-filter-report-1.xml",
                "cpptest-pro-report-202401-additional-with-filter-report-1.sarif",
                "cpptest-pro-report-202401-additional-with-filter-report-1.sarif",
                "/mnt/d/bitbucket/flowanalysiscpp/",
                "/mnt/d/bitbucket/flowanalysiscpp/");
    }

    @Test
    public void testXMLToSarif_cpptest_pro202401_normal_additionalWithFilterReport_2() throws IOException {
        this.testXMLToSarif(
                "cpptest-pro-report-202401-additional-with-filter-report-2.xml",
                "cpptest-pro-report-202401-additional-with-filter-report-2.sarif",
                "cpptest-pro-report-202401-additional-with-filter-report-2.sarif",
                "/mnt/d/bitbucket/flowanalysiscpp/",
                "/mnt/d/bitbucket/flowanalysiscpp/");
    }

    @Test
    public void testXMLToSarif_cpp_pro_202401_normal() throws IOException {
        this.testXMLToSarif(
                "cpptest-pro-report-202401.xml",
                  "cpptest-pro-report-202401.sarif",
                "cpptest-pro-report-202401.sarif",
                "D:\\reports\\projects\\flowanalysiscpp\\",
                "D:/reports/projects/flowanalysiscpp/");
    }

    @Test
    public void testXMLToSarif_dottest202401_normal() throws IOException {
        this.testXMLToSarif(
                "dottest-report-202401.xml",
                "dottest-report-202401.sarif",
                "dottest-report-202401.sarif",
                "D:/reports/projects/bankexample.net/Parasoft.Dottest.Examples.Bank/",
                "D:/reports/projects/bankexample.net/Parasoft.Dottest.Examples.Bank/");
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_withSuppression() throws IOException {
        this.testXMLToSarif(
                "jtest-report-202402-suppression.xml",
                "jtest-report-202402-suppression.sarif",
                "jtest-report-202402-suppression.sarif",
                "D:/JavaProjectTemplate/",
                "D:/JavaProjectTemplate/");
    }

    @Test
    public void testXMLToSarif_invalid_xml_report() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/invalid-report.xml"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Transformation error:")));
        });
    }

    @Test
    public void testXMLToSarif_inputXmlReportDoseNotExist() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/notExist.xml"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Input Parasoft XML report file does not exist:")));
            mockedLogger.verify(() -> Logger.error(endsWith("notExist.xml.")));
        });
    }

    @Test
    public void testXMLToSarif_inputXmlReportIsNotAFile() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Input Parasoft XML report is not a file:")));
            mockedLogger.verify(() -> Logger.error(endsWith("xml.")));
        });
    }

    @Test
    public void testXMLToSarif_inputXmlReportIsNotAnXMLFile() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/notXmlFile.txt"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Input Parasoft XML report is not an XML file:")));
            mockedLogger.verify(() -> Logger.error(endsWith("txt.")));
        });
    }

    @Test
    public void testXMLToSarif_outputSarifReportIsNotSarif() {
        testWithMockedLogger(mockedLogger -> {
            try {
                XMLToSarif xml2sarif = new XMLToSarif();
                CommandLine command = new CommandLine(xml2sarif);
                String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                                 "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report-202401.notSarifExtension"};
                int exitCode = command.execute(args);

                assertEquals(0, exitCode);
                mockedLogger.verify(() -> Logger.warn("WARNING: Output file name does not end with .sarif, automatically appended the extension."));
            } finally {
                File sariFile = new File(TEST_RESOURCES_LOC + "/jtest-report-202401.notSarifExtension.sarif");
                assertTrue(sariFile.exists());
                if (sariFile.exists()) {
                    sariFile.delete();
                }
            }
        });
    }

    @Test
    public void testXMLToSarif_invalidOutputSarifReportName() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            testWithMockedLogger(mockedLogger -> {
                XMLToSarif xml2sarif = new XMLToSarif();
                CommandLine command  = new CommandLine(xml2sarif);
                String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                        "--outputSarifReport", TEST_RESOURCES_LOC + "/invalid-sarif-report-><|.sarif"};
                int exitCode = command.execute(args);

                assertEquals(1, exitCode);
                mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Transformation error: Failed to create output file file:")));
            });
        } else {
            System.out.println("Skipping test on Linux");
        }
    }

    @Test
    public void testXMLToSarif_duplicateProjectRootPaths() {
        testWithMockedLogger(mockedLogger -> {
            try {
                this.testXMLToSarif(
                        "jtest-report 202401.xml",
                        "jtest-report 202401.sarif",
                        "jtest-report-202401.sarif",
                        "D:\\JavaProjectTemplate; D:/JavaProjectTemplate",
                        "D:/JavaProjectTemplate/");
            } catch (IOException e) {
                fail("Unexpected exception", e);
            }
            mockedLogger.verify(() -> Logger.warn("WARNING: Duplicate project root path found: D:/JavaProjectTemplate/"));
        });
    }

    @Test
    public void testXMLToSarif_invalidProjectRootPaths_relativePath() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report 202401.xml",
                    "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report 202401.sarif",
                    "--projectRootPaths", "./JavaProjectTemplate"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Project root path must be an absolute path: ./JavaProjectTemplate")));
        });
    }

    @Test
    public void testXMLToSarif_invalidProjectRootPaths_pathContainsOrIsContained() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report 202401.xml",
                    "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report 202401.sarif",
                    "--projectRootPaths", "D:/JavaProjectTemplate; D:/JavaProjectTemplate; D:/JavaProjectTemplate/sub"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.warn("WARNING: Duplicate project root path found: D:/JavaProjectTemplate/"));
            mockedLogger.verify(() -> Logger.error("ERROR: Project path conflict: Path 'D:/JavaProjectTemplate/sub/' contains or is contained by 'D:/JavaProjectTemplate/', which is not supported."));
        });
    }

    private void testWithMockedLogger(Consumer<MockedStatic<Logger>> function) {
        try(MockedStatic<Logger> mockedLogger = Mockito.mockStatic(Logger.class, Mockito.CALLS_REAL_METHODS)) {
            function.accept(mockedLogger);
        }
    }

    private void testXMLToSarif(String xmlFileName, String outputSarifFileName, String expectedSarifFileName, String projectRootPaths, String expectedProjectRootPaths) throws IOException {
        File outputSarifFile = new File(TEST_RESOURCES_LOC, outputSarifFileName != null ? outputSarifFileName : xmlFileName.replaceAll("\\.xml$", ".sarif"));
        File expectedOutputSarifFile = new File(TEST_RESOURCES_LOC, "/../expectedSarif/" + expectedSarifFileName);
        if (!expectedOutputSarifFile.exists()) {
            throw new FileNotFoundException(MessageFormat.format("Expected output SARIF file not found: {0}, please provide it.", expectedOutputSarifFile.getAbsolutePath()));
        }
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);

            ArrayList<String> argList = new ArrayList<>();
            argList.add("--inputXmlReport");
            argList.add(TEST_RESOURCES_LOC + "/" + xmlFileName);
            if (outputSarifFileName != null) {
                argList.add("--outputSarifReport");
                argList.add(TEST_RESOURCES_LOC + "/" + outputSarifFileName);
            }
            if (projectRootPaths != null) {
                argList.add("--projectRootPaths");
                argList.add(projectRootPaths);
            }

            int exitCode = command.execute(argList.toArray(new String[0]));

            assertEquals(0, exitCode);
            assertTrue(outputSarifFile.exists());
            assertTrue(FileUtils.contentEquals(outputSarifFile, expectedOutputSarifFile));
            assertEquals(expectedProjectRootPaths, xml2sarif.getProjectRootPaths());
        } finally {
            if (outputSarifFile.exists()) {
                outputSarifFile.delete();
            }
        }
    }
}
