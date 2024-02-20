package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Person {
    private String id;
    private String firstname;
    private String surname;
    private String gender;
    private List<Person> sisters;
    private List<Person> brothers;
    private List<Person> sons;
    private List<Person> daughters;
    private Person husband;
    private Person wife;
    private Person father;
    private Person mother;

    public Person() {
        this.sisters = new ArrayList<>();
        this.brothers = new ArrayList<>();
        this.sons = new ArrayList<>();
        this.daughters = new ArrayList<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHusband(Person husband) {
        if (this.husband != null && !this.husband.equals(husband)) {
            throw new IllegalStateException("Person can't have more than one husband");
        }
        this.husband = husband;
    }

    public void setWife(Person wife) {
        if (this.wife != null && !this.wife.equals(wife)) {
            throw new IllegalStateException("Person can't have more than one wife");
        }
        this.wife = wife;
    }

    public void setFather(Person father) {
        if (this.father != null && !this.father.equals(father)) {
            throw new IllegalStateException("Person can't have more than one father");
        }
        this.father = father;
    }

    public void setMother(Person mother) {
        if (this.mother != null && !this.mother.equals(mother)) {
            throw new IllegalStateException("Person can't have more than one mother");
        }
        this.mother = mother;
    }

    public void addSister(Person sister) {
        if (sisters.stream().noneMatch(s -> s.equals(sister))) {
            sisters.add(sister);
        }
    }

    public void addBrother(Person brother) {
        if (brothers.stream().noneMatch(b -> b.equals(brother))) {
            brothers.add(brother);
        }
    }

    public void addSon(Person son) {
        if (sons.stream().noneMatch(s -> s.equals(son))) {
            sons.add(son);
        }
    }

    public void addDaughter(Person daughter) {
        if (daughters.stream().noneMatch(d -> d.equals(daughter))) {
            daughters.add(daughter);
        }
    }

    public String getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getGender() {
        return gender;
    }

    public List<Person> getSisters() {
        return sisters;
    }

    public List<Person> getBrothers() {
        return brothers;
    }

    public List<Person> getSons() {
        return sons;
    }

    public List<Person> getDaughters() {
        return daughters;
    }

    public Person getHusband() {
        return husband;
    }

    public Person getWife() {
        return wife;
    }

    public Person getFather() {
        return father;
    }

    public Person getMother() {
        return mother;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        if (this.id != null && person.id != null) {
            return id.equals(person.id);
        }
        if (this.firstname != null && person.firstname != null &&
            this.surname != null && person.surname != null) {
            return firstname.equals(person.firstname) && surname.equals(person.surname);
        }
    }
}
