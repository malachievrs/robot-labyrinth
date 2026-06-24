package com.lab.robot.client;

import com.lab.robot.common.protocol.Position;
import com.lab.robot.common.protocol.RobotClient;
import com.lab.robot.common.protocol.RobotException;
import com.lab.robot.common.protocol.RobotResponse;

import java.io.IOException;

public class CommandExecutor {
    private final RobotClient client;

    public CommandExecutor(RobotClient client) {
        this.client = client;
    }

    public CommandResult execute(Command cmd) {
        try {
            switch (cmd.getType()) {
                case MOVE: {
                    RobotResponse resp = client.move(cmd.getDirection());
                    boolean ok = resp.isOk();
                    return new CommandResult(ok, resp.getData().optString("message", ""),
                            resp.getPosition(), -1, false, false);
                }
                case MEASURE: {
                    RobotResponse resp = client.measure(cmd.getDirection());
                    boolean ok = resp.isOk();
                    return new CommandResult(ok, "", null,
                            ok ? resp.getDistance() : -1, false, false);
                }
                case POSITION: {
                    RobotResponse resp = client.getPosition();
                    boolean ok = resp.isOk() || resp.isBroken();
                    Position pos = resp.getPosition();
                    boolean isExit = resp.getData().optJSONObject("cell") != null
                            && resp.getData().getJSONObject("cell").optBoolean("exit", false);
                    boolean isObstacle = resp.isCurrentCellObstacle();
                    return new CommandResult(ok, "", pos, -1, isExit, isObstacle);
                }
                case EXIT:
                    client.close();
                    return new CommandResult(true, "exit", null, -1, false, false);
                case HELP:
                    return new CommandResult(true,
                            "W/A/S/D/Q/E move, R <dir> measure, P position, X exit",
                            null, -1, false, false);
                default:
                    return new CommandResult(false, "Unknown command", null, -1, false, false);
            }
        } catch (IOException | RobotException e) {
            return new CommandResult(false, e.getMessage(), null, -1, false, false);
        }
    }
}