package com.lab.robot.interpreter.parser;

import com.lab.robot.interpreter.ParseTestUtil;
import com.lab.robot.interpreter.lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

public class ParseDiagnosticTest {

    @Test
    public void threeDimDeclarationAndMultiIndexAccessParse() throws Exception {
        ParseTestUtil.parse(
                "hairetsu arr = {2, 3, 4};\n" +
                "arr[1, 2, 3] = 7;\n" +
                "seisu x = arr[1, 2, 3];\n"
        );
    }

    @Test
    public void singleLetterVIsDisjunctNotVariableName() throws Exception {
        Lexer lexer = new Lexer(new StringReader("seisu v = 1;\n"));
        Assert.assertEquals(RobotSymbols.SEISU, lexer.next_token().sym);
        Assert.assertEquals(RobotSymbols.DISJUNCT, lexer.next_token().sym);
    }

    @Test(expected = Exception.class)
    public void programWithVAsVariableNameDoesNotParse() throws Exception {
        ParseTestUtil.parse("seisu v = 1;\n");
    }
}
