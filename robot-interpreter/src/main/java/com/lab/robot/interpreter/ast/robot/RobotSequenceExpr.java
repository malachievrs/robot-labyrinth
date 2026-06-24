package com.lab.robot.interpreter.ast.robot;

import com.lab.robot.interpreter.ast.Expression;
import com.lab.robot.interpreter.ast.RobotAction;
import java.util.List;

public class RobotSequenceExpr implements Expression {
    public final List<RobotAction> actions;

    public RobotSequenceExpr(List<RobotAction> actions) {
        this.actions = actions;
    }
}
