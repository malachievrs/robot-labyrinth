package com.lab.robot.interpreter.ast.robot;

import com.lab.robot.interpreter.ast.*;

public class MeasureExpr implements Expression {
    public final Direction direction;

    public MeasureExpr(Direction direction) {
        this.direction = direction;
    }
}