package com.lab.robot.interpreter;

import com.lab.robot.interpreter.ast.Program;
import com.lab.robot.interpreter.lexer.Lexer;
import com.lab.robot.interpreter.parser.RobotParser;
import com.lab.robot.interpreter.parser.RobotSymbols;
import java_cup.runtime.Symbol;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class ParseTestUtil {

    private ParseTestUtil() {
    }

    public static Program parse(String code) throws Exception {
        return com.lab.robot.interpreter.ParsePipeline.parse(code);
    }

    public static List<Symbol> tokenize(String code) throws Exception {
        Lexer lexer = new Lexer(new StringReader(code));
        List<Symbol> tokens = new ArrayList<>();
        Symbol symbol;
        while ((symbol = lexer.next_token()).sym != RobotSymbols.EOF) {
            tokens.add(symbol);
        }
        return tokens;
    }

    public static List<Integer> tokenTypes(String code) throws Exception {
        List<Integer> types = new ArrayList<>();
        for (Symbol symbol : tokenize(code)) {
            types.add(symbol.sym);
        }
        return types;
    }
}
