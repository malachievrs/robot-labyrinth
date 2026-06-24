package com.lab.robot.interpreter.parser;

import com.lab.robot.interpreter.ParsePipeline;
import com.lab.robot.interpreter.ast.Program;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;

public class SolveProgramParseTest {

    @Test
    public void defaultSolveProgramParses() throws Exception {
        Path path = Path.of("src/main/resources/programs/solve.robot");
        if (!path.toFile().exists()) {
            path = Path.of("robot-interpreter/src/main/resources/programs/solve.robot");
        }
        Program program = ParsePipeline.parseFile(path);
        Assert.assertFalse(program.statements.isEmpty());
    }
}
