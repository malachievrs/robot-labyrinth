package com.lab.robot.interpreter.ast.robot;

import com.lab.robot.interpreter.ast.*;

public class MeasureAction implements RobotAction {
    public final Direction direction;

    public MeasureAction(Direction direction) {
        this.direction = direction;
    }
}
