package com.lab.robot.interpreter.ast.statements;

import com.lab.robot.interpreter.ast.*;

public class ExpressionStatement implements Statement {
    public final Expression expr;

    public ExpressionStatement(Expression expr) {
        this.expr = expr;
    }
}
