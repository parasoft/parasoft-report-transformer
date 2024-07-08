package com.parasoft.report.transformer;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.startsWith;

public class XMLToSarifTest {

    private static final String TEST_RESOURCES_LOC = "src/test/resources/com/parasoft/report/transformer/XMLToSarifTest/xml";

    @Test
    public void testXMLToSarif_jtest202402_normal_1() throws IOException {
        String xmlFileName = "jtest-report-202401.xml";
        String sarifFileName = "jtest-report-202401.sarif";
        File outputSarifFile = new File(TEST_RESOURCES_LOC, sarifFileName);
        File expectedOutputSarifFile = new File(TEST_RESOURCES_LOC, "/../expectedSarif/" + sarifFileName);
        assertTrue(expectedOutputSarifFile.exists());
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/" + xmlFileName,
                             "--outputSarifReport", TEST_RESOURCES_LOC + "/" + sarifFileName,
                             "--projectRootPaths", "D:/JavaProjectTemplate/"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
            assertTrue(outputSarifFile.exists());
            assertTrue(FileUtils.contentEquals(outputSarifFile, expectedOutputSarifFile));
        } finally {
            if (outputSarifFile.exists()) {
                outputSarifFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_2() throws IOException {
        String xmlFileName = "jtest-report 202401.xml";
        String sarifFileName = "jtest-report 202401.sarif";
        File outputSarifFile = new File(TEST_RESOURCES_LOC, sarifFileName);
        File expectedOutputSarifFile = new File(TEST_RESOURCES_LOC, "/../expectedSarif/jtest-report-202401.sarif");
        assertTrue(expectedOutputSarifFile.exists());
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/" + xmlFileName,
                    "--outputSarifReport", TEST_RESOURCES_LOC + "/" + sarifFileName,
                    "--projectRootPaths", "D:\\JavaProjectTemplate"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
            assertTrue(outputSarifFile.exists());
            assertTrue(FileUtils.contentEquals(outputSarifFile, expectedOutputSarifFile));
        } finally {
            if (outputSarifFile.exists()) {
                outputSarifFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_noOutputSarifReportParam() throws IOException {
        String xmlFileName = "jtest-report-202401.xml";
        String sarifFileName = "jtest-report-202401.sarif";
        File outputSarifFile = new File(TEST_RESOURCES_LOC, sarifFileName);
        File expectedOutputSarifFile = new File(TEST_RESOURCES_LOC, "/../expectedSarif/jtest-report-202401.sarif");
        assertTrue(expectedOutputSarifFile.exists());
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/" + xmlFileName,
                             "--projectRootPaths", "D:/JavaProjectTemplate"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
            assertTrue(outputSarifFile.exists());
            assertTrue(FileUtils.contentEquals(outputSarifFile, expectedOutputSarifFile));
        } finally {
            if (outputSarifFile.exists()) {
                outputSarifFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_noProjectRootPathsParam() throws IOException {
        String xmlFileName = "jtest-report-202401.xml";
        String sarifFileName = "jtest-report-202401-1.sarif";
        File outputSarifFile = new File(TEST_RESOURCES_LOC, sarifFileName);
        File expectedOutputSarifFile = new File(TEST_RESOURCES_LOC, "/../expectedSarif/jtest-report-202401-1.sarif");
        assertTrue(expectedOutputSarifFile.exists());
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/" + xmlFileName,
                             "--outputSarifReport", TEST_RESOURCES_LOC + "/" + sarifFileName};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
            assertTrue(outputSarifFile.exists());
            assertTrue(FileUtils.contentEquals(outputSarifFile, expectedOutputSarifFile));
        } finally {
            if (outputSarifFile.exists()) {
                outputSarifFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_multipleProjectRootPaths() throws IOException {
        String xmlFileName = "jtest-report-202401.xml";
        String sarifFileName = "jtest-report-202401-2.sarif";
        File outputSarifFile = new File(TEST_RESOURCES_LOC, sarifFileName);
        File expectedOutputSarifFile = new File(TEST_RESOURCES_LOC, "/../expectedSarif/" + sarifFileName);
        assertTrue(expectedOutputSarifFile.exists());
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/" + xmlFileName,
                             "--outputSarifReport", TEST_RESOURCES_LOC + "/" + sarifFileName,
                             "--projectRootPaths", "D:/JavaProjectTemplate/, D:/JavaProjectTemplate-1, D:\\JavaProjectTemplate-2"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
            assertTrue(outputSarifFile.exists());
            assertTrue(FileUtils.contentEquals(outputSarifFile, expectedOutputSarifFile));
            assertEquals("D:/JavaProjectTemplate/,D:/JavaProjectTemplate-1,D:/JavaProjectTemplate-2", xml2sarif.getProjectRootPaths());
        } finally {
            if (outputSarifFile.exists()) {
                outputSarifFile.delete();
            }
        }
    }

    @Test
    public void testXMLToSarif_jtest202402_normal_projectRootPathsCanNotBeMatched() throws IOException {
        String xmlFileName = "jtest-report-202401.xml";
        String sarifFileName = "jtest-report-202401-3.sarif";
        File outputSarifFile = new File(TEST_RESOURCES_LOC, sarifFileName);
        File expectedOutputSarifFile = new File(TEST_RESOURCES_LOC, "/../expectedSarif/" + sarifFileName);
        assertTrue(expectedOutputSarifFile.exists());
        try {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/" + xmlFileName,
                    "--outputSarifReport", TEST_RESOURCES_LOC + "/" + sarifFileName,
                    "--projectRootPaths", "/JavaProjectTemplate/, D:/JavaProjectTemplate-1"};
            int exitCode = command.execute(args);

            assertEquals(0, exitCode);
            assertTrue(outputSarifFile.exists());
            assertTrue(FileUtils.contentEquals(outputSarifFile, expectedOutputSarifFile));
            assertEquals("/JavaProjectTemplate/,D:/JavaProjectTemplate-1", xml2sarif.getProjectRootPaths());
        } finally {
            if (outputSarifFile.exists()) {
                outputSarifFile.delete();
            }
        }
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
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Input XML report file does not exist:")));
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
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Input XML report is not a file:")));
            mockedLogger.verify(() -> Logger.error(endsWith("xml.")));
        });
    }

    @Test
    public void testXMLToSarif_outputSarifReportIsNotSarif() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                             "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report-202401.notEndWithSarif"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Output SARIF report must have .sarif extension:")));
            mockedLogger.verify(() -> Logger.error(endsWith("jtest-report-202401.notEndWithSarif.")));
        });
    }

    @Test
    public void testXMLToSarif_invalidOutputSarifReportName() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report-202401.xml",
                             "--outputSarifReport", TEST_RESOURCES_LOC + "/invalid-sarif-report-><|.sarif"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Transformation error: Failed to create output file file:")));
        });
    }

    @Test
    public void testXMLToSarif_invalidProjectRootPaths_duplicatePath() {
        testWithMockedLogger(mockedLogger -> {
            XMLToSarif xml2sarif = new XMLToSarif();
            CommandLine command  = new CommandLine(xml2sarif);
            String[] args = {"--inputXmlReport", TEST_RESOURCES_LOC + "/jtest-report 202401.xml",
                             "--outputSarifReport", TEST_RESOURCES_LOC + "/jtest-report 202401.sarif",
                             "--projectRootPaths", "D:\\JavaProjectTemplate, D:/JavaProjectTemplate-1, D:/JavaProjectTemplate-1"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Duplicate project root path found: D:/JavaProjectTemplate-1/")));
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
                    "--projectRootPaths", "D:/JavaProjectTemplate, D:/JavaProjectTemplate/sub"};
            int exitCode = command.execute(args);

            assertEquals(1, exitCode);
            mockedLogger.verify(() -> Logger.error(startsWith("ERROR: Project path contains or is contained by another path: D:/JavaProjectTemplate/sub/ and D:/JavaProjectTemplate/")));
        });
    }

    private void testWithMockedLogger(Consumer<MockedStatic<Logger>> function) {
        try(MockedStatic<Logger> mockedLogger = Mockito.mockStatic(Logger.class, Mockito.CALLS_REAL_METHODS)) {
            function.accept(mockedLogger);
        }
    }
}
