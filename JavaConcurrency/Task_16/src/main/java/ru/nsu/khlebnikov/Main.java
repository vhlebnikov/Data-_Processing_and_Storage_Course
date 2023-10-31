package ru.nsu.khlebnikov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static HttpURLConnection connection;
    private static Scanner scanner;
    private static Thread readerThread;
    private static BufferedReader reader;
    public static LinkedBlockingQueue<String> lines = new LinkedBlockingQueue<>();
    private static final int LINES_IN_BLOCK = 25;

    public static void main(String[] args) {
        try {
            try {
                scanner = new Scanner(System.in);
                URL url = new URL(scanner.nextLine());

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                readerThread = new Thread(new Reader(reader, lines));
                readerThread.start();

                int iterations = 0;
                while (!lines.isEmpty()) {
                    if (iterations == LINES_IN_BLOCK) {
                        System.out.println("Press space to scroll down");
                        iterations++;
                    } else if (iterations > LINES_IN_BLOCK) {
                        if (scanner.nextLine().equals(" ")) {
                            iterations = 0;
                        }
                    } else {
                        System.out.println(lines.poll());
                        iterations++;
                    }
                }
            } finally {
                if (readerThread != null) {
                    readerThread.interrupt();
                }
                if (reader != null) {
                    reader.close();
                }
                if (scanner != null) {
                    scanner.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

// http://rsdn.org