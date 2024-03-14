package ru.nsu.khlebnikov;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/resources/people.xml";
        PersonParser.parse(filePath);
        PersonParser.getPersons().serialize("data.ser");

        PersonParser.setPersons(Persons.deserialize("saves/data.ser"));

        PersonParser.getFamilyInfo();
        PersonParser.normalize();
        PersonParser.getPersons().serialize("data_normalized.ser");

        PersonParser.setPersons(Persons.deserialize("saves/data_normalized.ser"));
        PersonParser.validateConsistency();
        PersonParser.write("data_xml.xml");
    }
}
