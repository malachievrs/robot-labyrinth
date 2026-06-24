package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

public class BinaryOp implements Expression {
    public final Operator operator;
    public final Expression left, right;

    public BinaryOp(Operator operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
}
