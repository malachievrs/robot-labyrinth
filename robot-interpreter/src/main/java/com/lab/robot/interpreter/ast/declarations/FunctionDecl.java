package com.lab.robot.interpreter.ast.declarations;

import com.lab.robot.interpreter.ast.*;
import java.util.List;

public class FunctionDecl implements Statement {
    public final Type returnType;
    public final String name;
    public final List<Param> params;
    public final List<Statement> body;

    public FunctionDecl(Type returnType, String name, List<Param> params, List<Statement> body) {
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }
}