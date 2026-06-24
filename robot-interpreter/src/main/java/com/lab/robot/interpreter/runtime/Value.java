package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.ast.Type;

public abstract class Value {
    public abstract Type getType();

    public SeisuValue asSeisu() {
        throw new IllegalStateException("Expected seisu, got " + getType());
    }

    public RonriValue asRonri() {
        throw new IllegalStateException("Expected ronri, got " + getType());
    }

    public RipotaiValue asRipotai() {
        throw new IllegalStateException("Expected rippotai, got " + getType());
    }

    public HairetsuValue asHairetsu() {
        throw new IllegalStateException("Expected hairetsu, got " + getType());
    }
}
