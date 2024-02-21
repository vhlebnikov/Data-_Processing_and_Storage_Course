package ru.nsu.khlebnikov;

import org.w3c.dom.Attr;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PersonParser {
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private static XMLEventReader reader;
    private static Persons persons;

    public static void parse(String path) {
        try {
            try {
                reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));
                Map<String, List<String>> values = new HashMap<>();
                String lastElementName = null;
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    if (event.isStartElement()) {
                        StartElement startElement = event.asStartElement();
                        lastElementName = startElement.getName().getLocalPart();
                        switch (lastElementName) {
                            case "people" -> {
                                int count = Integer.parseInt(startElement.
                                        getAttributeByName(QName.valueOf("count")).getValue());
                                persons = new Persons(count);
                            }
                            case "person" -> {
                                values = new HashMap<>();
                                Iterator<Attribute> iterator = startElement.getAttributes();
                                while (iterator.hasNext()) {
                                    Attribute a = iterator.next();
                                    if (!values.containsKey(a.getName().getLocalPart())) {
                                        values.put(a.getName().getLocalPart(), new ArrayList<>());
                                    }
                                    values.get(a.getName().getLocalPart()).add(a.getValue());
                                }
                            }
                            case "parent" -> {
                                Attribute attribute =
                                        event.asStartElement().getAttributeByName(QName.valueOf("value"));
                                if (attribute != null) {
                                    String value = attribute.getValue();
                                    if (!value.equals("UNKNOWN")) {
                                        assert values != null;
                                        if (!values.containsKey(lastElementName)) {
                                            values.put(lastElementName, new ArrayList<>());
                                        }
                                        values.get(lastElementName).add(value);
                                    }
                                }
                            }
                            case "spouce" -> {
                                Attribute attribute =
                                        event.asStartElement().getAttributeByName(QName.valueOf("value"));
                                if (attribute != null) {
                                    String value = attribute.getValue();
                                    if (!value.equals("NONE")) {
                                        assert values != null;
                                        if (!values.containsKey(lastElementName)) {
                                            values.put(lastElementName, new ArrayList<>());
                                        }
                                        values.get(lastElementName).add(value);
                                    }
                                }
                            }
                            case "siblings" -> {
                                Attribute attribute =
                                        event.asStartElement().getAttributeByName(QName.valueOf("val"));
                                if (attribute != null) {
                                    String value = attribute.getValue();
                                    assert values != null;
                                    if (!values.containsKey(lastElementName)) {
                                        values.put(lastElementName, new ArrayList<>());
                                    }
                                    values.get(lastElementName).add(value);
                                }
                            }
                            case "firstname", "gender", "husband", "surname", "siblings-number",
                                    "id", "wife", "children-number" -> {
                                Attribute attribute =
                                        event.asStartElement().getAttributeByName(QName.valueOf("value"));
                                if (attribute != null) {
                                    String value = attribute.getValue();
                                    assert values != null;
                                    if (!values.containsKey(lastElementName)) {
                                        values.put(lastElementName, new ArrayList<>());
                                    }
                                    values.get(lastElementName).add(value);
                                }
                            }
                            case "father", "mother", "children", "sister", "brother",
                                    "family-name", "fullname", "family", "first", "child" -> {
                            }
                            case "son", "daughter" -> {
                                Attribute attribute =
                                        event.asStartElement().getAttributeByName(QName.valueOf("id"));
                                if (attribute != null) {
                                    String value = attribute.getValue();
                                    assert values != null;
                                    if (!values.containsKey(lastElementName)) {
                                        values.put(lastElementName, new ArrayList<>());
                                    }
                                    values.get(lastElementName).add(value);
                                }
                            }
                        }
                    }
                    if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
                        assert values != null;
                        assert lastElementName != null;
                        String data = event.asCharacters().getData();
                        if (lastElementName.equals("parent")) {
                            if (!data.equals("UNKNOWN")) {
                                if (!values.containsKey(lastElementName)) {
                                    values.put(lastElementName, new ArrayList<>());
                                }
                                values.get(lastElementName).add(data);
                            }
                        } else {
                            if (!values.containsKey(lastElementName)) {
                                values.put(lastElementName, new ArrayList<>());
                            }
                            values.get(lastElementName).add(data);
                        }
                    }
                    if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("person")) {
                        // тут логика с унификацией данных каждого поля
                        Person newPerson = new Person();

                        if (values.containsKey("id")) {
                            List<String> ids = values.get("id").stream().map(String::trim).toList();
                            if (!ids.stream().allMatch(ids.get(0)::equals)) {
                                throw new IllegalStateException("Person can't have two different ids");
                            }
                            newPerson.setId(values.get("id").get(0));
                        }
                        if (values.containsKey("name")) {
                            String name = values.get("name").get(0).trim();
                            String firstname = name.split(" ")[0];
                            String surname = name.split(" ")[1];
                            newPerson.setFirstname(firstname);
                            newPerson.setSurname(surname);
                        }
                        if (values.containsKey("parent")) {
                            List<String> parents = values.get("parent").stream().map(String::trim).toList();
                            parents.forEach(p -> {
                                Person parent = new Person();
                                parent.setId(p);
                                newPerson.addParent(parent);
                            });
                        }
                        if (values.containsKey("spouce")) {
                            String spouceName = values.get("spouce").get(0).trim();
                            String firstname = spouceName.split(" ")[0];
                            String surname = spouceName.split(" ")[1];
                            Person spouce = new Person();
                            spouce.setFirstname(firstname);
                            spouce.setSurname(surname);
                            newPerson.setSpouce(spouce);
                        }
                        if (values.containsKey("siblings")) {
                            List<String> siblingsIds = List.of(values.get("siblings").get(0).trim().split(" "));
                            siblingsIds.forEach(s -> {
                                Person sibling = new Person();
                                sibling.setId(s);
                                newPerson.addSibling(sibling);
                            });
                        }
                        if (values.containsKey("firstname")) {
                            List<String> firstnames = values.get("firstname").stream().map(String::trim).toList();
                            if (firstnames.size() >= 2 && !firstnames.stream().allMatch(firstnames.get(0)::equals)) {
                                throw new IllegalStateException("Ids must be equal");
                            }
                            if (newPerson.getFirstname() != null && !newPerson.getFirstname().equals(firstnames.get(0))) {
                                throw new IllegalStateException("Different firstnames for one person");
                            }
                            newPerson.setFirstname(firstnames.get(0));
                        }
                        if (values.containsKey("gender")) {
                            List<String> genders = values.get("gender").stream().map(String::trim).toList();
                            if (genders.size() >= 2) {
                                throw new IllegalStateException("Person can't have two genders, but in our time all is possible");
                            }
                            // Вот этот момент возможно лучше облегчить (сделать только для одного элемента)
                            genders = genders.stream().map(g ->
                                            Objects.equals(g, "F") ? g = "female" :
                                                    (Objects.equals(g, "M") ? g = "male" : g)).toList();
                            newPerson.setGender(genders.get(0));
                        }
                        if (values.containsKey("husband")) {

                        }
                        if (values.containsKey("surname")) {

                        }
                        if (values.containsKey("wife")) {

                        }
                        if (values.containsKey("siblings-number")) {
                            // подумать
                        }
                        if (values.containsKey("children-number")) {
                            // подумать
                        }
                        if (values.containsKey("father")) {

                        }
                        if (values.containsKey("mother")) {

                        }
                        if (values.containsKey("children")) {

                        }
                        if (values.containsKey("sister")) {

                        }
                        if (values.containsKey("brother")) {

                        }
                        if (values.containsKey("family-name")) {

                        }
                        if (values.containsKey("fullname")) {

                        }
                        if (values.containsKey("family")) {

                        }
                        if (values.containsKey("first")) {

                        }
                        if (values.containsKey("child")) {

                        }
                        if (values.containsKey("son")) {

                        }
                        if (values.containsKey("daughter")) {

                        }

                        // логика добавления одного person'а ко всем чувакам
                        persons.addPerson(newPerson);
                        values = null;
                    }
                }
            } finally {
                reader.close();
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Persons getPersons() {
        return persons;
    }
}
