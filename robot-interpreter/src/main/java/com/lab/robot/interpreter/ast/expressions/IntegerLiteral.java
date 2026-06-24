package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

public class IntegerLiteral implements Expression {
    public final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }
}
