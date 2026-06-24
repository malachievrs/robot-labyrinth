package com.lab.robot.interpreter.ast.expressions;

import com.lab.robot.interpreter.ast.*;

public class CellPropertyAccess implements Expression {
    public final Expression cell;
    public final String propertyName;

    public CellPropertyAccess(Expression cell, String propertyName) {
        this.cell = cell;
        this.propertyName = propertyName;
    }
}