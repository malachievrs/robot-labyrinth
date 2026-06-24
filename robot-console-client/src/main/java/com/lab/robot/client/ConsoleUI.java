package com.lab.robot.client;

import com.lab.robot.common.console.ConsoleRenderer;
import com.lab.robot.common.protocol.LabyrinthMap;
import com.lab.robot.common.protocol.Position;
import com.lab.robot.common.protocol.RobotClient;
import com.lab.robot.common.protocol.RobotException;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleUI {
    private final RobotClient client;
    private LabyrinthMap map;
    private Position currentPos;
    private final CommandExecutor executor;
    private final Scanner scanner;

    public ConsoleUI(String host, int port) throws IOException, RobotException {
        client = new RobotClient(host, port);
        client.connect();
        executor = new CommandExecutor(client);
        scanner = new Scanner(System.in);

        map = client.getLabyrinth();

        CommandResult posRes = executor.execute(new Command(Command.Type.POSITION, null));
        if (posRes.getNewPosition() != null)
            currentPos = posRes.getNewPosition();
        else
            currentPos = new Position(0, 0, 0);
    }

    public void run() {
        render();
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            Command cmd = CommandParser.parse(line);
            CommandResult result = executor.execute(cmd);

            if (cmd.getType() == Command.Type.EXIT) {
                System.out.println("Goodbye!");
                break;
            }

            if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                System.out.println(result.getMessage());
            }

            switch (cmd.getType()) {
                case MOVE:
                    if (result.isSuccess()) {
                        currentPos = result.getNewPosition();
                    }
                    break;
                case POSITION:
                    if (result.getNewPosition() != null) {
                        currentPos = result.getNewPosition();
                        System.out.printf("Position: %s | exit: %b | obstacle: %b%n",
                                currentPos, result.isExit(), result.isObstacle());
                    }
                    break;
                case MEASURE:
                    if (result.isSuccess()) {
                        System.out.println("Distance: " + result.getDistance());
                    }
                    break;
                default:
                    break;
            }

            render();
        }
    }

    private void render() {
        char[][] layer = map.getLayer(currentPos.getZ());
        ConsoleRenderer.render(layer, currentPos, currentPos.getZ());
    }

    public void close() {
        try { client.close(); } catch (Exception ignored) {}
        scanner.close();
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        ConsoleUI ui = null;
        try {
            ui = new ConsoleUI(host, port);
            Runtime.getRuntime().addShutdownHook(new Thread(ui::close));
            ui.run();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (ui != null) ui.close();
        }
    }
}