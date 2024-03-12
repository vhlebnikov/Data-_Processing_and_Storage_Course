package ru.nsu.khlebnikov;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Persons implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<Person> persons;
    private final int initialCount;

    public Persons(int initialCount) {
        this.persons = new ArrayList<>();
        this.initialCount = initialCount;
    }

    public void addPerson(Person person) {
        List<Person> existingPersons = persons.parallelStream().filter(p -> p.equals(person)).toList();
        if (!existingPersons.isEmpty()) {
            for (Person p : existingPersons) {
                if (p.merge(person)) {
                    return;
                } else if (person.getId() != null && person.getId().equals(p.getId())) {
                    return;
                }
            }
        }
        persons.add(person);
    }

    public int getInitialCount() {
        return initialCount;
    }

    public int getPersonsCount() {
        return persons.size();
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<Person> getPersonByFullName(String firstname, String surname) {
        return persons.parallelStream().filter(p -> p.getFirstname() != null && p.getSurname() != null &&
                p.getFirstname().equals(firstname) && p.getSurname().equals(surname)).toList();
    }

    public List<Person> getPersonById(String id) {
        return persons.parallelStream().filter(p -> p.getId() != null && p.getId().equals(id)).toList();
    }

    public void serialize(String fileName) {
        System.out.println("Serializing was started");
        try {
            Path saves = Path.of("saves");
            if (!Files.exists(saves)) {
                Files.createDirectories(saves);
            }
            FileOutputStream fileOutputStream = new FileOutputStream("saves/" + fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            fileOutputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Serializing done.");
    }

    public static Persons deserialize(String path) {
        System.out.println("Deserializing was started");
        Persons res = null;
        try (FileInputStream fileInputStream = new FileInputStream(path);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            res = (Persons) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Can't find file");
        }
        System.out.println("Deserializing done.");
        return res;
    }
}
