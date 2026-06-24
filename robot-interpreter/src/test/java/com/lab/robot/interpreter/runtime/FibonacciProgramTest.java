package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.ParsePipeline;
import com.lab.robot.interpreter.ParseTestUtil;
import com.lab.robot.interpreter.ast.Program;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

public class FibonacciProgramTest {

    private static final String FIB_PROGRAM =
            "seisu kansu fib(seisu n) kido\n" +
            "  sorenara n < 2 kido\n" +
            "    return n;\n" +
            "  shushi;\n" +
            "  return fib(n - 1) + fib(n - 2);\n" +
            "shushi;\n";

    private InterpreterRuntimeTest.RecordingRobotRuntime robot;
    private Interpreter interpreter;

    @Before
    public void setUp() {
        robot = new InterpreterRuntimeTest.RecordingRobotRuntime();
        interpreter = new Interpreter(robot);
    }

    @Test
    public void fib0() throws Exception {
        assertFib(0, 0);
    }

    @Test
    public void fib1() throws Exception {
        assertFib(1, 1);
    }

    @Test
    public void fib5() throws Exception {
        assertFib(5, 5);
    }

    @Test
    public void fib10() throws Exception {
        assertFib(10, 55);
    }

    private void assertFib(int n, int expected) throws Exception {
        Program program = ParseTestUtil.parse(
                FIB_PROGRAM + "seisu result = fib(" + n + ");\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(expected, interpreter.getVariable("result").asSeisu().value);
    }
}
