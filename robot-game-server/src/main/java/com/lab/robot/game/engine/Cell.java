package com.lab.robot.game.engine;

public class Cell {
    private boolean obstacle;
    private boolean exit;

    public Cell(boolean obstacle, boolean exit) {
        this.obstacle = obstacle;
        this.exit = exit;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }
}
