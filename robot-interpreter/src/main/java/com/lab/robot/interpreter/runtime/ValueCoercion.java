package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.InterpreterException;
import com.lab.robot.interpreter.ast.Type;

public final class ValueCoercion {

    private ValueCoercion() {
    }

    public static SeisuValue toSeisu(Value value) {
        if (value instanceof SeisuValue s) {
            return s;
        }
        if (value instanceof RonriValue r) {
            return r.toSeisu();
        }
        throw new InterpreterException("Cannot convert " + value.getType() + " to seisu");
    }

    public static RonriValue toRonri(Value value) {
        if (value instanceof RonriValue r) {
            return r;
        }
        if (value instanceof SeisuValue s) {
            return new RonriValue(s.value != 0);
        }
        throw new InterpreterException("Cannot convert " + value.getType() + " to ronri");
    }

    public static boolean typesMatch(Value left, Value right) {
        return left.getType() == right.getType();
    }

    public static Type resolveTypeName(String name) {
        try {
            return Type.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InterpreterException("Unknown type: " + name);
        }
    }
}
