package ru.nsu.khlebnikov;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/resources/people.xml";
        PersonParser.parse(filePath);
        Persons persons = PersonParser.getPersons();
        persons.serialize();
        System.out.println("Start search");
        System.out.println(persons.getPersonByFullName("Kaylene", "Startz"));
    }
}
