package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

public class ArrayDimension implements Expression {
    public final Expression array;

    public ArrayDimension(Expression array) {
        this.array = array;
    }
}
