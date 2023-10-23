package ru.nsu.khlebnikov;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class ProxyServer {
    private static String host;
    private static int port;
    private static ServerSocket proxySocket;
    private static Socket serverSocket;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("<proxy_host> <proxy_port>");
            return;
        }

        host = args[0];
        port = Integer.parseInt(args[1]);
        InetSocketAddress address = new InetSocketAddress(host, port);

        try {
            proxySocket = new ServerSocket();
            proxySocket.bind(address);
            System.out.println("Listening on port " + port + " and host " + host);

            try {
                serverSocket = proxySocket.accept();
                in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));

                while (true) {
                    String clientIn = in.readLine();
                    System.out.println("Message from " + serverSocket.getInetAddress() + ":" + serverSocket.getPort() +
                            " = " + clientIn);
                    if (clientIn.equals("stop")) {
                        out.write("I will disconnect ASAP" + '\n');
                        out.flush();
                        break;
                    }
                    out.write("You entered: " + clientIn + '\n');
                    out.flush();
                }
            } finally {
                in.close();
                out.close();
                serverSocket.close();
                proxySocket.close();
                System.out.println("Proxy server was disconnected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
