package com.lab.robot.interpreter.ast.statements;

import com.lab.robot.interpreter.ast.*;

public class ReturnStatement implements Statement {
    public final Expression expr;

    public ReturnStatement(Expression expr) {
        this.expr = expr;
    }
}
