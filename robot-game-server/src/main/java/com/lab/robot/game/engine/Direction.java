package com.lab.robot.game.engine;

public enum Direction {
    UP(0, 1, 0),
    DOWN(0, -1, 0),
    LEFT(-1, 0, 0),
    RIGHT(1, 0, 0),
    FORWARD(0, 0, 1),
    BACK(0, 0, -1);

    private final int dx, dy, dz;

    Direction(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getDz() {
        return dz;
    }

    public static Direction fromString(String name) {
        return valueOf(name.toUpperCase());
    }
}