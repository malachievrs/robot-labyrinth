package com.lab.robot.interpreter.analysis;

import com.lab.robot.interpreter.ast.Type;
import com.lab.robot.interpreter.runtime.Value;

public final class Symbol {
    public final String name;
    public final Type type;
    public final boolean mutable;
    public Value value;

    public Symbol(String name, Type type, boolean mutable, Value value) {
        this.name = name;
        this.type = type;
        this.mutable = mutable;
        this.value = value;
    }
}
