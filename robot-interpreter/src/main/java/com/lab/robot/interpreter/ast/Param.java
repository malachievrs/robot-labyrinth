package com.lab.robot.interpreter.ast;

public class Param implements Node {
    public final Type type;
    public final String name;

    public Param(Type type, String name) {
        this.type = type;
        this.name = name;
    }
}