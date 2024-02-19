package ru.nsu.khlebnikov;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PersonParser {
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private static XMLEventReader reader;
    private static final Attributes attributes = new Attributes();

    public static Attributes getAttributes() {
        return attributes;
    }

    public static void parse(String path) {
        try {
            try {
                reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    if (event.isStartElement()) {
                        StartElement startElement = event.asStartElement();
                        attributes.addAttribute(startElement.getName().getLocalPart());
                    }
                }
            } finally {
                reader.close();
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
