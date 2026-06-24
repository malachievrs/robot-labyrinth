package com.lab.robot.interpreter.runtime;

import com.lab.robot.common.console.ConsoleRenderer;
import com.lab.robot.common.protocol.LabyrinthMap;
import com.lab.robot.common.protocol.Position;
import com.lab.robot.common.protocol.RobotClient;
import com.lab.robot.common.protocol.RobotException;
import com.lab.robot.common.protocol.RobotResponse;
import com.lab.robot.interpreter.ast.Direction;
import com.lab.robot.interpreter.ast.RobotAction;
import com.lab.robot.interpreter.ast.robot.BreakAction;
import com.lab.robot.interpreter.ast.robot.MeasureAction;
import com.lab.robot.interpreter.ast.robot.MoveAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsoleRobotRuntime extends RobotRuntime {
    private boolean renderMap;
    private LabyrinthMap map;
    private Position lastPosition;

    public ConsoleRobotRuntime(RobotClient client, boolean renderMap) {
        super(client);
        this.renderMap = renderMap;
        if (renderMap) {
            try {
                this.map = client.getLabyrinth();
                RipotaiValue pos = getPosition();
                lastPosition = new Position(pos.x, pos.y, pos.z);
                log("Labyrinth map loaded: " + map.getSizeX() + "x" + map.getSizeY() + "x" + map.getSizeZ());
                render();
            } catch (IOException | RobotException e) {
                log("Map preview disabled: " + e.getMessage());
                this.renderMap = false;
            }
        }
    }

    private void log(String message) {
        System.out.println("[robot] " + message);
    }

    private void render() {
        if (!renderMap || map == null || lastPosition == null) {
            return;
        }
        char[][] layer = map.getLayer(lastPosition.getZ());
        ConsoleRenderer.render(layer, lastPosition, lastPosition.getZ(), false);
    }

    private void updatePositionFromServer() {
        try {
            RobotResponse response = client().getPosition();
            if (response.getPosition() != null) {
                lastPosition = response.getPosition();
            }
        } catch (IOException | RobotException ignored) {
            RipotaiValue pos = getPosition();
            lastPosition = new Position(pos.x, pos.y, pos.z);
        }
    }

    @Override
    public void move(Direction direction) {
        log("MOVE " + direction);
        super.move(direction);
        if (!isBroken()) {
            updatePositionFromServer();
            if (lastPosition != null) {
                log("  at " + lastPosition);
            }
            render();
        } else {
            log("  OBSTACLE — robot broken");
        }
    }

    @Override
    public int measure(Direction direction) {
        int distance = super.measure(direction);
        log("MEASURE " + direction + " => " + distance);
        return distance;
    }

    @Override
    public RipotaiValue getPosition() {
        RipotaiValue pos = super.getPosition();
        lastPosition = new Position(pos.x, pos.y, pos.z);
        log("POSITION " + lastPosition + " exit=" + pos.exit + " obstacle=" + pos.obstacle);
        return pos;
    }

    @Override
    public Value executeSequence(List<RobotAction> actions) {
        log("SEQUENCE { ... } (" + actions.size() + " actions)");

        List<Value> measureResults = new ArrayList<>();
        boolean hasMeasure = false;

        for (RobotAction action : actions) {
            if (isBroken()) {
                break;
            }
            if (action instanceof MoveAction move) {
                move(move.direction);
            } else if (action instanceof MeasureAction measure) {
                hasMeasure = true;
                int dist = measure(measure.direction);
                measureResults.add(new SeisuValue(dist));
            } else if (action instanceof BreakAction) {
                log("BREAK scan");
                if (hasAdjacentObstacle()) {
                    log("  adjacent obstacle — stop sequence");
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
