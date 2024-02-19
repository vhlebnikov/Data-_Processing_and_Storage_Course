package ru.nsu.khlebnikov;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        PersonParser.parse("src/main/resources/people.xml");
        Attributes attributes = PersonParser.getAttributes();
        for (String s : attributes.getAttributes()) {
            System.out.println(s);
        }
    }
}
