package com.lab.robot.interpreter.ast.declarations;

import com.lab.robot.interpreter.ast.*;

public class CellDecl implements Statement {
    public final String name;
    public final Expression x, y, z, obstacle;

    public CellDecl(String name, Expression x, Expression y, Expression z, Expression obstacle) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.obstacle = obstacle;
    }
}