package com.lab.robot.client;

public class Command {
    public enum Type { MOVE, MEASURE, POSITION, EXIT, HELP, UNKNOWN }

    private final Type type;
    private final String direction;

    public Command(Type type, String direction) {
        this.type = type;
        this.direction = direction;
    }
    public Type getType() { return type; }
    public String getDirection() { return direction; }
}