package ru.nsu.khlebnikov;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class Reader implements Runnable {
    private final BufferedReader reader;
    private final LinkedBlockingQueue<String> lines;

    public Reader(BufferedReader reader, LinkedBlockingQueue<String> lines) {
        this.reader = reader;
        this.lines = lines;
    }

    @Override
    public void run() {
        String line;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
