package com.lab.robot.interpreter.lexer;

import com.lab.robot.interpreter.ParseTestUtil;
import com.lab.robot.interpreter.parser.RobotSymbols;
import java_cup.runtime.Symbol;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class LexerTest {

    private static void assertTokens(String code, int... expectedTypes) throws Exception {
        List<Integer> actual = ParseTestUtil.tokenTypes(code);
        Assert.assertEquals(
                "Token types mismatch for: " + code,
                Arrays.asList(box(expectedTypes)),
                actual
        );
    }

    private static void assertTokenValue(String code, int index, int type, Object value) throws Exception {
        List<Symbol> tokens = ParseTestUtil.tokenize(code);
        Assert.assertTrue("Not enough tokens", index < tokens.size());
        Assert.assertEquals(type, tokens.get(index).sym);
        Assert.assertEquals(value, tokens.get(index).value);
    }

    private static Integer[] box(int[] values) {
        Integer[] boxed = new Integer[values.length];
        for (int i = 0; i < values.length; i++) {
            boxed[i] = values[i];
        }
        return boxed;
    }

    @Test
    public void keywordsAndLiterals() throws Exception {
        assertTokens("seisu ronri rippotai hairetsu", RobotSymbols.SEISU, RobotSymbols.RONRI, RobotSymbols.RIPPTAI, RobotSymbols.HAIRETSU);
        assertTokens("shinri uso", RobotSymbols.SHINRI, RobotSymbols.USO);
        assertTokens("shuki kido shushi sorenara kansu return ruikei jigen",
                RobotSymbols.SHUKI, RobotSymbols.KIDO, RobotSymbols.SHUSHI, RobotSymbols.SORENARA,
                RobotSymbols.KANSU, RobotSymbols.RETURN, RobotSymbols.RUIKEI, RobotSymbols.JIGEN);
    }

    @Test
    public void punctuationAndOperators() throws Exception {
        assertTokens("; , : ( ) { } [ ] = + - ~ ^ < > => v",
                RobotSymbols.SEMICOLON, RobotSymbols.COMMA, RobotSymbols.COLON,
                RobotSymbols.LPAREN, RobotSymbols.RPAREN, RobotSymbols.LBRACE, RobotSymbols.RBRACE,
                RobotSymbols.LBRACKET, RobotSymbols.RBRACKET, RobotSymbols.EQ, RobotSymbols.ADD,
                RobotSymbols.SUB, RobotSymbols.NOT, RobotSymbols.CONJUNCT, RobotSymbols.LESS,
                RobotSymbols.GREATER, RobotSymbols.DOT_ACCESS, RobotSymbols.DISJUNCT);
    }

    @Test
    public void integersAndHex() throws Exception {
        assertTokenValue("0", 0, RobotSymbols.INT_LITERAL, 0);
        assertTokenValue("42", 0, RobotSymbols.INT_LITERAL, 42);
        assertTokenValue("xA1F", 0, RobotSymbols.HEX_LITERAL, "xA1F");
        assertTokenValue("xFF", 0, RobotSymbols.HEX_LITERAL, "xFF");
    }

    @Test
    public void identifiers() throws Exception {
        assertTokenValue("myVar", 0, RobotSymbols.IDENTIFIER, "myVar");
        assertTokenValue("cell_1", 0, RobotSymbols.IDENTIFIER, "cell_1");
    }

    @Test
    public void robotMoveTokensBeforeSingleCharOperators() throws Exception {
        assertTokens("^_^", RobotSymbols.MOVE_UP);
        assertTokens("v_v", RobotSymbols.MOVE_DOWN);
        assertTokens("<_<", RobotSymbols.MOVE_LEFT);
        assertTokens(">_<", RobotSymbols.BREAK);
        assertTokens(">_>", RobotSymbols.MOVE_RIGHT);
        assertTokens("o_o", RobotSymbols.MOVE_FORWARD);
        assertTokens("~_~", RobotSymbols.MOVE_BACK);
    }

    @Test
    public void robotMeasureAndPositionTokens() throws Exception {
        assertTokens("^_0", RobotSymbols.MEASURE_UP);
        assertTokens("v_0", RobotSymbols.MEASURE_DOWN);
        assertTokens("<_0", RobotSymbols.MEASURE_LEFT);
        assertTokens(">_0", RobotSymbols.MEASURE_RIGHT);
        assertTokens("o_0", RobotSymbols.MEASURE_FORWARD);
        assertTokens("~_0", RobotSymbols.MEASURE_BACK);
        assertTokens("*_*", RobotSymbols.GETPOSITION);
    }

    @Test
    public void disjunctNotConfusedWithMoveDown() throws Exception {
        assertTokens("shinri v uso", RobotSymbols.SHINRI, RobotSymbols.DISJUNCT, RobotSymbols.USO);
        assertTokens("v_v;", RobotSymbols.MOVE_DOWN, RobotSymbols.SEMICOLON);
    }

    @Test
    public void skipsWhitespaceAndComments() throws Exception {
        assertTokens("seisu\ta\n// comment\n/* block */ b", RobotSymbols.SEISU, RobotSymbols.IDENTIFIER, RobotSymbols.IDENTIFIER);
        assertTokenValue("  x  ", 0, RobotSymbols.IDENTIFIER, "x");
    }

    @Test
    public void fullStatementTokenStream() throws Exception {
        assertTokens("seisu d = ^_0;",
                RobotSymbols.SEISU, RobotSymbols.IDENTIFIER, RobotSymbols.EQ,
                RobotSymbols.MEASURE_UP, RobotSymbols.SEMICOLON);
    }

    @Test(expected = RuntimeException.class)
    public void illegalCharacterThrows() throws Exception {
        ParseTestUtil.tokenize("seisu a = @;");
    }

    @Test
    public void digitPrefixSplitsIntoNumberAndIdentifier() throws Exception {
        assertTokens("123bad", RobotSymbols.INT_LITERAL, RobotSymbols.IDENTIFIER);
        assertTokenValue("123bad", 0, RobotSymbols.INT_LITERAL, 123);
        assertTokenValue("123bad", 1, RobotSymbols.IDENTIFIER, "bad");
    }

    @Test(expected = RuntimeException.class)
    public void identifierCannotStartWithUnderscore() throws Exception {
        ParseTestUtil.tokenize("_hidden");
    }
}
