package com.lab.robot.interpreter;

import com.lab.robot.interpreter.ast.*;
import com.lab.robot.interpreter.ast.declarations.*;
import com.lab.robot.interpreter.ast.expressions.*;
import com.lab.robot.interpreter.ast.robot.*;
import com.lab.robot.interpreter.ast.statements.*;
import org.junit.Assert;

import java.util.List;

public final class AstAssert {

    private AstAssert() {
    }

    public static void assertInstanceOf(Object node, Class<?> expected) {
        Assert.assertNotNull(node);
        Assert.assertTrue(
                "Expected " + expected.getSimpleName() + " but got " + node.getClass().getSimpleName(),
                expected.isInstance(node)
        );
    }

    public static IntegerLiteral assertIntLiteral(Expression expr, int value) {
        assertInstanceOf(expr, IntegerLiteral.class);
        IntegerLiteral literal = (IntegerLiteral) expr;
        Assert.assertEquals(value, literal.value);
        return literal;
    }

    public static BooleanLiteral assertBoolLiteral(Expression expr, boolean value) {
        assertInstanceOf(expr, BooleanLiteral.class);
        BooleanLiteral literal = (BooleanLiteral) expr;
        Assert.assertEquals(value, literal.value);
        return literal;
    }

    public static VarAccess assertVarAccess(Expression expr, String name) {
        assertInstanceOf(expr, VarAccess.class);
        VarAccess access = (VarAccess) expr;
        Assert.assertEquals(name, access.name);
        return access;
    }

    public static BinaryOp assertBinaryOp(Expression expr, Operator op) {
        assertInstanceOf(expr, BinaryOp.class);
        BinaryOp binary = (BinaryOp) expr;
        Assert.assertEquals(op, binary.operator);
        return binary;
    }

    public static UnaryOp assertUnaryOp(Expression expr, Operator op) {
        assertInstanceOf(expr, UnaryOp.class);
        UnaryOp unary = (UnaryOp) expr;
        Assert.assertEquals(op, unary.operator);
        return unary;
    }

    public static Assignment assertAssignment(Statement stmt) {
        if (stmt instanceof ExpressionStatement) {
            Expression expr = ((ExpressionStatement) stmt).expr;
            assertInstanceOf(expr, Assignment.class);
            return (Assignment) expr;
        }
        assertInstanceOf(stmt, Assignment.class);
        return (Assignment) stmt;
    }

    public static ExpressionStatement assertExprStatement(Statement stmt) {
        assertInstanceOf(stmt, ExpressionStatement.class);
        return (ExpressionStatement) stmt;
    }

    public static VarDecl assertVarDecl(Statement stmt, Type type, String name) {
        assertInstanceOf(stmt, VarDecl.class);
        VarDecl decl = (VarDecl) stmt;
        Assert.assertEquals(type, decl.type);
        Assert.assertEquals(name, decl.name);
        return decl;
    }

    public static CellDecl assertCellDecl(Statement stmt, String name) {
        assertInstanceOf(stmt, CellDecl.class);
        CellDecl decl = (CellDecl) stmt;
        Assert.assertEquals(name, decl.name);
        return decl;
    }

    public static ArrayDecl assertArrayDecl(Statement stmt, String name) {
        assertInstanceOf(stmt, ArrayDecl.class);
        ArrayDecl decl = (ArrayDecl) stmt;
        Assert.assertEquals(name, decl.name);
        return decl;
    }

    public static FunctionDecl assertFunctionDecl(Statement stmt, String name) {
        assertInstanceOf(stmt, FunctionDecl.class);
        FunctionDecl decl = (FunctionDecl) stmt;
        Assert.assertEquals(name, decl.name);
        return decl;
    }

    public static FunctionCall assertFunctionCall(Expression expr, String name, int argCount) {
        assertInstanceOf(expr, FunctionCall.class);
        FunctionCall call = (FunctionCall) expr;
        Assert.assertEquals(name, call.name);
        Assert.assertEquals(argCount, call.args.size());
        return call;
    }

    public static MoveAction assertMoveAction(Statement stmt, Direction direction) {
        assertInstanceOf(stmt, MoveAction.class);
        MoveAction action = (MoveAction) stmt;
        Assert.assertEquals(direction, action.direction);
        return action;
    }

    public static MeasureExpr assertMeasureExpr(Expression expr, Direction direction) {
        assertInstanceOf(expr, MeasureExpr.class);
        MeasureExpr measure = (MeasureExpr) expr;
        Assert.assertEquals(direction, measure.direction);
        return measure;
    }

    public static void assertStatementOrder(Program program, Class<?>... expectedTypes) {
        Assert.assertEquals(expectedTypes.length, program.statements.size());
        for (int i = 0; i < expectedTypes.length; i++) {
            assertInstanceOf(program.statements.get(i), expectedTypes[i]);
        }
    }

    public static void assertRobotSequence(List<RobotAction> actions, Class<?>... expectedTypes) {
        Assert.assertEquals(expectedTypes.length, actions.size());
        for (int i = 0; i < expectedTypes.length; i++) {
            assertInstanceOf(actions.get(i), expectedTypes[i]);
        }
    }
}
