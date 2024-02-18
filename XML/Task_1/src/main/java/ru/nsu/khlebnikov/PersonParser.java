package ru.nsu.khlebnikov;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PersonParser {
    private static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private static XMLEventReader reader;

    public static void parse(String path) {
        try {
            try {
                reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();

                }
            } finally {
                reader.close();
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
