package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

import java.util.List;

public class ArrayElementAccess implements Expression {
    public final Expression array;
    public final List<Expression> indices;

    public ArrayElementAccess(Expression array, List<Expression> indices) {
        this.array = array;
        this.indices = indices;
    }
}