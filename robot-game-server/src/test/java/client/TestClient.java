package client;

import com.lab.robot.common.protocol.Position;
import com.lab.robot.common.protocol.RobotClient;
import com.lab.robot.common.protocol.RobotException;
import com.lab.robot.common.protocol.RobotResponse;

import java.io.IOException;


public class TestClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        System.out.println("Connecting to " + host + ":" + port + "...");

        try (RobotClient client = new RobotClient(host, port)) {
            client.connect();
            System.out.println("Connected.\n");

            System.out.println(">>> GET_POSITION");
            RobotResponse posResp = client.getPosition();
            printResponse(posResp);
            Position startPos = posResp.getPosition();
            if (startPos != null) {
                System.out.println("Start position: " + startPos);
            }

            System.out.println("\n>>> MEASURE FORWARD");
            RobotResponse measResp = client.measure("FORWARD");
            printResponse(measResp);
            System.out.println("Distance: " + measResp.getDistance());

            System.out.println("\n>>> MOVE UP");
            RobotResponse moveResp = client.move("UP");
            printResponse(moveResp);

            for (int i = 0; i < 3; i++) {
                System.out.println("\n>>> MOVE FORWARD " + (i+1));
                moveResp = client.move("FORWARD");
                printResponse(moveResp);
                if (!moveResp.isOk()) {
                    System.out.println("Robot crashed! Stopping moves.");
                    break;
                }
            }

            System.out.println("\n>>> GET_POSITION (after moves)");
            posResp = client.getPosition();
            printResponse(posResp);

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            e.printStackTrace();
        } catch (RobotException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void printResponse(RobotResponse resp) {
        System.out.println("Status: " + resp.getStatus());
        if (resp.getData().has("message") && !resp.getData().getString("message").isEmpty()) {
            System.out.println("Message: " + resp.getData().getString("message"));
        }
    }
}
