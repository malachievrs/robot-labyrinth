package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.InterpreterException;
import com.lab.robot.interpreter.analysis.ExecutionEnvironment;
import com.lab.robot.interpreter.analysis.StatementTreeWalker;
import com.lab.robot.interpreter.analysis.Symbol;
import com.lab.robot.interpreter.ast.*;
import com.lab.robot.interpreter.ast.declarations.*;
import com.lab.robot.interpreter.ast.expressions.*;
import com.lab.robot.interpreter.ast.robot.*;
import com.lab.robot.interpreter.ast.statements.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Interpreter {
    private final ExecutionEnvironment env = new ExecutionEnvironment();
    private final RobotRuntime robot;
    private final Map<String, FunctionDecl> functions = new HashMap<>();
    private int functionDepth;

    public Interpreter(RobotRuntime robot) {
        this.robot = robot;
    }

    public Value getVariable(String name) {
        return env.lookup(name).value;
    }

    public void execute(Program program) {
        registerFunctions(program);
        for (Statement stmt : program.statements) {
            executeStatement(stmt);
            if (robot.isBroken()) {
                break;
            }
        }
    }

    private void registerFunctions(Program program) {
        StatementTreeWalker.forEachStatement(program.statements, stmt -> {
            if (stmt instanceof FunctionDecl fd) {
                if (functions.containsKey(fd.name)) {
                    throw new InterpreterException("Duplicate function: " + fd.name);
                }
                functions.put(fd.name, fd);
            }
        });
    }

    private void executeStatement(Statement stmt) {
        if (stmt instanceof VarDecl vd) {
            executeVarDecl(vd);
        } else if (stmt instanceof CellDecl cd) {
            executeCellDecl(cd);
        } else if (stmt instanceof ArrayDecl ad) {
            executeArrayDecl(ad);
        } else if (stmt instanceof IfStatement ifs) {
            executeIf(ifs);
        } else if (stmt instanceof LoopStatement loop) {
            executeLoop(loop);
        } else if (stmt instanceof ReturnStatement ret) {
            if (functionDepth == 0) {
                throw new InterpreterException("Return outside function");
            }
            Value v = ret.expr != null ? evaluateExpression(ret.expr) : new SeisuValue(0);
            throw new ReturnSignal(v);
        } else if (stmt instanceof ExpressionStatement es) {
            evaluateExpression(es.expr);
        } else if (stmt instanceof Assignment asg) {
            executeAssignment(asg);
        } else if (stmt instanceof MoveAction move) {
            robot.move(move.direction);
        } else if (stmt instanceof BreakAction) {
        } else if (stmt instanceof FunctionDecl) {
        } else {
            throw new InterpreterException("Unknown statement: " + stmt.getClass().getSimpleName());
        }
    }

    private void executeVarDecl(VarDecl vd) {
        Value init = vd.init != null ? evaluateExpression(vd.init) : defaultValue(vd.type);
        if (init.getType() != vd.type) {
            init = coerceInit(vd.type, init);
        }
        env.declare(vd.name, vd.type, true, init);
    }

    private void executeCellDecl(CellDecl cd) {
        int x = ValueCoercion.toSeisu(evaluateExpression(cd.x)).value;
        int y = ValueCoercion.toSeisu(evaluateExpression(cd.y)).value;
        int z = ValueCoercion.toSeisu(evaluateExpression(cd.z)).value;
        boolean obs = ValueCoercion.toRonri(evaluateExpression(cd.obstacle)).value;
        env.declare(cd.name, Type.RIPPTAI, true, new RipotaiValue(x, y, z, obs));
    }

    private void executeArrayDecl(ArrayDecl ad) {
        List<Integer> dims = new ArrayList<>();
        for (Expression dimExpr : ad.dimensions) {
            dims.add(ValueCoercion.toSeisu(evaluateExpression(dimExpr)).value);
        }
        env.declare(ad.name, Type.HAIRETSU, true, HairetsuValue.allocate(dims));
    }

    private void executeIf(IfStatement ifs) {
        if (ValueCoercion.toRonri(evaluateExpression(ifs.condition)).value) {
            for (Statement bodyStmt : ifs.body) {
                executeStatement(bodyStmt);
                if (robot.isBroken()) {
                    return;
                }
            }
        }
    }

    private void executeLoop(LoopStatement loop) {
        int start = ValueCoercion.toSeisu(evaluateExpression(loop.start)).value;
        int end = ValueCoercion.toSeisu(evaluateExpression(loop.end)).value;
        if (env.isDefined(loop.iteratorName)) {
            env.assign(loop.iteratorName, new SeisuValue(start));
        } else {
            env.declare(loop.iteratorName, Type.SEISU, true, new SeisuValue(start));
        }
        int step = start <= end ? 1 : -1;
        for (int i = start; step > 0 ? i <= end : i >= end; i += step) {
            env.assign(loop.iteratorName, new SeisuValue(i));
            for (Statement bodyStmt : loop.body) {
                executeStatement(bodyStmt);
                if (robot.isBroken()) {
                    return;
                }
            }
        }
    }

    private void executeAssignment(Assignment asg) {
        Value rhs = evaluateExpression(asg.rhs);
        assignToTarget(asg.lhs, rhs);
    }

    private void assignToTarget(Expression target, Value value) {
        if (target instanceof VarAccess va) {
            Symbol sym = env.lookup(va.name);
            Value coerced = coerceAssign(sym.type, value);
            env.assign(va.name, coerced);
        } else if (target instanceof CellPropertyAccess cpa) {
            RipotaiValue cell = resolveRipotai(cpa.cell);
            String prop = cpa.propertyName;
            cell = updateCellProperty(cell, prop, value);
            assignBack(cpa.cell, cell);
        } else if (target instanceof ArrayElementAccess aea) {
            setArrayElement(aea, value);
        } else {
            throw new InterpreterException("Invalid assignment target");
        }
    }

    private RipotaiValue updateCellProperty(RipotaiValue cell, String prop, Value value) {
        return switch (prop) {
            case "x" -> new RipotaiValue(ValueCoercion.toSeisu(value).value, cell.y, cell.z, cell.obstacle, cell.exit);
            case "y" -> new RipotaiValue(cell.x, ValueCoercion.toSeisu(value).value, cell.z, cell.obstacle, cell.exit);
            case "z" -> new RipotaiValue(cell.x, cell.y, ValueCoercion.toSeisu(value).value, cell.obstacle, cell.exit);
            case "obstacle" -> new RipotaiValue(cell.x, cell.y, cell.z, ValueCoercion.toRonri(value).value, cell.exit);
            case "exit" -> new RipotaiValue(cell.x, cell.y, cell.z, cell.obstacle, ValueCoercion.toRonri(value).value);
            default -> throw new InterpreterException("Unknown cell property: " + prop);
        };
    }

    private void assignBack(Expression cellExpr, RipotaiValue cell) {
        if (cellExpr instanceof VarAccess va) {
            env.assign(va.name, cell);
        } else {
            throw new InterpreterException("Cannot assign to complex cell expression");
        }
    }

    private RipotaiValue resolveRipotai(Expression expr) {
        return evaluateExpression(expr).asRipotai();
    }

    private void setArrayElement(ArrayElementAccess access, Value value) {
        HairetsuValue array = evaluateExpression(access.array).asHairetsu();
        List<Integer> indices = new ArrayList<>();
        for (Expression ix : access.indices) {
            indices.add(ValueCoercion.toSeisu(evaluateExpression(ix)).value);
        }
        array.setElement(indices, value);
    }

    private Value getArrayElement(ArrayElementAccess access) {
        HairetsuValue array = evaluateExpression(access.array).asHairetsu();
        List<Integer> indices = new ArrayList<>();
        for (Expression ix : access.indices) {
            indices.add(ValueCoercion.toSeisu(evaluateExpression(ix)).value);
        }
        return array.getElement(indices);
    }

    public Value evaluateExpression(Expression expr) {
        if (expr instanceof IntegerLiteral il) {
            return new SeisuValue(il.value);
        }
        if (expr instanceof BooleanLiteral bl) {
            return new RonriValue(bl.value);
        }
        if (expr instanceof VarAccess va) {
            return env.lookup(va.name).value;
        }
        if (expr instanceof BinaryOp bin) {
            return evalBinary(bin);
        }
        if (expr instanceof UnaryOp un) {
            return evalUnary(un);
        }
        if (expr instanceof Assignment asg) {
            Value rhs = evaluateExpression(asg.rhs);
            assignToTarget(asg.lhs, rhs);
            return rhs;
        }
        if (expr instanceof FunctionCall call) {
            return callFunction(call);
        }
        if (expr instanceof CellPropertyAccess cpa) {
            return readCellProperty(cpa);
        }
        if (expr instanceof ArrayElementAccess aea) {
            return getArrayElement(aea);
        }
        if (expr instanceof ArrayDimension ad) {
            HairetsuValue arr = evaluateExpression(ad.array).asHairetsu();
            return new SeisuValue(arr.dimensionCount());
        }
        if (expr instanceof TypeComparison tc) {
            return evalTypeComparison(tc);
        }
        if (expr instanceof MeasureExpr me) {
            return new SeisuValue(robot.measure(me.direction));
        }
        if (expr instanceof GetPositionExpr) {
            return robot.getPosition();
        }
        if (expr instanceof RobotSequenceExpr rse) {
            return robot.executeSequence(rse.actions);
        }
        throw new InterpreterException("Unknown expression: " + expr.getClass().getSimpleName());
    }

    private Value readCellProperty(CellPropertyAccess cpa) {
        RipotaiValue cell = resolveRipotai(cpa.cell);
        return switch (cpa.propertyName) {
            case "x" -> new SeisuValue(cell.x);
            case "y" -> new SeisuValue(cell.y);
            case "z" -> new SeisuValue(cell.z);
            case "obstacle" -> new RonriValue(cell.obstacle);
            case "exit" -> new RonriValue(cell.exit);
            default -> throw new InterpreterException("Unknown cell property: " + cpa.propertyName);
        };
    }

    private Value evalTypeComparison(TypeComparison tc) {
        Type leftType = resolveTypeOf(tc.left);
        Type rightType = resolveTypeOf(tc.right);
        return new RonriValue(leftType == rightType);
    }

    private Type resolveTypeOf(Expression expr) {
        if (expr instanceof VarAccess va) {
            if (env.isDefined(va.name)) {
                return env.lookup(va.name).type;
            }
            return ValueCoercion.resolveTypeName(va.name);
        }
        if (expr instanceof CellPropertyAccess cpa) {
            String prop = cpa.propertyName;
            if ("obstacle".equals(prop)) {
                return Type.RONRI;
            }
            return Type.SEISU;
        }
        if (expr instanceof ArrayElementAccess aea) {
            return getArrayElement(aea).getType();
        }
        throw new InterpreterException("Cannot resolve type for ruikei operand");
    }

    private Value evalBinary(BinaryOp bin) {
        if (bin.operator == Operator.AND || bin.operator == Operator.OR) {
            RonriValue l = ValueCoercion.toRonri(evaluateExpression(bin.left));
            RonriValue r = ValueCoercion.toRonri(evaluateExpression(bin.right));
            return switch (bin.operator) {
                case AND -> new RonriValue(l.value && r.value);
                case OR -> new RonriValue(l.value || r.value);
                default -> throw new InterpreterException("Invalid logical op");
            };
        }
        if (bin.operator == Operator.LESS || bin.operator == Operator.GREATER) {
            int l = ValueCoercion.toSeisu(evaluateExpression(bin.left)).value;
            int r = ValueCoercion.toSeisu(evaluateExpression(bin.right)).value;
            return switch (bin.operator) {
                case LESS -> new RonriValue(l < r);
                case GREATER -> new RonriValue(l > r);
                default -> throw new InterpreterException("Invalid compare op");
            };
        }
        int l = ValueCoercion.toSeisu(evaluateExpression(bin.left)).value;
        int r = ValueCoercion.toSeisu(evaluateExpression(bin.right)).value;
        return switch (bin.operator) {
            case ADD -> new SeisuValue(l + r);
            case SUB -> new SeisuValue(l - r);
            default -> throw new InterpreterException("Invalid arithmetic op");
        };
    }

    private Value evalUnary(UnaryOp un) {
        if (un.operator == Operator.NOT) {
            return new RonriValue(!ValueCoercion.toRonri(evaluateExpression(un.expr)).value);
        }
        if (un.operator == Operator.NEG) {
            return new SeisuValue(-ValueCoercion.toSeisu(evaluateExpression(un.expr)).value);
        }
        throw new InterpreterException("Unknown unary op");
    }

    private Value callFunction(FunctionCall call) {
        FunctionDecl fn = functions.get(call.name);
        if (fn == null) {
            throw new InterpreterException("Undefined function: " + call.name);
        }
        if (call.args.size() != fn.params.size()) {
            throw new InterpreterException("Argument count mismatch for " + call.name);
        }
        env.pushScope();
        try {
            for (int i = 0; i < fn.params.size(); i++) {
                Param p = fn.params.get(i);
                Value argVal = evaluateExpression(call.args.get(i));
                argVal = coerceInit(p.type, argVal);
                env.declare(p.name, p.type, false, argVal);
            }
            Value result = null;
            functionDepth++;
            try {
                for (Statement stmt : fn.body) {
                    try {
                        executeStatement(stmt);
                    } catch (ReturnSignal rs) {
                        result = rs.value;
                        break;
                    }
                    if (robot.isBroken()) {
                        break;
                    }
                }
            } finally {
                functionDepth--;
            }
            if (fn.returnType == null) {
                return new SeisuValue(0);
            }
            if (result == null) {
                return defaultValue(fn.returnType);
            }
            return coerceInit(fn.returnType, result);
        } finally {
            env.popScope();
        }
    }

    private Value defaultValue(Type type) {
        return switch (type) {
            case SEISU -> new SeisuValue(0);
            case RONRI -> new RonriValue(false);
            case RIPPTAI -> new RipotaiValue(0, 0, 0, false);
            case HAIRETSU -> HairetsuValue.flat(new ArrayList<>());
        };
    }

    private Value coerceInit(Type target, Value value) {
        return switch (target) {
            case SEISU -> ValueCoercion.toSeisu(value);
            case RONRI -> ValueCoercion.toRonri(value);
            case RIPPTAI -> {
                if (value instanceof RipotaiValue r) {
                    yield r;
                }
                throw new InterpreterException("Expected rippotai");
            }
            case HAIRETSU -> {
                if (value instanceof HairetsuValue h) {
                    yield h;
                }
                throw new InterpreterException("Expected hairetsu");
            }
        };
    }

    private Value coerceAssign(Type target, Value value) {
        if (target == Type.SEISU && value instanceof RonriValue r) {
            return r.toSeisu();
        }
        if (target == Type.RONRI && value instanceof SeisuValue s) {
            return new RonriValue(s.value != 0);
        }
        if (value.getType() != target) {
            throw new InterpreterException("Type mismatch: " + value.getType() + " -> " + target);
        }
        return value;
    }
}
