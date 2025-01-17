package ru.nsu.khlebnikov;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class PersonParser {
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private static final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private static XMLEventReader reader;
    private static XMLStreamWriter writer;
    private static Persons persons;

    public static void parse(String path) {
        try {
            try {
                System.out.println("Started parse file: " + path);
                long totalLines = ProgressBar.getLinesInXML(path);
                System.out.println("Total lines: " + totalLines);

                reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));
                Map<String, List<String>> values = new HashMap<>();
                String lastElementName = null;

                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    long currentLine = ProgressBar.getCurrentLineInXML(event);
                    ProgressBar.printProgress(currentLine, totalLines);
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
                            List<String> fullName = splitFullName(name);
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
                            spoucesNames.forEach(s -> newPerson.setSpouce(createPersonUsingFullName(s)));
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
                            List<String> siblingsNumbers =
                                    values.get("siblings-number").stream().map(String::trim).distinct().toList();
                            siblingsNumbers.forEach(n -> newPerson.setSiblingsNumber(Integer.parseInt(n)));
                        }
                        if (values.containsKey("children-number")) {
                            List<String> childrenNumbers =
                                    values.get("children-number").stream().map(String::trim).distinct().toList();
                            childrenNumbers.forEach(n -> newPerson.setChildrenNumber(Integer.parseInt(n)));
                        }
                        if (values.containsKey("father")) {
                            List<String> fathers =
                                    values.get("father").stream().map(String::trim).distinct().toList();
                            fathers.forEach(f -> newPerson.setFather(createPersonUsingFullName(f)));
                        }
                        if (values.containsKey("mother")) {
                            List<String> mothers =
                                    values.get("mother").stream().map(String::trim).distinct().toList();
                            mothers.forEach(f -> newPerson.setMother(createPersonUsingFullName(f)));
                        }
                        if (values.containsKey("sister")) {
                            List<String> sisters =
                                    values.get("sister").stream().map(String::trim).distinct().toList();
                            sisters.forEach(s -> newPerson.addSister(createPersonUsingFullName(s)));
                        }
                        if (values.containsKey("brother")) {
                            List<String> brothers =
                                    values.get("brother").stream().map(String::trim).distinct().toList();
                            brothers.forEach(b -> newPerson.addBrother(createPersonUsingFullName(b)));
                        }
                        if (values.containsKey("family-name")) {
                            List<String> familyNames =
                                    values.get("family-name").stream().map(String::trim).distinct().toList();
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
                            children.forEach(c -> newPerson.addChild(createPersonUsingFullName(c)));
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
                System.out.println("Parsing done.");
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(String path) {
        System.out.println("Started writing data to XML");
        long totalPersons = persons.getPersonsCount();
        long currentPersonNumber = 0;

        try {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                writer = xmlOutputFactory.createXMLStreamWriter(out);
                writer.writeStartDocument("utf-8", "1.0");
                writer.writeStartElement("people");
                writer.writeAttribute("count", String.valueOf(persons.getPersonsCount()));
                for (Person person : persons.getPersons()) {
                    ProgressBar.printProgress(currentPersonNumber++, totalPersons);
                    writer.writeStartElement("person");
                    if (person.getId() != null) {
                        writer.writeStartElement("id");
                        writer.writeCharacters(person.getId());
                        writer.writeEndElement();
                    }
                    if (person.getFirstname() != null) {
                        writer.writeStartElement("firstname");
                        writer.writeCharacters(person.getFirstname());
                        writer.writeEndElement();
                    }
                    if (person.getSurname() != null) {
                        writer.writeStartElement("surname");
                        writer.writeCharacters(person.getSurname());
                        writer.writeEndElement();
                    }
                    if (person.getGender() != null) {
                        writer.writeStartElement("gender");
                        writer.writeCharacters(person.getGender());
                        writer.writeEndElement();
                    }
                    if (!person.getSiblings().isEmpty()) {
                        writer.writeStartElement("siblings");
                        for (Person sibling : person.getSiblings()) {
                            switch (sibling.getGender()) {
                                case "male" -> writer.writeStartElement("brother");
                                case "female" -> writer.writeStartElement("sister");
                            }
                            if (sibling.getId() != null) {
                                writer.writeStartElement("id");
                                writer.writeCharacters(sibling.getId());
                                writer.writeEndElement();
                            }
                            if (sibling.getFirstname() != null) {
                                writer.writeStartElement("firstname");
                                writer.writeCharacters(sibling.getFirstname());
                                writer.writeEndElement();
                            }
                            if (sibling.getSurname() != null) {
                                writer.writeStartElement("surname");
                                writer.writeCharacters(sibling.getSurname());
                                writer.writeEndElement();
                            }

                            writer.writeEndElement();
                        }
                        writer.writeEndElement();
                    }
                    if (!person.getChildren().isEmpty()) {
                        writer.writeStartElement("children");
                        for (Person child : person.getChildren()) {
                            switch (child.getGender()) {
                                case "male" -> writer.writeStartElement("son");
                                case "female" -> writer.writeStartElement("daughter");
                            }
                            if (child.getId() != null) {
                                writer.writeStartElement("id");
                                writer.writeCharacters(child.getId());
                                writer.writeEndElement();
                            }
                            if (child.getFirstname() != null) {
                                writer.writeStartElement("firstname");
                                writer.writeCharacters(child.getFirstname());
                                writer.writeEndElement();
                            }
                            if (child.getSurname() != null) {
                                writer.writeStartElement("surname");
                                writer.writeCharacters(child.getSurname());
                                writer.writeEndElement();
                            }

                            writer.writeEndElement();
                        }
                        writer.writeEndElement();
                    }
                    if (person.getSpouce() != null) {
                        Person spouce = person.getSpouce();
                        writer.writeStartElement("spouce");
                        String gender =
                                spouce.getGender() != null ?
                                        (spouce.getGender().equals("male") ?
                                                "husband" : (spouce.getGender().equals("female") ?
                                                "wife" : null)) : null;
                        if (gender != null) {
                            writer.writeStartElement(gender);
                        }
                        if (spouce.getId() != null) {
                            writer.writeStartElement("id");
                            writer.writeCharacters(spouce.getId());
                            writer.writeEndElement();
                        }
                        if (spouce.getFirstname() != null) {
                            writer.writeStartElement("firstname");
                            writer.writeCharacters(spouce.getFirstname());
                            writer.writeEndElement();
                        }
                        if (spouce.getSurname() != null) {
                            writer.writeStartElement("surname");
                            writer.writeCharacters(spouce.getSurname());
                            writer.writeEndElement();
                        }
                        if (gender != null) {
                            writer.writeEndElement();
                        }
                        writer.writeEndElement();
                    }
                    if (!person.getParents().isEmpty()) {
                        writer.writeStartElement("parents");
                        for (Person parent : person.getParents()) {
                            switch (parent.getGender()) {
                                case "male" -> writer.writeStartElement("father");
                                case "female" -> writer.writeStartElement("mother");
                            }
                            if (parent.getId() != null) {
                                writer.writeStartElement("id");
                                writer.writeCharacters(parent.getId());
                                writer.writeEndElement();
                            }
                            if (parent.getFirstname() != null) {
                                writer.writeStartElement("firstname");
                                writer.writeCharacters(parent.getFirstname());
                                writer.writeEndElement();
                            }
                            if (parent.getSurname() != null) {
                                writer.writeStartElement("surname");
                                writer.writeCharacters(parent.getSurname());
                                writer.writeEndElement();
                            }
                            writer.writeEndElement();
                        }
                        writer.writeEndElement();
                    }
                    writer.writeEndElement();

                }
                writer.writeEndElement();
                writer.flush();

                String xml = new String(out.toByteArray(), StandardCharsets.UTF_8);
                String prettyXML = formatXML(xml);

                Files.writeString(Paths.get("saves/" + path), prettyXML, StandardCharsets.UTF_8);
            } finally {
                writer.close();
            }
        } catch (XMLStreamException | TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
        ProgressBar.printProgress(-1, totalPersons);
        System.out.println("Writing to XML done.");
    }

    public static void findDuplicates() {
        System.out.println("Search has been started");
        System.out.println("Initial persons count: " + persons.getInitialCount());
        long totalPersons = persons.getPersonsCount();
        long currentPersonNumber = 0;
        System.out.println("Current persons count: " + totalPersons);
        List<Person> allPersons = persons.getPersons();
        List<List<Person>> duplicates = new ArrayList<>();
        for (Person person : allPersons) {
            ProgressBar.printProgress(currentPersonNumber++, totalPersons);
            List<Person> existingPersons = allPersons.parallelStream().filter(p -> p.equals(person)).toList();
            if (existingPersons.size() >= 2) {
                duplicates.add(existingPersons);
            }
        }
        ProgressBar.printProgress(-1, totalPersons);
        if (!duplicates.isEmpty()) {
            System.out.println("Duplicates:");
            duplicates.forEach(list -> {
                System.out.println(list.size() + " duplicates like this:");
                list.forEach(System.out::println);
            });
        } else {
            System.out.println("There are no duplicates");
        }
    }

    public static void normalize() {
        System.out.println("Normalizing has been started");
        long totalPersons = persons.getPersonsCount();
        long currentPersonNumber = 0;
        List<Person> allPersons = persons.getPersons();
        for (Person person : allPersons) {
            ProgressBar.printProgress(currentPersonNumber++, totalPersons);

            List<Person> equalPersons = persons.getPersons().parallelStream().filter(p -> p.weakEquals(person)).toList();

            if (equalPersons.size() > 1) {
                // Siblings part
                List<Person> personSiblings = person.getSiblings();

                if (!personSiblings.isEmpty()) {
                    List<Person> realSiblings =
                            persons.getPersons().parallelStream().
                                    filter(p -> personSiblings.stream().anyMatch(s -> s.equals(p))).toList();

                    for (Person realSibling : realSiblings) {
                        List<Person> siblingsSiblings = realSibling.getSiblings();
                        if (siblingsSiblings.stream().noneMatch(s -> s.equals(person))) {
                            Person realOwnerOfSibling =
                                    equalPersons.stream().filter(p -> siblingsSiblings.stream().
                                            anyMatch(s -> s.equals(p))).findFirst().orElse(null);

                            if (realOwnerOfSibling != null) {
                                Person realSiblingMainInfo = realSibling.saveOnlyMainInfo();
                                realOwnerOfSibling.addSibling(realSiblingMainInfo);
                                person.removeSibling(realSibling);
                                if (realSiblingMainInfo.getGender() != null) {
                                    switch (realSiblingMainInfo.getGender()) {
                                        case "male" -> {
                                            realOwnerOfSibling.addBrother(realSiblingMainInfo);
                                            person.removeBrother(realSibling);
                                        }
                                        case "female" -> {
                                            realOwnerOfSibling.addSister(realSiblingMainInfo);
                                            person.removeSister(realSibling);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Children part

            }
        }
        ProgressBar.printProgress(-1, totalPersons);
        System.out.println("Normalizing done.");
    }

    private static String formatXML(String xml) throws TransformerException {

        // write data to xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // pretty print by indention
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // add standalone="yes", add line break before the root element
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        StreamSource source = new StreamSource(new StringReader(xml));
        StringWriter output = new StringWriter();
        transformer.transform(source, new StreamResult(output));

        return output.toString();

    }

    public static void getFamilyInfo() {
        System.out.println("Getting additional family info has been started");
        long totalPersons = persons.getPersonsCount();
        long currentPersonNumber = 0;
        List<Person> allPersons = persons.getPersons();
        for (Person person : allPersons) {
            ProgressBar.printProgress(currentPersonNumber++, totalPersons);

            // Siblings: sisters and brothers
            List<Person> allSiblings = new ArrayList<>();
            if (!person.getSiblings().isEmpty()) {
                person.getSiblings().forEach(s -> addPersonToList(s, allSiblings));
            }
            if (!person.getSisters().isEmpty()) {
                person.getSisters().forEach(s -> {
                    s.setGender("female");
                    addPersonToList(s, allSiblings);
                });
            }
            if (!person.getBrothers().isEmpty()) {
                person.getBrothers().forEach(b -> {
                    b.setGender("male");
                    addPersonToList(b, allSiblings);
                });
            }

            findAndMerge(allSiblings);

            if (!allSiblings.isEmpty()) {
                List<Person> allSiblingsMainInfo = new ArrayList<>();
                allSiblings.forEach(s -> allSiblingsMainInfo.add(s.saveOnlyMainInfo()));

                person.setSiblings(allSiblingsMainInfo);

                List<Person> sisters =
                        allSiblingsMainInfo.stream().filter(s -> s.getGender() != null && s.getGender().equals("female")).toList();
                person.setSisters(sisters);

                List<Person> brothers =
                        allSiblingsMainInfo.stream().filter(s -> s.getGender() != null && s.getGender().equals("male")).toList();
                person.setBrothers(brothers);
            }

            // Children: sons and daughters
            List<Person> allChildren = new ArrayList<>();
            if (!person.getChildren().isEmpty()) {
                person.getChildren().forEach(c -> addPersonToList(c, allChildren));
            }
            if (!person.getSons().isEmpty()) {
                person.getSons().forEach(s -> {
                    s.setGender("male");
                    addPersonToList(s, allChildren);
                });
            }
            if (!person.getDaughters().isEmpty()) {
                person.getDaughters().forEach(d -> {
                    d.setGender("female");
                    addPersonToList(d, allChildren);
                });
            }

            findAndMerge(allChildren);

            if (!allChildren.isEmpty()) {
                List<Person> allChildrenMainInfo = new ArrayList<>();
                allChildren.forEach(c -> allChildrenMainInfo.add(c.saveOnlyMainInfo()));

                person.setChildren(allChildrenMainInfo);

                List<Person> sons =
                        allChildrenMainInfo.stream().filter(c -> c.getGender() != null && c.getGender().equals("male")).toList();
                person.setSons(sons);

                List<Person> daughters =
                        allChildrenMainInfo.stream().filter(c -> c.getGender() != null && c.getGender().equals("female")).toList();
                person.setDaughters(daughters);
            }

            // Parents: father and mother
            List<Person> allParents = new ArrayList<>();
            if (!person.getParents().isEmpty()) {
                person.getParents().forEach(p -> addPersonToList(p, allParents));
            }
            if (person.getFather() != null) {
                Person father = person.getFather();
                father.setGender("male");
                addPersonToList(father, allParents);
            }
            if (person.getMother() != null) {
                Person mother = person.getMother();
                mother.setGender("female");
                addPersonToList(mother, allParents);
            }

            findAndMerge(allParents);

            if (!allParents.isEmpty()) {
                List<Person> allParentsMainInfo = new ArrayList<>();
                allParents.forEach(p -> allParentsMainInfo.add(p.saveOnlyMainInfo()));

                person.setParents(allParentsMainInfo);

                allParentsMainInfo.stream().filter(p -> p.getGender() != null &&
                        p.getGender().equals("male")).findFirst().ifPresent(person::setFather);

                allParentsMainInfo.stream().filter(p -> p.getGender() != null &&
                        p.getGender().equals("female")).findFirst().ifPresent(person::setMother);
            }

            // Spouce: husband or wife
            Person spouce = person.getSpouce();
            Person oneMoreSpouce = null;
            if (person.getWife() != null) {
                oneMoreSpouce = person.getWife();
                oneMoreSpouce.setGender("female");
            }
            if (person.getHusband() != null) {
                oneMoreSpouce = person.getHusband();
                oneMoreSpouce.setGender("male");
            }
            if (oneMoreSpouce != null) {
                if (spouce != null) {
                    spouce.merge(oneMoreSpouce);
                } else {
                    spouce = oneMoreSpouce;
                }
            }
            if (spouce != null) {
                Person finalSpouce = spouce;
                persons.getPersons().stream().filter(x -> x.equals(finalSpouce)).findFirst().ifPresent(spouce::merge);
                Person spouceMainInfo = spouce.saveOnlyMainInfo();
                person.setSpouce(spouceMainInfo);
                if (spouce.getGender() != null) {
                    switch (spouce.getGender()) {
                        case "male" -> person.setHusband(spouceMainInfo);
                        case "female" -> person.setWife(spouceMainInfo);
                    }
                }
            }
        }
        ProgressBar.printProgress(-1, totalPersons);
        System.out.println("Family infos have been updated.");
    }

    public static void validateConsistency() {
        System.out.println("Validation was started");
        for (Person person : persons.getPersons()) {
            if (person.getSiblingsNumber() != person.getSiblings().size()) {
                throw new IllegalStateException("Different numbers of siblings for person " + person.getId() + ": " +
                        person.getSiblingsNumber() + " != " + person.getSiblings().size() + "\n" + person);
            }
            if (person.getChildrenNumber() != person.getChildren().size()) {
                throw new IllegalStateException("Different numbers of children for person " + person.getId() + ": " +
                        person.getChildrenNumber() + " != " + person.getChildren().size() + "\n" + person);
            }
        }
        System.out.println("Validation was successfully completed");
    }

    private static void addPersonToList(Person person, List<Person> list) {
        if (list.isEmpty()) {
            list.add(person);
            return;
        }
        List<Person> existingPersons = list.stream().filter(p -> p.equals(person)).toList();
        if (!existingPersons.isEmpty()) {
            for (Person p : existingPersons) {
                if (p.merge(person)) {
                    return;
                }
            }
        }
        list.add(person);
    }

    private static void findAndMerge(List<Person> personsToMerge) {
        if (!personsToMerge.isEmpty()) {
            personsToMerge.forEach(p ->
                    persons.getPersons().parallelStream().
                            filter(x -> x.equals(p)).findFirst().ifPresent(p::merge));
        }
    }

    private static List<String> splitFullName(String fullName) {
        return Stream.of(fullName.split(" ")).filter(s -> !s.isBlank()).toList();
    }

    private static Person createPersonUsingFullName(String name) {
        List<String> fullName = splitFullName(name);
        Person person = new Person();
        person.setFirstname(fullName.get(0));
        person.setSurname(fullName.get(1));
        return person;
    }

    public static Persons getPersons() {
        return persons;
    }

    public static void setPersons(Persons persons) {
        PersonParser.persons = persons;
    }
}
