package com.lab.robot.common.console;

import com.lab.robot.common.protocol.Position;

public final class ConsoleRenderer {

    public static void render(char[][] layer, Position robotPos, int z) {
        render(layer, robotPos, z, true);
    }

    public static void render(char[][] layer, Position robotPos, int z, boolean showControls) {
        clearScreen();
        int sy = layer.length;
        int sx = layer[0].length;

        System.out.printf("Layer Z=%d (robot at %s)%n", z, robotPos);
        System.out.println("=".repeat(sx + 2));
        for (int y = sy - 1; y >= 0; y--) {
            System.out.print("|");
            for (int x = 0; x < sx; x++) {
                if (x == robotPos.getX() && y == robotPos.getY() && z == robotPos.getZ())
                    System.out.print('R');
                else
                    System.out.print(layer[y][x]);
            }
            System.out.println("|");
        }
        System.out.println("=".repeat(sx + 2));
        if (showControls) {
            System.out.println("Controls: W/A/S/D/Q/E move, R <dir> measure, P position, X exit");
        }
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}