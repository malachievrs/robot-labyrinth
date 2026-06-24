package com.lab.robot.interpreter.main;

import com.lab.robot.common.protocol.RobotClient;
import com.lab.robot.interpreter.InterpreterException;
import com.lab.robot.interpreter.ParsePipeline;
import com.lab.robot.interpreter.analysis.SemanticAnalyzer;
import com.lab.robot.interpreter.ast.Program;
import com.lab.robot.interpreter.ast.ProgramAstPrinter;
import com.lab.robot.interpreter.runtime.ConsoleRobotRuntime;
import com.lab.robot.interpreter.runtime.Interpreter;
import com.lab.robot.interpreter.runtime.RobotRuntime;
import com.lab.robot.interpreter.util.ProjectPaths;

import java.nio.file.Path;

public final class InterpreterMain {

    public static void main(String[] args) {
        Path programPath = ProjectPaths.defaultRobotProgram();
        String host = "localhost";
        int port = 8080;
        boolean quiet = false;
        boolean noMap = false;

        if (args.length > 0 && !args[0].startsWith("--")) {
            programPath = Path.of(args[0]);
        }

        for (int i = 0; i < args.length; i++) {
            if ("--host".equals(args[i]) && i + 1 < args.length) {
                host = args[++i];
            } else if ("--port".equals(args[i]) && i + 1 < args.length) {
                port = Integer.parseInt(args[++i]);
            } else if ("--quiet".equals(args[i])) {
                quiet = true;
            } else if ("--no-map".equals(args[i])) {
                noMap = true;
            }
        }

        try {
            System.out.println("=== Robot Interpreter ===");
            System.out.println("Program: " + programPath.toAbsolutePath());
            System.out.println("Server:  " + host + ":" + port);
            System.out.println();

            Program program = ParsePipeline.parseFile(programPath);
            if (isTestProgram(programPath)) {
                System.out.println("--- Program AST ---");
                ProgramAstPrinter.print(program, System.out);
                System.out.println("--- end AST ---");
                System.out.println();
            }
            new SemanticAnalyzer().analyze(program);

            RobotClient client = new RobotClient(host, port);
            client.connect();
            System.out.println("Connected to game server.");

            RobotRuntime robot = quiet
                    ? new RobotRuntime(client)
                    : new ConsoleRobotRuntime(client, !noMap);

            Interpreter interpreter = new Interpreter(robot);
            System.out.println("--- Program start ---");
            interpreter.execute(program);
            System.out.println("--- Program end ---");

            if (robot.isBroken()) {
                System.out.println("Result: robot BROKEN");
                System.exit(2);
            }
            System.out.println("Result: OK");
            client.disconnect();
        } catch (InterpreterException e) {
            System.err.println("Interpreter error: " + e.getMessage());
            System.exit(3);
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(4);
        }
    }

    private static boolean isTestProgram(Path programPath) {
        String fileName = programPath.getFileName().toString();
        if ("test.robot".equals(fileName)) {
            return true;
        }
        try {
            return programPath.toAbsolutePath().normalize()
                    .equals(ProjectPaths.testRobotProgram());
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
