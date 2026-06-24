package com.lab.robot.interpreter.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ProjectPaths {

    public static final String DEFAULT_LABYRINTH = "src/main/resources/labyrinth.txt";
    public static final String MAIN_PROGRAM = "robot-interpreter/src/main/resources/programs/solve.robot";
    public static final String TEST_PROGRAM = "robot-interpreter/src/main/resources/programs/test.robot";

    public static final String DEFAULT_PROGRAM = MAIN_PROGRAM;

    private ProjectPaths() {
    }

    public static Path resolveFirstExisting(String... candidates) {
        for (String candidate : candidates) {
            Path path = Path.of(candidate).toAbsolutePath().normalize();
            if (Files.isRegularFile(path)) {
                return path;
            }
        }
        List<String> tried = List.of(candidates);
        throw new IllegalArgumentException("File not found. Tried: " + tried);
    }

    public static Path defaultLabyrinthFile() {
        return resolveFirstExisting(
                DEFAULT_LABYRINTH,
                "../" + DEFAULT_LABYRINTH,
                "../../" + DEFAULT_LABYRINTH
        );
    }

    public static Path defaultRobotProgram() {
        return mainRobotProgram();
    }

    public static Path mainRobotProgram() {
        return resolveFirstExisting(
                MAIN_PROGRAM,
                "src/main/resources/programs/solve.robot",
                "../" + MAIN_PROGRAM
        );
    }

    public static Path testRobotProgram() {
        return resolveFirstExisting(
                TEST_PROGRAM,
                "src/main/resources/programs/test.robot",
                "../" + TEST_PROGRAM
        );
    }
}
