package com.lab.robot.interpreter.runtime;

import com.lab.robot.common.protocol.Position;
import com.lab.robot.common.protocol.RobotClient;
import com.lab.robot.common.protocol.RobotException;
import com.lab.robot.common.protocol.RobotResponse;
import com.lab.robot.interpreter.InterpreterException;
import com.lab.robot.interpreter.ast.Direction;
import com.lab.robot.interpreter.ast.RobotAction;
import com.lab.robot.interpreter.ast.robot.BreakAction;
import com.lab.robot.interpreter.ast.robot.MeasureAction;
import com.lab.robot.interpreter.ast.robot.MoveAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RobotRuntime {
    protected final RobotClient client;
    private boolean broken;

    public RobotRuntime(RobotClient client) {
        this.client = client;
    }

    public boolean isBroken() {
        return broken;
    }

    protected RobotClient client() {
        return client;
    }

    public void move(Direction direction) {
        if (broken) {
            return;
        }
        try {
            RobotResponse response = client.move(DirectionMapper.toProtocolName(direction));
            if (response.isObstacle() || response.isBroken()) {
                broken = true;
            }
        } catch (IOException | RobotException e) {
            throw new InterpreterException("Robot move failed: " + e.getMessage());
        }
    }

    public int measure(Direction direction) {
        if (broken) {
            return -1;
        }
        try {
            RobotResponse response = client.measure(DirectionMapper.toProtocolName(direction));
            if (response.isBroken()) {
                broken = true;
                return -1;
            }
            if (!response.isOk()) {
                return 0;
            }
            return response.getDistance();
        } catch (IOException | RobotException e) {
            throw new InterpreterException("Robot measure failed: " + e.getMessage());
        }
    }

    public RipotaiValue getPosition() {
        if (broken) {
            throw new InterpreterException("Robot is broken");
        }
        try {
            RobotResponse response = client.getPosition();
            if (response.isBroken()) {
                broken = true;
                throw new InterpreterException("Robot is broken");
            }
            Position pos = response.getPosition();
            boolean obstacle = response.isCurrentCellObstacle();
            boolean exit = response.getData().optJSONObject("cell") != null
                    && response.getData().getJSONObject("cell").optBoolean("exit", false);
            return new RipotaiValue(pos.getX(), pos.getY(), pos.getZ(), obstacle, exit);
        } catch (IOException | RobotException e) {
            throw new InterpreterException("Get position failed: " + e.getMessage());
        }
    }

    public boolean hasAdjacentObstacle() {
        if (broken) {
            return true;
        }
        for (Direction d : Direction.values()) {
            if (measure(d) == 0) {
                return true;
            }
        }
        return false;
    }

    public Value executeSequence(List<RobotAction> actions) {
        List<Value> measureResults = new ArrayList<>();
        boolean hasMeasure = false;
        for (RobotAction action : actions) {
            if (broken) {
                break;
            }
            if (action instanceof MoveAction move) {
                move(move.direction);
            } else if (action instanceof MeasureAction measure) {
                hasMeasure = true;
                int dist = this.measure(measure.direction);
                measureResults.add(new SeisuValue(dist));
            } else if (action instanceof BreakAction) {
                if (hasAdjacentObstacle()) {
                    break;
                }
            }
        }
        if (hasMeasure) {
            return HairetsuValue.flat(measureResults);
        }
        return HairetsuValue.flat(List.of());
    }
}
