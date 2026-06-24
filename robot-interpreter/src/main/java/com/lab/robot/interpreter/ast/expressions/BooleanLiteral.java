package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

public class BooleanLiteral implements Expression {
    public final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }
}