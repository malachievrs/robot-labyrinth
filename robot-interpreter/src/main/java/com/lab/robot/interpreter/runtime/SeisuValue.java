package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.ast.Type;

public final class SeisuValue extends Value {
    public final int value;

    public SeisuValue(int value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.SEISU;
    }

    @Override
    public SeisuValue asSeisu() {
        return this;
    }
}
