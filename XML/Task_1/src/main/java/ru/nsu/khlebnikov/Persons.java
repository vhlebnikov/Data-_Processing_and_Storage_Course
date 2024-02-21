package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;

public class Persons {
    private final List<Person> persons;
    private final int initialCount;

    public Persons(int initialCount) {
        this.persons = new ArrayList<>();
        this.initialCount = initialCount;
    }

    public void addPerson(Person person) {
        Person existingPerson = persons.stream().filter(p -> p.equals(person)).findFirst().orElse(null);
        if (existingPerson != null) {
            existingPerson.merge(person);
        } else {
            persons.add(person);
        }
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
}
