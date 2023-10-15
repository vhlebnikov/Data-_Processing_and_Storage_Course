package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Fork> forks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            forks.add(new Fork(i));
        }

        List<String> names = new ArrayList<>(List.of("Aristotle", "Plato", "Confucius", "Socrates", "Epicurus"));
        for (int i = 0; i < 5; i++) {
            new Thread(new Philosopher(forks.get(i), forks.get((i + 1) % 5), names.get(i))).start();
        }
    }
}
