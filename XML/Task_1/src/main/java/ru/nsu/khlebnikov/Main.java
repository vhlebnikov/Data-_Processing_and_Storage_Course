package ru.nsu.khlebnikov;

import java.util.List;

public class Main {
    public static void main(String[] args) {
//        String filePath = "src/main/resources/people.xml";
//        PersonParser.parse(filePath);
//        PersonParser.getPersons().serialize();
//
        PersonParser.setPersons(Persons.deserialize("saves/save.ser"));
//        System.out.println("Start search (after parsing)");
//        List<Person> foundPersons = PersonParser.getPersons().getPersonByFullName("Kaylene", "Startz");
//        foundPersons.forEach(System.out::println);

        PersonParser.findDuplicates();

//        System.out.println("Start search (after parsing)");
//        foundPersons = PersonParser.getPersons().getPersonByFullName("Kaylene", "Startz");
//        foundPersons.forEach(System.out::println);
    }
}
