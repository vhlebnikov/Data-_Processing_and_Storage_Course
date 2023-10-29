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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

public class Server {
    private static ServerSocket serverSocket;
    private static ServerSocketChannel serverSocketChannel;
    private static Selector selector;
    private static final int BUFFER_SIZE = 2048;
    private static final String regex = "Client\\s+\\/\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d+\\s+was\\s+disconnected";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("<server_host> <server_port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        Pattern pattern = Pattern.compile(regex);

        try {
            try {
                selector = Selector.open();

                InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
                serverSocketChannel = ServerSocketChannel.open();
                serverSocket = serverSocketChannel.socket();
                serverSocket.bind(inetSocketAddress);

                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(selector, serverSocketChannel.validOps());

                System.out.println("Server started at host/port: " + host + "/" + port);

                while (selector.select() > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            SocketChannel client = serverSocketChannel.accept();
                            System.out.println("Client " + client.getRemoteAddress() + " connected");
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                            client.read(buffer);
                            String message = new String(buffer.array()).trim();

                            if (pattern.matcher(message).find()) {
                                System.out.println(message);
                                buffer = ByteBuffer.allocate(BUFFER_SIZE);
                                buffer.put("stop".getBytes());
                            } else {
                                System.out.println("Message from " + client.getRemoteAddress() + ": " + message);
                                buffer.clear();
                                buffer.put(("Server got message: " + message).getBytes());
                            }
                            buffer.flip();
                            client.write(buffer);
                        }
                    }
                }

            } finally {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (selector != null) {
                    selector.close();
                }
                System.out.println("Server was disconnected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
