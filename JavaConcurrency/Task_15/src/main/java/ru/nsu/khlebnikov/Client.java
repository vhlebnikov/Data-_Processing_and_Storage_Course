package ru.nsu.khlebnikov;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client {
    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static String userInput;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("<server_port>");
            return;
        }

        int serverPort = Integer.parseInt(args[0]);

        try {
            try {
                clientSocket = new Socket("127.0.0.1", serverPort);
                reader = new BufferedReader(new InputStreamReader(System.in));
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                while (true) {
                    System.out.print("Enter your message: ");
                    userInput = reader.readLine();
                    out.write(userInput + '\n');
                    out.flush();

                    String serverResponse = in.readLine();
                    System.out.println("Server response: " + serverResponse);
                    if (userInput.equals("stop")) {
                        break;
                    }
                }
            } catch (SocketException e) {
                System.out.println("Server was disconnected from you");
            } finally {
                reader.close();
                in.close();
                out.close();
                clientSocket.close();
                System.out.println("Client was disconnected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
