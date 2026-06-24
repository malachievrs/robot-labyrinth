package com.lab.robot.interpreter.ast.statements;

import com.lab.robot.interpreter.ast.*;

import java.util.List;

public class LoopStatement implements Statement {
    public final String iteratorName;
    public final Expression start;
    public final Expression end;
    public final List<Statement> body;

    public LoopStatement(String iteratorName, Expression start, Expression end, List<Statement> body) {
        this.iteratorName = iteratorName;
        this.start = start;
        this.end = end;
        this.body = body;
    }
}
