package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

public class TypeComparison implements Expression {
    public final Expression left, right;

    public TypeComparison(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
}
