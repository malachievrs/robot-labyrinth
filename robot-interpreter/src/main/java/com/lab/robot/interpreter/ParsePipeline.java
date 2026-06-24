package com.lab.robot.interpreter;

import com.lab.robot.interpreter.ast.Program;
import com.lab.robot.interpreter.lexer.Lexer;
import com.lab.robot.interpreter.parser.RobotParser;
import java_cup.runtime.Symbol;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.StringReader;

public final class ParsePipeline {

    private ParsePipeline() {
    }

    public static Program parse(String source) throws Exception {
        Lexer lexer = new Lexer(new StringReader(source));
        RobotParser parser = new RobotParser(lexer);
        Symbol result = parser.parse();
        return (Program) result.value;
    }

    public static Program parseFile(Path path) throws Exception {
        String source = Files.readString(path, StandardCharsets.UTF_8);
        return parse(source);
    }
}
