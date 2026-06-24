package com.lab.robot.interpreter.ast.declarations;

import com.lab.robot.interpreter.ast.*;

import java.util.List;

public class ArrayDecl implements Statement {
    public final String name;
    public final List<Expression> dimensions;
    public final List<RobotAction> robotActions;

    public ArrayDecl(String name, List<Expression> dimensions, List<RobotAction> robotActions) {
        this.name = name;
        this.dimensions = dimensions;
        this.robotActions = robotActions;
    }
}
