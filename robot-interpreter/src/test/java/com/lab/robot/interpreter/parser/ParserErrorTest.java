package com.lab.robot.interpreter.parser;

import com.lab.robot.interpreter.ParseTestUtil;
import org.junit.Test;

public class ParserErrorTest {

    @Test(expected = Exception.class)
    public void missingSemicolon() throws Exception {
        ParseTestUtil.parse("seisu a = 10");
    }

    @Test(expected = Exception.class)
    public void invalidDeclarationStartsWithNumber() throws Exception {
        ParseTestUtil.parse("seisu 123bad = 10;");
    }

    @Test(expected = Exception.class)
    public void ifWithoutKido() throws Exception {
        ParseTestUtil.parse("sorenara a < 10 { a = 1; } shushi;");
    }

    @Test(expected = Exception.class)
    public void incompleteCellLiteral() throws Exception {
        ParseTestUtil.parse("rippotai c = {1, 2, 3};\n");
    }

    @Test(expected = Exception.class)
    public void robotActionInExpressionWithoutSemicolonInBlock() throws Exception {
        ParseTestUtil.parse("hairetsu bad = { ^_^ o_o; };\n");
    }

    @Test(expected = RuntimeException.class)
    public void illegalCharacterInLexer() throws Exception {
        ParseTestUtil.parse("seisu x = $;\n");
    }
}
