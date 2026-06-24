package com.lab.robot.client;

public class CommandParser {
    public static Command parse(String input) {
        String line = input.trim().toLowerCase();
        if (line.isEmpty()) return new Command(Command.Type.UNKNOWN, null);
        String[] parts = line.split("\\s+");

        switch (parts[0]) {
            case "w": return new Command(Command.Type.MOVE, "UP");
            case "s": return new Command(Command.Type.MOVE, "DOWN");
            case "a": return new Command(Command.Type.MOVE, "LEFT");
            case "d": return new Command(Command.Type.MOVE, "RIGHT");
            case "q": return new Command(Command.Type.MOVE, "FORWARD");
            case "e": return new Command(Command.Type.MOVE, "BACK");
            case "up": case "down": case "left": case "right": case "forward": case "back":
                return new Command(Command.Type.MOVE, parts[0].toUpperCase());
            case "r":
                if (parts.length < 2) return new Command(Command.Type.UNKNOWN, "Usage: r <direction>");
                return new Command(Command.Type.MEASURE, parts[1].toUpperCase());
            case "p": return new Command(Command.Type.POSITION, null);
            case "x": case "exit": return new Command(Command.Type.EXIT, null);
            case "help": return new Command(Command.Type.HELP, null);
            default: return new Command(Command.Type.UNKNOWN, null);
        }
    }
}
