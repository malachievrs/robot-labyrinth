package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

public class UnaryOp implements Expression {
    public final Operator operator;
    public final Expression expr;

    public UnaryOp(Operator operator, Expression expr) {
        this.operator = operator;
        this.expr = expr;
    }
}
