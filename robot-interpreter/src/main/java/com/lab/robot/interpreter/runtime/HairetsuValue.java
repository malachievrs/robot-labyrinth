package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.InterpreterException;
import com.lab.robot.interpreter.ast.Type;

import java.util.ArrayList;
import java.util.List;

public final class HairetsuValue extends Value {
    public final List<Integer> dimensions;
    private final Object storage;

    public HairetsuValue(List<Integer> dimensions, Object storage) {
        this.dimensions = List.copyOf(dimensions);
        this.storage = storage;
    }

    public static HairetsuValue flat(List<Value> elements) {
        return new HairetsuValue(List.of(elements.size()), new ArrayList<>(elements));
    }

    public static HairetsuValue allocate(List<Integer> dimensions) {
        if (dimensions.isEmpty()) {
            return flat(new ArrayList<>());
        }
        for (int size : dimensions) {
            if (size < 0) {
                throw new InterpreterException("Array dimension must be non-negative");
            }
        }
        return new HairetsuValue(dimensions, buildLevel(dimensions, 0));
    }

    private static Object buildLevel(List<Integer> dimensions, int depth) {
        int size = dimensions.get(depth);
        if (depth == dimensions.size() - 1) {
            List<Value> leaf = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                leaf.add(new SeisuValue(0));
            }
            return leaf;
        }
        List<Object> branch = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            branch.add(buildLevel(dimensions, depth + 1));
        }
        return branch;
    }

    public List<Value> flatElements() {
        if (dimensions.size() != 1) {
            throw new IllegalStateException("Not a flat array");
        }
        return leafList();
    }

    @SuppressWarnings("unchecked")
    public List<List<Value>> grid2d() {
        if (dimensions.size() != 2) {
            throw new IllegalStateException("Not a 2D array");
        }
        return (List<List<Value>>) storage;
    }

    public int dimensionCount() {
        return dimensions.size();
    }

    public Value getElement(List<Integer> indices) {
        validateIndices(indices);
        return leafListAt(indices).get(indices.get(indices.size() - 1));
    }

    public void setElement(List<Integer> indices, Value value) {
        validateIndices(indices);
        leafListAt(indices).set(indices.get(indices.size() - 1), value);
    }

    private void validateIndices(List<Integer> indices) {
        if (indices.size() != dimensions.size()) {
            throw new InterpreterException(
                    "Index count mismatch: expected " + dimensions.size() + ", got " + indices.size());
        }
        for (int d = 0; d < indices.size(); d++) {
            int idx = indices.get(d);
            if (idx < 0 || idx >= dimensions.get(d)) {
                throw new InterpreterException("Array index out of bounds");
            }
        }
    }

    private List<Value> leafListAt(List<Integer> indices) {
        Object current = storage;
        for (int d = 0; d < indices.size() - 1; d++) {
            List<Object> level = (List<Object>) current;
            current = level.get(indices.get(d));
        }
        return (List<Value>) current;
    }

    private List<Value> leafList() {
        return (List<Value>) storage;
    }

    @Override
    public Type getType() {
        return Type.HAIRETSU;
    }

    @Override
    public HairetsuValue asHairetsu() {
        return this;
    }
}
