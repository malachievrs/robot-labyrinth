package com.lab.robot.interpreter.analysis;

import com.lab.robot.interpreter.ast.Statement;
import com.lab.robot.interpreter.ast.declarations.FunctionDecl;
import com.lab.robot.interpreter.ast.statements.IfStatement;
import com.lab.robot.interpreter.ast.statements.LoopStatement;

import java.util.List;
import java.util.function.Consumer;

public final class StatementTreeWalker {

    private StatementTreeWalker() {
    }

    public static void forEachStatement(List<Statement> statements, Consumer<Statement> action) {
        for (Statement stmt : statements) {
            action.accept(stmt);
            if (stmt instanceof FunctionDecl fd) {
                forEachStatement(fd.body, action);
            } else if (stmt instanceof IfStatement ifs) {
                forEachStatement(ifs.body, action);
            } else if (stmt instanceof LoopStatement loop) {
                forEachStatement(loop.body, action);
            }
        }
    }
}
