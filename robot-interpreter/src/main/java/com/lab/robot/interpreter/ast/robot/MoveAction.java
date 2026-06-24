package com.lab.robot.interpreter.ast.robot;

import com.lab.robot.interpreter.ast.*;

public class MoveAction implements RobotAction {
    public final Direction direction;

    public MoveAction(Direction direction) {
        this.direction = direction;
    }
}