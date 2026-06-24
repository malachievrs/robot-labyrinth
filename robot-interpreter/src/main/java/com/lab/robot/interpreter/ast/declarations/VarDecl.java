package com.lab.robot.interpreter.ast.declarations;

import com.lab.robot.interpreter.ast.*;

public class VarDecl implements Statement {
    public final Type type;
    public final String name;
    public final Expression init;

    public VarDecl(Type type, String name, Expression init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }
}
