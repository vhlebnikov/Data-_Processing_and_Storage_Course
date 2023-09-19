package ru.nsu.khlebnikov;

import java.util.List;

public class ChildTask implements Runnable{
    List<String> inputParameters;

    public ChildTask(List<String> inputParameters) {
        this.inputParameters = inputParameters;
    }

    @Override
    public void run() {
        for (String s : inputParameters) {
            System.out.println("String by " + Thread.currentThread().getId() + " thread:" + s);
        }
    }
}
