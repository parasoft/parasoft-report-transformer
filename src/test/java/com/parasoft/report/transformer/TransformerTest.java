package com.parasoft.report.transformer;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;

class TransformerTest {

    @Test
    void testTransformer() {
        Transformer transformer = new Transformer();
        CommandLine command  = new CommandLine(transformer);
        String[] args = {};
        int exitCode = command.execute(args);

        assertEquals(0, exitCode);
    }
}