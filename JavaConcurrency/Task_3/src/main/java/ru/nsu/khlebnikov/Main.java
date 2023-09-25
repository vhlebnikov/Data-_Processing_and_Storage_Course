package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        ExecutorService es = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            List<String> data = List.of((2 * i + 1) + " string", (2 * i + 2) + " string");
            strings.addAll(data);
            es.submit(new ChildTask(strings));
            strings.removeAll(data);
        }
        es.shutdown();
    }
}
