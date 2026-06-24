package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.ParseTestUtil;
import com.lab.robot.interpreter.ast.Program;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NestedFunctionReturnTest {

    private InterpreterRuntimeTest.RecordingRobotRuntime robot;
    private Interpreter interpreter;

    @Before
    public void setUp() {
        robot = new InterpreterRuntimeTest.RecordingRobotRuntime();
        interpreter = new Interpreter(robot);
    }

    @Test
    public void nestedRecursiveReturnWorks() throws Exception {
        Program program = ParseTestUtil.parse(
                "seisu kansu inner() kido\n" +
                "  return 42;\n" +
                "shushi;\n" +
                "seisu kansu outer() kido\n" +
                "  seisu x = inner();\n" +
                "  return x;\n" +
                "shushi;\n" +
                "seisu r = outer();\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(42, interpreter.getVariable("r").asSeisu().value);
    }
}
