package com.lab.robot.interpreter.ast;

import com.lab.robot.interpreter.ast.declarations.ArrayDecl;
import com.lab.robot.interpreter.ast.declarations.CellDecl;
import com.lab.robot.interpreter.ast.declarations.FunctionDecl;
import com.lab.robot.interpreter.ast.declarations.VarDecl;
import com.lab.robot.interpreter.ast.expressions.ArrayDimension;
import com.lab.robot.interpreter.ast.expressions.ArrayElementAccess;
import com.lab.robot.interpreter.ast.expressions.BinaryOp;
import com.lab.robot.interpreter.ast.expressions.BooleanLiteral;
import com.lab.robot.interpreter.ast.expressions.CellPropertyAccess;
import com.lab.robot.interpreter.ast.expressions.FunctionCall;
import com.lab.robot.interpreter.ast.expressions.IntegerLiteral;
import com.lab.robot.interpreter.ast.expressions.TypeComparison;
import com.lab.robot.interpreter.ast.expressions.UnaryOp;
import com.lab.robot.interpreter.ast.expressions.VarAccess;
import com.lab.robot.interpreter.ast.robot.BreakAction;
import com.lab.robot.interpreter.ast.robot.GetPositionExpr;
import com.lab.robot.interpreter.ast.robot.MeasureAction;
import com.lab.robot.interpreter.ast.robot.MeasureExpr;
import com.lab.robot.interpreter.ast.robot.MoveAction;
import com.lab.robot.interpreter.ast.robot.RobotSequenceExpr;
import com.lab.robot.interpreter.ast.statements.Assignment;
import com.lab.robot.interpreter.ast.statements.ExpressionStatement;
import com.lab.robot.interpreter.ast.statements.IfStatement;
import com.lab.robot.interpreter.ast.statements.LoopStatement;
import com.lab.robot.interpreter.ast.statements.ReturnStatement;

import java.io.PrintStream;
import java.util.List;

public final class ProgramAstPrinter {

    private final StringBuilder out = new StringBuilder();
    private int depth;

    private ProgramAstPrinter() {
    }

    public static String format(Program program) {
        ProgramAstPrinter printer = new ProgramAstPrinter();
        printer.printProgram(program);
        return printer.out.toString();
    }

    public static void print(Program program, PrintStream stream) {
        stream.print(format(program));
    }

    private void printProgram(Program program) {
        line("Program");
        indent(() -> {
            for (int i = 0; i < program.statements.size(); i++) {
                final int index = i;
                line("statement[" + index + "]");
                indent(() -> printStatement(program.statements.get(index)));
            }
        });
    }

    private void printStatements(List<Statement> statements) {
        for (int i = 0; i < statements.size(); i++) {
            final int index = i;
            line("statement[" + index + "]");
            indent(() -> printStatement(statements.get(index)));
        }
    }

    private void printStatement(Statement stmt) {
        if (stmt instanceof VarDecl vd) {
            line("VarDecl type=" + vd.type + " name=" + vd.name);
            if (vd.init != null) {
                indent(() -> {
                    line("init");
                    indent(() -> printExpression(vd.init));
                });
            }
        } else if (stmt instanceof CellDecl cd) {
            line("CellDecl name=" + cd.name);
            indent(() -> {
                line("x");
                indent(() -> printExpression(cd.x));
                line("y");
                indent(() -> printExpression(cd.y));
                line("z");
                indent(() -> printExpression(cd.z));
                line("obstacle");
                indent(() -> printExpression(cd.obstacle));
            });
        } else if (stmt instanceof ArrayDecl ad) {
            line("ArrayDecl name=" + ad.name);
            indent(() -> {
                for (int i = 0; i < ad.dimensions.size(); i++) {
                    final int index = i;
                    line("dim[" + index + "]");
                    indent(() -> printExpression(ad.dimensions.get(index)));
                }
            });
        } else if (stmt instanceof FunctionDecl fd) {
            line("FunctionDecl name=" + fd.name + " returnType=" + fd.returnType);
            indent(() -> {
                for (int i = 0; i < fd.params.size(); i++) {
                    final int index = i;
                    Param p = fd.params.get(index);
                    line("param[" + index + "] type=" + p.type + " name=" + p.name);
                }
                line("body");
                indent(() -> printStatements(fd.body));
            });
        } else if (stmt instanceof IfStatement ifs) {
            line("IfStatement");
            indent(() -> {
                line("condition");
                indent(() -> printExpression(ifs.condition));
                line("body");
                indent(() -> printStatements(ifs.body));
            });
        } else if (stmt instanceof LoopStatement loop) {
            line("LoopStatement iterator=" + loop.iteratorName);
            indent(() -> {
                line("start");
                indent(() -> printExpression(loop.start));
                line("end");
                indent(() -> printExpression(loop.end));
                line("body");
                indent(() -> printStatements(loop.body));
            });
        } else if (stmt instanceof ReturnStatement ret) {
            line("ReturnStatement");
            if (ret.expr != null) {
                indent(() -> {
                    line("expr");
                    indent(() -> printExpression(ret.expr));
                });
            }
        } else if (stmt instanceof ExpressionStatement es) {
            line("ExpressionStatement");
            indent(() -> printExpression(es.expr));
        } else if (stmt instanceof Assignment asg) {
            line("Assignment");
            indent(() -> {
                line("lhs");
                indent(() -> printExpression(asg.lhs));
                line("rhs");
                indent(() -> printExpression(asg.rhs));
            });
        } else if (stmt instanceof MoveAction move) {
            line("MoveAction direction=" + move.direction);
        } else if (stmt instanceof BreakAction) {
            line("BreakAction");
        } else {
            line("UnknownStatement " + stmt.getClass().getSimpleName());
        }
    }

    private void printExpression(Expression expr) {
        if (expr instanceof IntegerLiteral il) {
            line("IntegerLiteral value=" + il.value);
        } else if (expr instanceof BooleanLiteral bl) {
            line("BooleanLiteral value=" + bl.value);
        } else if (expr instanceof VarAccess va) {
            line("VarAccess name=" + va.name);
        } else if (expr instanceof BinaryOp bin) {
            line("BinaryOp operator=" + bin.operator);
            indent(() -> {
                line("left");
                indent(() -> printExpression(bin.left));
                line("right");
                indent(() -> printExpression(bin.right));
            });
        } else if (expr instanceof UnaryOp un) {
            line("UnaryOp operator=" + un.operator);
            indent(() -> {
                line("expr");
                indent(() -> printExpression(un.expr));
            });
        } else if (expr instanceof Assignment asg) {
            line("Assignment(expr)");
            indent(() -> {
                line("lhs");
                indent(() -> printExpression(asg.lhs));
                line("rhs");
                indent(() -> printExpression(asg.rhs));
            });
        } else if (expr instanceof FunctionCall call) {
            line("FunctionCall name=" + call.name);
            indent(() -> {
                for (int i = 0; i < call.args.size(); i++) {
                    final int index = i;
                    line("arg[" + index + "]");
                    indent(() -> printExpression(call.args.get(index)));
                }
            });
        } else if (expr instanceof CellPropertyAccess cpa) {
            line("CellPropertyAccess property=" + cpa.propertyName);
            indent(() -> {
                line("cell");
                indent(() -> printExpression(cpa.cell));
            });
        } else if (expr instanceof ArrayElementAccess aea) {
            line("ArrayElementAccess");
            indent(() -> {
                line("array");
                indent(() -> printExpression(aea.array));
                for (int i = 0; i < aea.indices.size(); i++) {
                    final int index = i;
                    line("index[" + index + "]");
                    indent(() -> printExpression(aea.indices.get(index)));
                }
            });
        } else if (expr instanceof ArrayDimension ad) {
            line("ArrayDimension");
            indent(() -> {
                line("array");
                indent(() -> printExpression(ad.array));
            });
        } else if (expr instanceof TypeComparison tc) {
            line("TypeComparison");
            indent(() -> {
                line("left");
                indent(() -> printExpression(tc.left));
                line("right");
                indent(() -> printExpression(tc.right));
            });
        } else if (expr instanceof MeasureExpr me) {
            line("MeasureExpr direction=" + me.direction);
        } else if (expr instanceof GetPositionExpr) {
            line("GetPositionExpr");
        } else if (expr instanceof RobotSequenceExpr rse) {
            line("RobotSequenceExpr");
            indent(() -> printRobotActions(rse.actions));
        } else {
            line("UnknownExpression " + expr.getClass().getSimpleName());
        }
    }

    private void printRobotActions(List<RobotAction> actions) {
        for (int i = 0; i < actions.size(); i++) {
            final int index = i;
            line("action[" + index + "]");
            indent(() -> printRobotAction(actions.get(index)));
        }
    }

    private void printRobotAction(RobotAction action) {
        if (action instanceof MoveAction move) {
            line("MoveAction direction=" + move.direction);
        } else if (action instanceof MeasureAction measure) {
            line("MeasureAction direction=" + measure.direction);
        } else if (action instanceof BreakAction) {
            line("BreakAction");
        } else {
            line("UnknownRobotAction " + action.getClass().getSimpleName());
        }
    }

    private void line(String text) {
        out.append("  ".repeat(depth));
        out.append(text);
        out.append('\n');
    }

    private void indent(Runnable block) {
        depth++;
        try {
            block.run();
        } finally {
            depth--;
        }
    }
}
