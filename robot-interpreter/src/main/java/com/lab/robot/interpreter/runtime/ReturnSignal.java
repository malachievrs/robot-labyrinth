package com.lab.robot.interpreter.runtime;

public final class ReturnSignal extends RuntimeException {
    public final Value value;

    public ReturnSignal(Value value) {
        this.value = value;
    }
}
