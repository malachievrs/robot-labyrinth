package com.lab.robot.interpreter.analysis;

import com.lab.robot.interpreter.InterpreterException;
import com.lab.robot.interpreter.ParseTestUtil;
import org.junit.Test;

public class SemanticAnalyzerTest {

    @Test
    public void acceptsValidProgram() throws Exception {
        new SemanticAnalyzer().analyze(ParseTestUtil.parse("seisu a = 1;\n"));
    }

    @Test(expected = InterpreterException.class)
    public void rejectsDuplicateVariable() throws Exception {
        new SemanticAnalyzer().analyze(ParseTestUtil.parse("seisu a = 1;\nseisu a = 2;\n"));
    }

    @Test(expected = InterpreterException.class)
    public void rejectsDuplicateFunction() throws Exception {
        new SemanticAnalyzer().analyze(ParseTestUtil.parse(
                "kansu f() kido return; shushi;\n" +
                "kansu f() kido return; shushi;\n"
        ));
    }

    @Test(expected = InterpreterException.class)
    public void rejectsDuplicateFunctionInNestedBody() throws Exception {
        new SemanticAnalyzer().analyze(ParseTestUtil.parse(
                "kansu outer() kido\n" +
                "  kansu f() kido return; shushi;\n" +
                "shushi;\n" +
                "kansu f() kido return; shushi;\n"
        ));
    }
}
