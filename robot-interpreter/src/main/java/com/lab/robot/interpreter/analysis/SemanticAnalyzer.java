package com.lab.robot.interpreter.analysis;

import com.lab.robot.interpreter.InterpreterException;
import com.lab.robot.interpreter.ast.Program;
import com.lab.robot.interpreter.ast.Statement;
import com.lab.robot.interpreter.ast.declarations.FunctionDecl;
import com.lab.robot.interpreter.ast.declarations.VarDecl;

import java.util.HashSet;
import java.util.Set;

public final class SemanticAnalyzer {

    public void analyze(Program program) {
        Set<String> names = new HashSet<>();
        Set<String> functions = new HashSet<>();
        for (Statement stmt : program.statements) {
            if (stmt instanceof VarDecl vd) {
                if (!names.add(vd.name)) {
                    throw new InterpreterException("Duplicate variable: " + vd.name);
                }
            }
        }
        StatementTreeWalker.forEachStatement(program.statements, stmt -> {
            if (stmt instanceof FunctionDecl fd) {
                if (!functions.add(fd.name)) {
                    throw new InterpreterException("Duplicate function: " + fd.name);
                }
            }
        });
    }
}
