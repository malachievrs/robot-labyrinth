package com.lab.robot.interpreter.ast.statements;
import com.lab.robot.interpreter.ast.*;
public class Assignment implements Statement, Expression {
    public final Expression lhs;
    public final Expression rhs;
    public Assignment(Expression lhs, Expression rhs) { this.lhs = lhs; this.rhs = rhs; }
}