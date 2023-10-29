package ru.nsu.khlebnikov;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ProxyServer {
    private static Selector selector;
    private static ServerSocketChannel proxySocketChannel;
    private static ServerSocket proxySocket;
    private static SocketChannel serverSocketChannel;
    private static final int BUFFER_SIZE = 2048;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("<proxy_port> <server_host> <server_port>");
            return;
        }

        int proxyPort = Integer.parseInt(args[0]);
        String serverHost = args[1];
        int serverPort = Integer.parseInt(args[2]);

        try {
            try {
                selector = Selector.open();

                InetSocketAddress proxyAddress = new InetSocketAddress(InetAddress.getLocalHost(), proxyPort);
                proxySocketChannel = ServerSocketChannel.open();
                proxySocket = proxySocketChannel.socket();
                proxySocket.bind(proxyAddress);
                System.out.println("Proxy server started at: " + proxySocketChannel.getLocalAddress());

                proxySocketChannel.configureBlocking(false);
                proxySocketChannel.register(selector, proxySocketChannel.validOps());

                InetSocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
                serverSocketChannel = SocketChannel.open(serverAddress);
                System.out.println("Connected to server: " + serverSocketChannel.getRemoteAddress());

                while (selector.select() > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            SocketChannel client = proxySocketChannel.accept();
                            System.out.println("Client " + client.getRemoteAddress() + " connected");
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                            SocketChannel client = (SocketChannel) key.channel();

                            client.read(buffer);
                            String message = new String(buffer.array()).trim();
                            System.out.println("Message: " + message + " from client: " + client.getRemoteAddress() +
                                    " to server: " + serverSocketChannel.getRemoteAddress());
                            if (message.equals("stop") || message.isEmpty()) {
                                buffer.clear();
                                buffer.put(("Client " + client.getRemoteAddress() + " was disconnected").getBytes());
                            }
                            buffer.flip();
                            serverSocketChannel.write(buffer);

                            buffer.clear();
                            buffer = ByteBuffer.allocate(BUFFER_SIZE);
                            serverSocketChannel.read(buffer);
                            message = new String(buffer.array()).trim();
                            System.out.println("Message: " + message + " from server: " +
                                    serverSocketChannel.getRemoteAddress() +
                                    " to client: " + client.getRemoteAddress());
                            buffer.flip();
                            client.write(buffer);

                            if (message.equals("stop")) {
                                System.out.println("Client " + client.getRemoteAddress() + " was disconnected");
                                client.close();
                            }
                        }
                    }
                }
            } finally {
                if (proxySocket != null) {
                    proxySocket.close();
                }
                if (serverSocketChannel != null) {
                    serverSocketChannel.close();
                }
                if (selector != null) {
                    selector.close();
                }
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset by peer")) {
                System.out.println("Server was disconnected");
            } else {
                throw new RuntimeException(e);
            }

        }
    }
}
