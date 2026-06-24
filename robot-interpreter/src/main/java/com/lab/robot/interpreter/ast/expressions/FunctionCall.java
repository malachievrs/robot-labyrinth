package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

import java.util.List;

public class FunctionCall implements Expression {
    public final String name;
    public final List<Expression> args;

    public FunctionCall(String name, List<Expression> args) {
        this.name = name;
        this.args = args;
    }
}