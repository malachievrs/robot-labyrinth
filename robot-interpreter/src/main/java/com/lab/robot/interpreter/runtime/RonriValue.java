package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.ast.Type;

public final class RonriValue extends Value {
    public final boolean value;

    public RonriValue(boolean value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.RONRI;
    }

    @Override
    public RonriValue asRonri() {
        return this;
    }

    public SeisuValue toSeisu() {
        return new SeisuValue(value ? 1 : 0);
    }
}
