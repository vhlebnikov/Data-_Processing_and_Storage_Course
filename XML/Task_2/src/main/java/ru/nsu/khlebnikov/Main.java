package ru.nsu.khlebnikov;

import ru.nsu.khlebnikov.parser.*;

import java.io.File;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
//        String filePath = "src/main/resources/people.xml";
//        PersonParser.parse(filePath);
//        PersonParser.getPersons().serialize("data.ser");
//
//        PersonParser.setPersons(Persons.deserialize("saves/data.ser"));
//
//        PersonParser.getFamilyInfo();
//        PersonParser.normalize();
//        PersonParser.getPersons().serialize("data_normalized.ser");

        PersonParser.setPersons(Persons.deserialize("saves/data_normalized.ser"));
        PersonParser.validateConsistency();
        PersonParser.simpleWrite("data_xml.xml");
        PersonParser.writeAndValidate("src/main/resources/xsd/schema.xsd", "saves/validated_xml.xml");
    }
}
