package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>(List.of("1", "2", "3", "4", "5", "6", "7", "8"));
        ExecutorService es = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            es.submit(new ChildTask(strings.subList(i * 2, i * 2 + 2)));
        }
        es.shutdown();
    }
}
