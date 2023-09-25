package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;

public class ChildTask implements Runnable{
    List<String> inputParameters;

    public ChildTask(List<String> inputParameters) {
        this.inputParameters = new ArrayList<>(inputParameters);
    }

    @Override
    public void run() {
        for (String s : inputParameters) {
            System.out.println("String by " + Thread.currentThread().getId() + " thread:" + s);
        }
    }
}
