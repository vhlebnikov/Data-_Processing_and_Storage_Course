package ru.nsu.khlebnikov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static LinkedBlockingQueue<String> lines = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        URL url = new URL(scanner.nextLine());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        new Thread(new Reader(reader, lines)).start();

        Thread.sleep(1000);

        int iterations = 0;
        while (!lines.isEmpty()) {
            if (iterations == 25) {
                System.out.println("Press space to scroll down");
                iterations++;
            } else if (iterations > 25) {
                if (scanner.nextLine().equals(" ")) {
                    iterations = 0;
                }
            } else {
                System.out.println(lines.poll());
                iterations++;
            }
        }

    }
}

// http://rsdn.org