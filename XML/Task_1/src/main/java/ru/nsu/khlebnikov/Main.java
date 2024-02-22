package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        PersonParser.parse("src/main/resources/people.xml");
//        Persons persons = PersonParser.getPersons();
//        for (Person p : persons.getPersons()) {
//            System.out.println(p);
//        }
    }
}
