package com.lab.robot.interpreter.parser;

import com.lab.robot.interpreter.ParseTestUtil;
import com.lab.robot.interpreter.ast.Program;
import org.junit.Assert;
import org.junit.Test;

public class ParserTest {

    @Test
    public void testVariableDeclarations() throws Exception {
        String code =
                "seisu a = 10;\n" +
                "seisu hex = xA1F;\n" +
                "ronri b = shinri;\n" +
                "ronri c = uso;\n" +
                "rippotai myCell = {1, 2, 3, uso};\n" +
                "hairetsu arr1 = {10, 20};\n";

        Program program = ParseTestUtil.parse(code);
        Assert.assertNotNull(program);
        Assert.assertEquals(6, program.statements.size());
    }

    @Test
    public void testExpressionsAndAssignments() throws Exception {
        String code =
                "a = 5 + 10 - 2;\n" +
                "b = shinri v uso ^ ~shinri;\n" +
                "c = (a < 10) v (b > 5);\n" +
                "myCell=>x = 5;\n" +
                "arr1[0, 1] = 100;\n" +
                "ronri isSame = ruikei {a b};\n" +
                "seisu dim = jigen arr1;\n";

        Program program = ParseTestUtil.parse(code);
        Assert.assertNotNull(program);
        Assert.assertEquals(7, program.statements.size());
    }

    @Test
    public void testControlStructures() throws Exception {
        String code =
                "sorenara a < 10 kido\n" +
                "  a = a + 1;\n" +
                "shushi;\n" +
                "shuki i = 0 : 10 kido\n" +
                "  arr1[i] = i;\n" +
                "shushi;\n";

        Program program = ParseTestUtil.parse(code);
        Assert.assertNotNull(program);
        Assert.assertEquals(2, program.statements.size());
    }

    @Test
    public void testFunctions() throws Exception {
        String code =
                "seisu kansu calculate(seisu x, ronri flag) kido\n" +
                "  sorenara flag kido\n" +
                "    return x + 1;\n" +
                "  shushi;\n" +
                "  return x - 1;\n" +
                "shushi;\n" +
                "seisu result = calculate(10, shinri);\n";

        Program program = ParseTestUtil.parse(code);
        Assert.assertNotNull(program);
        Assert.assertEquals(2, program.statements.size());
    }

    @Test
    public void testRobotActions() throws Exception {
        String code =
                "^_^;\n" +
                "v_v;\n" +
                "<_<;\n" +
                ">_>;\n" +
                "o_o;\n" +
                "~_~;\n" +
                "seisu d1 = ^_0;\n" +
                "seisu d2 = o_0;\n" +
                "rippotai pos = *_*;\n" +
                "hairetsu robotSeq = { ^_^; >_<; o_o; };\n";

        Program program = ParseTestUtil.parse(code);
        Assert.assertNotNull(program);
        Assert.assertEquals(10, program.statements.size());
    }

    @Test
    public void testComplexLabyrinthAlgorithm() throws Exception {
        String code =
                "seisu kansu solve() kido\n" +
                "  shuki i = 0 : 100 kido\n" +
                "    seisu dist = o_0;\n" +
                "    sorenara dist > 0 kido\n" +
                "      o_o;\n" +
                "    shushi;\n" +
                "  shushi;\n" +
                "  return 0;\n" +
                "shushi;\n" +
                "solve();\n";

        Program program = ParseTestUtil.parse(code);
        Assert.assertNotNull(program);
        Assert.assertEquals(2, program.statements.size());
    }

    @Test
    public void testFunctionWithoutReturnType() throws Exception {
        Program program = ParseTestUtil.parse("kansu doSomething() kido return; shushi;");
        Assert.assertNotNull(program);
        Assert.assertEquals(1, program.statements.size());
    }
}
