package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.ParseTestUtil;
import com.lab.robot.interpreter.ast.Program;
import com.lab.robot.interpreter.ast.Type;
import com.lab.robot.interpreter.ast.declarations.VarDecl;
import org.junit.Assert;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class InterpreterRuntimeTest {

    private RecordingRobotRuntime robot;
    private Interpreter interpreter;

    @Before
    public void setUp() {
        robot = new RecordingRobotRuntime();
        interpreter = new Interpreter(robot);
    }

    @Test
    public void evaluatesArithmeticAndVariables() throws Exception {
        Program program = ParseTestUtil.parse("seisu a = 10;\nseisu b = a + 5;\n");
        interpreter.execute(program);
        VarDecl b = (VarDecl) program.statements.get(1);
        SeisuValue val = (SeisuValue) interpreter.evaluateExpression(
                new com.lab.robot.interpreter.ast.expressions.VarAccess(b.name));
        Assert.assertEquals(15, val.value);
    }

    @Test
    public void logicalCoercion() throws Exception {
        Program program = ParseTestUtil.parse("ronri t = shinri;\nseisu n = t;\n");
        interpreter.execute(program);
        Assert.assertEquals(1, interpreter.getVariable("n").asSeisu().value);
    }

    @Test
    public void executesMoveStatement() throws Exception {
        Program program = ParseTestUtil.parse("^_^;\n");
        interpreter.execute(program);
        Assert.assertEquals(1, robot.moveCount);
    }

    @Test
    public void ifStatementSkipsBody() throws Exception {
        Program program = ParseTestUtil.parse(
                "seisu a = 0;\n" +
                "sorenara a > 0 kido\n" +
                "  ^_^;\n" +
                "shushi;\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(0, robot.moveCount);
    }

    @Test
    public void functionCallReturnsValue() throws Exception {
        Program program = ParseTestUtil.parse(
                "seisu kansu inc(seisu x) kido\n" +
                "  return x + 1;\n" +
                "shushi;\n" +
                "seisu r = inc(41);\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(42, interpreter.getVariable("r").asSeisu().value);
    }

    @Test
    public void functionDeclaredAfterCall() throws Exception {
        Program program = ParseTestUtil.parse(
                "seisu r = inc(41);\n" +
                "seisu kansu inc(seisu x) kido\n" +
                "  return x + 1;\n" +
                "shushi;\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(42, interpreter.getVariable("r").asSeisu().value);
    }

    @Test
    public void functionDeclaredInsideAnotherFunction() throws Exception {
        Program program = ParseTestUtil.parse(
                "seisu kansu outer() kido\n" +
                "  seisu kansu inner() kido\n" +
                "    return 42;\n" +
                "  shushi;\n" +
                "  return inner();\n" +
                "shushi;\n" +
                "seisu r = outer();\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(42, interpreter.getVariable("r").asSeisu().value);
    }

    @Test
    public void threeDimensionalArrayReadWrite() throws Exception {
        Program program = ParseTestUtil.parse(
                "hairetsu cube = {2, 3, 4};\n" +
                "cube[1, 2, 3] = 7;\n" +
                "seisu x = cube[1, 2, 3];\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(7, interpreter.getVariable("x").asSeisu().value);
    }

    @Test
    public void twoDimensionalArrayReadWrite() throws Exception {
        Program program = ParseTestUtil.parse(
                "hairetsu grid = {3, 4};\n" +
                "grid[2, 1] = 11;\n" +
                "seisu x = grid[2, 1];\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(11, interpreter.getVariable("x").asSeisu().value);
    }

    @Test
    public void functionDeclaredInsideIfIsRegisteredWithoutRunningIf() throws Exception {
        Program program = ParseTestUtil.parse(
                "sorenara uso kido\n" +
                "  seisu kansu helper() kido\n" +
                "    return 99;\n" +
                "  shushi;\n" +
                "shushi;\n" +
                "seisu r = helper();\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(99, interpreter.getVariable("r").asSeisu().value);
    }

    @Test
    public void rippotaiLiteralAndAccess() throws Exception {
        Program program = ParseTestUtil.parse(
                "rippotai c = {1, 2, 3, uso};\n" +
                "seisu x = c=>x;\n"
        );
        interpreter.execute(program);
        Assert.assertEquals(1, interpreter.getVariable("x").asSeisu().value);
    }

    static final class RecordingRobotRuntime extends RobotRuntime {
        int moveCount;
        int measureCount;

        RecordingRobotRuntime() {
            super(null);
        }

        @Override
        public void move(com.lab.robot.interpreter.ast.Direction direction) {
            moveCount++;
        }

        @Override
        public int measure(com.lab.robot.interpreter.ast.Direction direction) {
            measureCount++;
            return 5;
        }

        @Override
        public RipotaiValue getPosition() {
            return new RipotaiValue(0, 0, 0, false, false);
        }

        @Override
        public boolean isBroken() {
            return false;
        }
    }
}
