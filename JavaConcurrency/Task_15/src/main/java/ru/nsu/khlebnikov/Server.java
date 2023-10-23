package ru.nsu.khlebnikov;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static Socket proxySocket;
    private static BufferedReader clientIn;
    private static BufferedWriter clientOut;
    private static BufferedReader proxyIn;
    private static BufferedWriter proxyOut;

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("<server_port> <proxy_host> <proxy_port>");
            return;
        }

        int serverPort = Integer.parseInt(args[0]);
        String proxyHost = args[1];
        int proxyPort = Integer.parseInt(args[2]);

        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Listening on port " + serverPort + " and forwarding to " + proxyHost + ":" + proxyPort);

            clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            proxySocket = new Socket(proxyHost, proxyPort);
            System.out.println("Connected to proxy: " + proxyHost + ":" + proxyPort);

            clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            proxyIn = new BufferedReader(new InputStreamReader(proxySocket.getInputStream()));
            proxyOut = new BufferedWriter(new OutputStreamWriter(proxySocket.getOutputStream()));

            while (true) {
                String clientResponse = clientIn.readLine();
                System.out.println("Client response: " + clientResponse);
                proxyOut.write(clientResponse + '\n');
                proxyOut.flush();

                String proxyResponse = proxyIn.readLine();
                System.out.println("Proxy response: " + proxyResponse);
                clientOut.write(proxyResponse + '\n');
                clientOut.flush();
            }
        } catch (SocketException e) {
            System.out.println("Server was disconnected");
        } finally {
            serverSocket.close();
            if (clientSocket != null && clientSocket.isConnected()) {
                if (clientIn != null) {
                    clientIn.close();
                }
                if (clientOut != null) {
                    clientOut.close();
                }
                clientSocket.close();
            }
            if (proxySocket != null && proxySocket.isConnected()) {
                if (proxyIn != null) {
                    proxyIn.close();
                }
                if (proxyOut != null) {
                    proxyOut.close();
                }
                proxySocket.close();
            }
        }
    }
}
