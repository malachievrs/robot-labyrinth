package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

public class VarAccess implements Expression {
    public final String name;

    public VarAccess(String name) {
        this.name = name;
    }
}
