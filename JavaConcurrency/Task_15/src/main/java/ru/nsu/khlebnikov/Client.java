package ru.nsu.khlebnikov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static SocketChannel clientSocketChannel;
    private static ByteBuffer buffer;
    private static BufferedReader reader;
    private static String userInput = "";
    private static final int BUFFER_SIZE = 2048;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("<server_port>");
            return;
        }

        int serverPort = Integer.parseInt(args[0]);

        try {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), serverPort);
                clientSocketChannel = SocketChannel.open(inetSocketAddress);
                reader = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Hello! You've connected to the server");

                boolean exitFlag = false;

                while (!exitFlag) {
                    System.out.print("Enter your message: ");
                    while (userInput.isEmpty()) {
                        userInput = reader.readLine();
                    }

                    if (userInput.equals("stop")) {
                        exitFlag = true;
                    }

                    buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    buffer.put(userInput.getBytes());
                    userInput = "";
                    buffer.flip();
                    clientSocketChannel.write(buffer);

                    buffer.clear();
                    clientSocketChannel.read(buffer);
                    buffer.flip();
                    System.out.println("Server response: " + new String(buffer.array()).trim());
                }
            } catch (IOException e) {
                System.out.println("Server was disconnected from you");
            } finally {
                if (clientSocketChannel != null) {
                    clientSocketChannel.close();
                }
                if (reader != null) {
                    reader.close();
                }
                System.out.println("Client was disconnected");
            }
        } catch (IOException e) {
            System.out.println("Can't close channels");
        }
    }
}
