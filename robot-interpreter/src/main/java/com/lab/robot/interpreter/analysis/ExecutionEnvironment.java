package com.lab.robot.interpreter.analysis;

import com.lab.robot.interpreter.InterpreterException;
import com.lab.robot.interpreter.ast.Type;
import com.lab.robot.interpreter.runtime.Value;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public final class ExecutionEnvironment {
    private final Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();

    public ExecutionEnvironment() {
        pushScope();
    }

    public void pushScope() {
        scopes.push(new HashMap<>());
    }

    public void popScope() {
        if (scopes.size() <= 1) {
            throw new IllegalStateException("Cannot pop global scope");
        }
        scopes.pop();
    }

    public void declare(String name, Type type, boolean mutable, Value initial) {
        Map<String, Symbol> scope = scopes.peek();
        if (scope.containsKey(name)) {
            throw new InterpreterException("Duplicate declaration: " + name);
        }
        scope.put(name, new Symbol(name, type, mutable, initial));
    }

    public Symbol lookup(String name) {
        for (Map<String, Symbol> scope : scopes) {
            Symbol symbol = scope.get(name);
            if (symbol != null) {
                return symbol;
            }
        }
        throw new InterpreterException("Undefined variable: " + name);
    }

    public boolean isDefined(String name) {
        for (Map<String, Symbol> scope : scopes) {
            if (scope.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public void assign(String name, Value value) {
        Symbol symbol = lookup(name);
        if (!symbol.mutable) {
            throw new InterpreterException("Cannot assign to constant/parameter: " + name);
        }
        if (symbol.type != value.getType()) {
            throw new InterpreterException("Type mismatch assigning to " + name);
        }
        symbol.value = value;
    }
}
