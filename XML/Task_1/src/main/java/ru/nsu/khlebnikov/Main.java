package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
//        PersonParser.parse("src/main/resources/people_test.xml");
//        Persons persons = PersonParser.getPersons();
//        for (Person p : persons.getPersons()) {
//            System.out.println(p);
//        }
        List<String> list = new ArrayList<>();
        list.add("F");
        list.add("male");
        list.add("M");
        list = list.stream().map(g -> Objects.equals(g, "F") ? g = "female" : (Objects.equals(g, "M") ? g = "male" : g)).toList();
        list.forEach(System.out::println);
    }
}
