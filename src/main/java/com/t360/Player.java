package com.t360;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Player {
    private static final int PLAYER_PORT = 9999;
    private static final int INITIATOR_PORT = 9998;
    private static final String HOST = "localhost";

    private static final int MAX_MESSAGES = 10;
    private static final String MESSAGE_BODY = "Message";

    private static int sent = 0;
    private static int received = 0;


    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Please specify the player type");
            System.out.println("Usage: java Player [player/initiator]");
            System.exit(1);
        }

        if (!args[0].equalsIgnoreCase("player")
                && !args[0].equalsIgnoreCase("initiator")) {
            System.err.println("Unknown type");
            System.exit(1);
        }

        boolean isInitiator = args[0].equalsIgnoreCase("initiator");

        int sourcePort;
        int responsePort;

        if (isInitiator) {
            sourcePort = INITIATOR_PORT;
            responsePort = PLAYER_PORT;
            sendMessage(MESSAGE_BODY, responsePort);
        } else {
            sourcePort = PLAYER_PORT;
            responsePort = INITIATOR_PORT;
        }

        try (ServerSocket listener = new ServerSocket(sourcePort)) {
            while (received < MAX_MESSAGES) {
                try (Socket socket = listener.accept()) {
                    received++;
                    Scanner in = new Scanner(socket.getInputStream());
                    String receivedMessage = in.nextLine();
                    System.out.println(receivedMessage);

                    if (sent < MAX_MESSAGES) {
                        sendMessage(receivedMessage + " " + sent, responsePort);
                    }
                }
            }
        }
    }

    public static void sendMessage(String message, int port) throws IOException {
        try (Socket socket = new Socket(HOST, port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            sent++;
        }
    }
}
