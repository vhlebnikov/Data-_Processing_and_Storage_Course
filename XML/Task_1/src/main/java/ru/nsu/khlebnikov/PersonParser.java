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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

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
                            List<String> ids = values.get("id").stream().map(String::trim).distinct().toList();
                            ids.forEach(newPerson::setId);
                        }
                        if (values.containsKey("name")) {
                            String name = values.get("name").get(0).trim();
                            List<String> fullName =
                                    Stream.of(name.split(" ")).filter(s -> !s.isBlank()).toList();
                            String firstname = fullName.get(0);
                            String surname = fullName.get(1);
                            newPerson.setFirstname(firstname);
                            newPerson.setSurname(surname);
                        }
                        if (values.containsKey("parent")) {
                            List<String> parents =
                                    values.get("parent").stream().map(String::trim).distinct().toList();
                            parents.forEach(p -> {
                                Person parent = new Person();
                                parent.setId(p);
                                newPerson.addParent(parent);
                            });
                        }
                        if (values.containsKey("spouce")) {
                            List<String> spoucesNames = values.get("spouce").stream().map(String::trim).distinct().toList();
                            spoucesNames.forEach(s -> {
                                List<String> fullName =
                                        Stream.of(s.split(" ")).filter(s1 -> !s1.isBlank()).toList();
                                String firstname = fullName.get(0);
                                String surname = fullName.get(1);
                                Person spouce = new Person();
                                spouce.setFirstname(firstname);
                                spouce.setSurname(surname);
                                newPerson.setSpouce(spouce);
                            });
                        }
                        if (values.containsKey("siblings")) {
                            List<String> siblings = values.get("siblings").stream().map(String::trim).distinct().toList();
                            siblings.forEach(x -> {
                                List<String> siblingsIds = Stream.of(x.split(" ")).filter(s -> !s.isBlank()).distinct().toList();
                                siblingsIds.forEach(s -> {
                                    Person sibling = new Person();
                                    sibling.setId(s);
                                    newPerson.addSibling(sibling);
                                });
                            });
                        }
                        if (values.containsKey("firstname")) {
                            List<String> firstnames =
                                    values.get("firstname").stream().map(String::trim).distinct().toList();
                            firstnames.forEach(newPerson::setFirstname);
                        }
                        if (values.containsKey("gender")) {
                            List<String> genders = values.get("gender").stream().map(String::trim).toList();
                            genders = genders.stream().map(g ->
                                    Objects.equals(g, "F") ? g = "female" :
                                            (Objects.equals(g, "M") ? g = "male" : g)).distinct().toList();
                            genders.forEach(newPerson::setGender);
                        }
                        if (values.containsKey("husband")) {
                            List<String> husbandsIds =
                                    values.get("husband").stream().map(String::trim).distinct().toList();
                            husbandsIds.forEach(h -> {
                                Person husband = new Person();
                                husband.setId(h);
                                newPerson.setHusband(husband);
                            });
                        }
                        if (values.containsKey("surname")) {
                            List<String> surnames =
                                    values.get("surname").stream().map(String::trim).distinct().toList();
                            surnames.forEach(newPerson::setSurname);
                        }
                        if (values.containsKey("wife")) {
                            List<String> wifesIds =
                                    values.get("wife").stream().map(String::trim).distinct().toList();
                            wifesIds.forEach(w -> {
                                Person wife = new Person();
                                wife.setId(w);
                                newPerson.setWife(wife);
                            });
                        }
                        if (values.containsKey("siblings-number")) {
                            // подумать
                        }
                        if (values.containsKey("children-number")) {
                            // подумать
                        }
                        if (values.containsKey("father")) {
                            List<String> fathers =
                                    values.get("father").stream().map(String::trim).distinct().toList();
                            fathers.forEach(f -> {
                                String[] fullName = f.split(" ");
                                String firstname = fullName[0];
                                String surname = fullName[1];
                                Person father = new Person();
                                father.setFirstname(firstname);
                                father.setSurname(surname);
                                newPerson.setFather(father);
                            });
                        }
                        if (values.containsKey("mother")) {
                            List<String> mothers =
                                    values.get("mother").stream().map(String::trim).distinct().toList();
                            mothers.forEach(f -> {
                                List<String> fullName =
                                        Stream.of(f.split(" ")).filter(s -> !s.isBlank()).toList();
                                String firstname = fullName.get(0);
                                String surname = fullName.get(1);
                                Person mother = new Person();
                                mother.setFirstname(firstname);
                                mother.setSurname(surname);
                                newPerson.setMother(mother);
                            });
                        }
                        if (values.containsKey("sister")) {
                            List<String> sisters =
                                    values.get("sister").stream().map(String::trim).distinct().toList();
                            sisters.forEach(s -> {
                                List<String> fullName =
                                        Stream.of(s.split(" ")).filter(s1 -> !s1.isBlank()).toList();
                                String firstname = fullName.get(0);
                                String surname = fullName.get(1);
                                Person sister = new Person();
                                sister.setFirstname(firstname);
                                sister.setSurname(surname);
                                newPerson.addSister(sister);
                            });
                        }
                        if (values.containsKey("brother")) {
                            List<String> brothers =
                                    values.get("brother").stream().map(String::trim).distinct().toList();
                            brothers.forEach(b -> {
                                List<String> fullName =
                                        Stream.of(b.split(" ")).filter(s -> !s.isBlank()).toList();
                                String firstname = fullName.get(0);
                                String surname = fullName.get(1);
                                Person brother = new Person();
                                brother.setFirstname(firstname);
                                brother.setSurname(surname);
                                newPerson.addBrother(brother);
                            });
                        }
                        if (values.containsKey("family-name")) {
                            List<String> familyNames =
                                    values.get("family-names").stream().map(String::trim).distinct().toList();
                            familyNames.forEach(newPerson::setSurname);
                        }
                        if (values.containsKey("family")) {
                            List<String> familyNames =
                                    values.get("family").stream().map(String::trim).distinct().toList();
                            familyNames.forEach(newPerson::setSurname);
                        }
                        if (values.containsKey("first")) {
                            List<String> firstnames =
                                    values.get("first").stream().map(String::trim).distinct().toList();
                            firstnames.forEach(newPerson::setFirstname);
                        }
                        if (values.containsKey("child")) {
                            List<String> children =
                                    values.get("child").stream().map(String::trim).distinct().toList();
                            children.forEach(c -> {
                                List<String> fullName =
                                        Stream.of(c.split(" ")).filter(s -> !s.isBlank()).toList();
                                String firstname = fullName.get(0);
                                String surname = fullName.get(1);
                                Person child = new Person();
                                child.setFirstname(firstname);
                                child.setSurname(surname);
                                newPerson.addChild(child);
                            });
                        }
                        if (values.containsKey("son")) {
                            List<String> sonsIds = values.get("son").stream().map(String::trim).distinct().toList();
                            sonsIds.forEach(s -> {
                                Person son = new Person();
                                son.setId(s);
                                newPerson.addSon(son);
                            });
                        }
                        if (values.containsKey("daughter")) {
                            List<String> daughtersIds =
                                    values.get("daughter").stream().map(String::trim).distinct().toList();
                            daughtersIds.forEach(d -> {
                                Person daughter = new Person();
                                daughter.setId(d);
                                newPerson.addDaughter(daughter);
                            });
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
