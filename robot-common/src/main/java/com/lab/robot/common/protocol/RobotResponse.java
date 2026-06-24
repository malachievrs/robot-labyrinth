package com.lab.robot.common.protocol;

import org.json.JSONObject;

public class RobotResponse {
    private final String status;
    private final JSONObject data;

    public RobotResponse(String status, JSONObject data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOk() {
        return "OK".equals(status);
    }

    public boolean isObstacle() {
        return "OBSTACLE".equals(status);
    }

    public boolean isBroken() {
        return "BROKEN".equals(status);
    }

    public int getDistance() {
        return data.optInt("distance", -1);
    }

    public Position getPosition() {
        JSONObject pos = data.optJSONObject("position");
        if (pos == null) return null;
        return new Position(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
    }

    public boolean isCurrentCellObstacle() {
        JSONObject cell = data.optJSONObject("cell");
        if (cell == null) return false;
        return cell.optBoolean("obstacle", false);
    }

    public JSONObject getData() {
        return data;
    }
}
