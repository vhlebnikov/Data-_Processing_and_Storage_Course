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
            try {
                
            } finally {

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
