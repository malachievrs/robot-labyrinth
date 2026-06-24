package com.lab.robot.interpreter.ast.statements;

import com.lab.robot.interpreter.ast.*;

import java.util.List;

public class IfStatement implements Statement {
    public final Expression condition;
    public final List<Statement> body;

    public IfStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }
}