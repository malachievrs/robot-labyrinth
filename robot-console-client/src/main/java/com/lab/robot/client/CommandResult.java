package com.lab.robot.client;

import com.lab.robot.common.protocol.Position;

public class CommandResult {
    private final boolean success;
    private final String message;
    private final Position newPosition;
    private final int distance;
    private final boolean exit;
    private final boolean obstacle;

    public CommandResult(boolean success, String message, Position newPosition,
                         int distance, boolean exit, boolean obstacle) {
        this.success = success;
        this.message = message;
        this.newPosition = newPosition;
        this.distance = distance;
        this.exit = exit;
        this.obstacle = obstacle;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Position getNewPosition() { return newPosition; }
    public int getDistance() { return distance; }
    public boolean isExit() { return exit; }
    public boolean isObstacle() { return obstacle; }
}
