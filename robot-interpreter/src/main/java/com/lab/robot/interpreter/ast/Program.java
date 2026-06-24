package com.lab.robot.interpreter.ast;

import java.util.List;

public class Program implements Node {
    public final List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }
}
