package ru.nsu.khlebnikov;

import java.util.List;

public class Main {
    public static void main(String[] args) {
//        String filePath = "src/main/resources/people.xml";
//        PersonParser.parse(filePath);
//        PersonParser.getPersons().serialize("data.ser");

        PersonParser.setPersons(Persons.deserialize("saves/data.ser"));

        System.out.println("Start search (after parsing)");
        List<Person> foundPersons = PersonParser.getPersons().getPersonByFullName("Kaylene", "Startz");
        foundPersons.forEach(System.out::println);

//        PersonParser.findDuplicates();

//        PersonParser.normalize();
//        PersonParser.getPersons().serialize("data_normalized.ser");

        PersonParser.setPersons(Persons.deserialize("saves/data_normalized.ser"));

        System.out.println("Start search (after parsing)");
        foundPersons = PersonParser.getPersons().getPersonByFullName("Kaylene", "Startz");
        foundPersons.forEach(System.out::println);
    }
}
