package com.lab.robot.interpreter.parser;

import com.lab.robot.interpreter.AstAssert;
import com.lab.robot.interpreter.ParseTestUtil;
import com.lab.robot.interpreter.ast.*;
import com.lab.robot.interpreter.ast.declarations.*;
import com.lab.robot.interpreter.ast.expressions.*;
import com.lab.robot.interpreter.ast.robot.*;
import com.lab.robot.interpreter.ast.statements.*;
import org.junit.Assert;
import org.junit.Test;

public class ParserAstTest {

    @Test
    public void emptyProgram() throws Exception {
        Program program = ParseTestUtil.parse("");
        Assert.assertNotNull(program);
        Assert.assertTrue(program.statements.isEmpty());
    }

    @Test
    public void statementOrderPreserved() throws Exception {
        Program program = ParseTestUtil.parse("seisu a = 1;\nseisu b = 2;\nseisu c = 3;\n");
        AstAssert.assertStatementOrder(program, VarDecl.class, VarDecl.class, VarDecl.class);
        AstAssert.assertVarDecl(program.statements.get(0), Type.SEISU, "a");
        AstAssert.assertVarDecl(program.statements.get(1), Type.SEISU, "b");
        AstAssert.assertVarDecl(program.statements.get(2), Type.SEISU, "c");
    }

    @Test
    public void seisuDeclarationWithDecimalAndHex() throws Exception {
        Program program = ParseTestUtil.parse("seisu a = 10;\nseisu hex = xA1F;\n");
        VarDecl a = AstAssert.assertVarDecl(program.statements.get(0), Type.SEISU, "a");
        AstAssert.assertIntLiteral(a.init, 10);

        VarDecl hex = AstAssert.assertVarDecl(program.statements.get(1), Type.SEISU, "hex");
        AstAssert.assertIntLiteral(hex.init, 0xA1F);
    }

    @Test
    public void ronriDeclarationAndLogicalLiterals() throws Exception {
        Program program = ParseTestUtil.parse("ronri t = shinri;\nronri f = uso;\n");
        AstAssert.assertBoolLiteral(AstAssert.assertVarDecl(program.statements.get(0), Type.RONRI, "t").init, true);
        AstAssert.assertBoolLiteral(AstAssert.assertVarDecl(program.statements.get(1), Type.RONRI, "f").init, false);
    }

    @Test
    public void rippotaiCellLiteralDeclaration() throws Exception {
        Program program = ParseTestUtil.parse("rippotai myCell = {1, 2, 3, uso};\n");
        CellDecl cell = AstAssert.assertCellDecl(program.statements.get(0), "myCell");
        AstAssert.assertIntLiteral(cell.x, 1);
        AstAssert.assertIntLiteral(cell.y, 2);
        AstAssert.assertIntLiteral(cell.z, 3);
        AstAssert.assertBoolLiteral(cell.obstacle, false);
    }

    @Test
    public void rippotaiPositionExpressionDeclaration() throws Exception {
        Program program = ParseTestUtil.parse("rippotai pos = *_*;\n");
        VarDecl pos = AstAssert.assertVarDecl(program.statements.get(0), Type.RIPPTAI, "pos");
        AstAssert.assertInstanceOf(pos.init, GetPositionExpr.class);
    }

    @Test
    public void hairetsuDimensionDeclaration() throws Exception {
        Program program = ParseTestUtil.parse("hairetsu arr = {10, 20, 30};\n");
        ArrayDecl arr = AstAssert.assertArrayDecl(program.statements.get(0), "arr");
        Assert.assertNull(arr.robotActions);
        Assert.assertEquals(3, arr.dimensions.size());
        AstAssert.assertIntLiteral(arr.dimensions.get(0), 10);
        AstAssert.assertIntLiteral(arr.dimensions.get(1), 20);
        AstAssert.assertIntLiteral(arr.dimensions.get(2), 30);
    }

    @Test
    public void hairetsuRobotSequenceDeclaration() throws Exception {
        Program program = ParseTestUtil.parse("hairetsu robotSeq = { ^_^; >_<; o_o; };\n");
        VarDecl decl = AstAssert.assertVarDecl(program.statements.get(0), Type.HAIRETSU, "robotSeq");
        RobotSequenceExpr seq = (RobotSequenceExpr) decl.init;
        AstAssert.assertRobotSequence(seq.actions, MoveAction.class, BreakAction.class, MoveAction.class);
    }

    @Test
    public void arithmeticExpressionPrecedence() throws Exception {
        Program program = ParseTestUtil.parse("seisu r = 5 + 10 - 2;\n");
        VarDecl decl = AstAssert.assertVarDecl(program.statements.get(0), Type.SEISU, "r");
        BinaryOp sub = AstAssert.assertBinaryOp(decl.init, Operator.SUB);
        AstAssert.assertBinaryOp(sub.left, Operator.ADD);
        AstAssert.assertIntLiteral(sub.right, 2);
    }

    @Test
    public void logicalExpressionStructure() throws Exception {
        Program program = ParseTestUtil.parse("ronri b = shinri v uso ^ ~shinri;\n");
        VarDecl decl = AstAssert.assertVarDecl(program.statements.get(0), Type.RONRI, "b");
        BinaryOp or = AstAssert.assertBinaryOp(decl.init, Operator.OR);
        AstAssert.assertBoolLiteral(or.left, true);
        BinaryOp and = AstAssert.assertBinaryOp(or.right, Operator.AND);
        AstAssert.assertBoolLiteral(and.left, false);
        AstAssert.assertUnaryOp(and.right, Operator.NOT);
    }

    @Test
    public void comparisonAndParentheses() throws Exception {
        Program program = ParseTestUtil.parse("ronri c = (a < 10) v (b > 5);\n");
        VarDecl decl = AstAssert.assertVarDecl(program.statements.get(0), Type.RONRI, "c");
        BinaryOp or = AstAssert.assertBinaryOp(decl.init, Operator.OR);
        AstAssert.assertBinaryOp(or.left, Operator.LESS);
        AstAssert.assertBinaryOp(or.right, Operator.GREATER);
    }

    @Test
    public void assignmentExpressions() throws Exception {
        Program program = ParseTestUtil.parse("a = 5;\nmyCell=>x = 5;\narr[0, 1] = 100;\n");
        Assignment a = AstAssert.assertAssignment(program.statements.get(0));
        AstAssert.assertVarAccess(a.lhs, "a");
        AstAssert.assertIntLiteral(a.rhs, 5);

        Assignment cell = AstAssert.assertAssignment(program.statements.get(1));
        AstAssert.assertInstanceOf(cell.lhs, CellPropertyAccess.class);
        Assert.assertEquals("x", ((CellPropertyAccess) cell.lhs).propertyName);

        Assignment arr = AstAssert.assertAssignment(program.statements.get(2));
        ArrayElementAccess access = (ArrayElementAccess) arr.lhs;
        AstAssert.assertVarAccess(access.array, "arr");
        Assert.assertEquals(2, access.indices.size());
        AstAssert.assertIntLiteral(access.indices.get(0), 0);
        AstAssert.assertIntLiteral(access.indices.get(1), 1);
    }

    @Test
    public void ruikeiAndJigen() throws Exception {
        Program program = ParseTestUtil.parse("ronri same = ruikei {a b};\nseisu dim = jigen arr;\n");
        VarDecl same = AstAssert.assertVarDecl(program.statements.get(0), Type.RONRI, "same");
        TypeComparison cmp = (TypeComparison) same.init;
        AstAssert.assertVarAccess(cmp.left, "a");
        AstAssert.assertVarAccess(cmp.right, "b");

        VarDecl dim = AstAssert.assertVarDecl(program.statements.get(1), Type.SEISU, "dim");
        ArrayDimension arrayDim = (ArrayDimension) dim.init;
        AstAssert.assertVarAccess(arrayDim.array, "arr");
    }

    @Test
    public void unaryNegation() throws Exception {
        Program program = ParseTestUtil.parse("seisu n = -5;\n");
        UnaryOp neg = AstAssert.assertUnaryOp(AstAssert.assertVarDecl(program.statements.get(0), Type.SEISU, "n").init, Operator.NEG);
        AstAssert.assertIntLiteral(neg.expr, 5);
    }

    @Test
    public void ifStatementAst() throws Exception {
        Program program = ParseTestUtil.parse(
                "sorenara a < 10 kido\n" +
                "  a = a + 1;\n" +
                "shushi;\n"
        );
        IfStatement ifStmt = (IfStatement) program.statements.get(0);
        AstAssert.assertBinaryOp(ifStmt.condition, Operator.LESS);
        Assert.assertEquals(1, ifStmt.body.size());
        AstAssert.assertAssignment(ifStmt.body.get(0));
    }

    @Test
    public void loopStatementAst() throws Exception {
        Program program = ParseTestUtil.parse(
                "shuki i = 0 : 10 kido\n" +
                "  arr[i] = i;\n" +
                "shushi;\n"
        );
        LoopStatement loop = (LoopStatement) program.statements.get(0);
        Assert.assertEquals("i", loop.iteratorName);
        AstAssert.assertIntLiteral(loop.start, 0);
        AstAssert.assertIntLiteral(loop.end, 10);
        Assert.assertEquals(1, loop.body.size());
    }

    @Test
    public void functionDeclarationAndCall() throws Exception {
        Program program = ParseTestUtil.parse(
                "seisu kansu calculate(seisu x, ronri flag) kido\n" +
                "  return x + 1;\n" +
                "shushi;\n" +
                "seisu result = calculate(10, shinri);\n"
        );
        FunctionDecl fn = AstAssert.assertFunctionDecl(program.statements.get(0), "calculate");
        Assert.assertEquals(Type.SEISU, fn.returnType);
        Assert.assertEquals(2, fn.params.size());
        Assert.assertEquals("x", fn.params.get(0).name);
        Assert.assertEquals(Type.RONRI, fn.params.get(1).type);

        VarDecl result = AstAssert.assertVarDecl(program.statements.get(1), Type.SEISU, "result");
        FunctionCall call = AstAssert.assertFunctionCall(result.init, "calculate", 2);
        AstAssert.assertIntLiteral(call.args.get(0), 10);
        AstAssert.assertBoolLiteral(call.args.get(1), true);
    }

    @Test
    public void functionWithoutReturnTypeAndVoidReturn() throws Exception {
        Program program = ParseTestUtil.parse("kansu doSomething() kido return; shushi;\n");
        FunctionDecl fn = AstAssert.assertFunctionDecl(program.statements.get(0), "doSomething");
        Assert.assertNull(fn.returnType);
        ReturnStatement ret = (ReturnStatement) fn.body.get(0);
        Assert.assertNull(ret.expr);
    }

    @Test
    public void robotMoveStatements() throws Exception {
        Program program = ParseTestUtil.parse("^_^;\nv_v;\n>_<;\n");
        AstAssert.assertMoveAction(program.statements.get(0), Direction.UP);
        AstAssert.assertMoveAction(program.statements.get(1), Direction.DOWN);
        AstAssert.assertInstanceOf(program.statements.get(2), BreakAction.class);
    }

    @Test
    public void measureAndGetPositionInExpression() throws Exception {
        Program program = ParseTestUtil.parse("seisu up = ^_0;\nseisu fwd = o_0;\nrippotai here = *_*;\n");
        AstAssert.assertMeasureExpr(AstAssert.assertVarDecl(program.statements.get(0), Type.SEISU, "up").init, Direction.UP);
        AstAssert.assertMeasureExpr(AstAssert.assertVarDecl(program.statements.get(1), Type.SEISU, "fwd").init, Direction.FORWARD);
        AstAssert.assertInstanceOf(AstAssert.assertVarDecl(program.statements.get(2), Type.RIPPTAI, "here").init, GetPositionExpr.class);
    }

    @Test
    public void expressionStatementFunctionCall() throws Exception {
        Program program = ParseTestUtil.parse("solve();\n");
        ExpressionStatement stmt = AstAssert.assertExprStatement(program.statements.get(0));
        AstAssert.assertFunctionCall(stmt.expr, "solve", 0);
    }

    @Test
    public void robotSequenceExprInAssignment() throws Exception {
        Program program = ParseTestUtil.parse("hairetsu path = { ^_^; o_o; };\n");
        RobotSequenceExpr seq = (RobotSequenceExpr) AstAssert.assertVarDecl(program.statements.get(0), Type.HAIRETSU, "path").init;
        AstAssert.assertRobotSequence(seq.actions, MoveAction.class, MoveAction.class);
    }

    @Test
    public void robotSequenceExprAsScalarInitializer() throws Exception {
        Program program = ParseTestUtil.parse("seisu block = { ^_^; >_<; };\n");
        RobotSequenceExpr seq = (RobotSequenceExpr) AstAssert.assertVarDecl(program.statements.get(0), Type.SEISU, "block").init;
        AstAssert.assertRobotSequence(seq.actions, MoveAction.class, BreakAction.class);
    }

    @Test
    public void fullRobotActionsProgramAst() throws Exception {
        Program program = ParseTestUtil.parse(
                "^_^;\nv_v;\n<_<;\n>_>;\no_o;\n~_~;\n" +
                "seisu d1 = ^_0;\nseisu d2 = o_0;\n" +
                "rippotai pos = *_*;\n" +
                "hairetsu robotSeq = { ^_^; >_<; o_o; };\n"
        );
        Assert.assertEquals(10, program.statements.size());
        AstAssert.assertMoveAction(program.statements.get(0), Direction.UP);
        AstAssert.assertMoveAction(program.statements.get(5), Direction.BACK);
        AstAssert.assertMeasureExpr(AstAssert.assertVarDecl(program.statements.get(6), Type.SEISU, "d1").init, Direction.UP);
        AstAssert.assertInstanceOf(AstAssert.assertVarDecl(program.statements.get(8), Type.RIPPTAI, "pos").init, GetPositionExpr.class);
        RobotSequenceExpr seq = (RobotSequenceExpr) AstAssert.assertVarDecl(program.statements.get(9), Type.HAIRETSU, "robotSeq").init;
        AstAssert.assertRobotSequence(seq.actions, MoveAction.class, BreakAction.class, MoveAction.class);
    }

    @Test
    public void commentsDoNotAffectParse() throws Exception {
        Program program = ParseTestUtil.parse("// leading\nseisu n = 1; /* tail */");
        AstAssert.assertIntLiteral(AstAssert.assertVarDecl(program.statements.get(0), Type.SEISU, "n").init, 1);
    }

    @Test
    public void ruikeiWithTypeNames() throws Exception {
        Program program = ParseTestUtil.parse("ronri t = ruikei {seisu ronri};\n");
        TypeComparison cmp = (TypeComparison) AstAssert.assertVarDecl(program.statements.get(0), Type.RONRI, "t").init;
        AstAssert.assertVarAccess(cmp.left, Type.SEISU.name());
        AstAssert.assertVarAccess(cmp.right, Type.RONRI.name());
    }

    @Test
    public void functionArgumentOrder() throws Exception {
        Program program = ParseTestUtil.parse("f(1, 2, 3);\n");
        FunctionCall call = AstAssert.assertFunctionCall(AstAssert.assertExprStatement(program.statements.get(0)).expr, "f", 3);
        AstAssert.assertIntLiteral(call.args.get(0), 1);
        AstAssert.assertIntLiteral(call.args.get(1), 2);
        AstAssert.assertIntLiteral(call.args.get(2), 3);
    }
}
