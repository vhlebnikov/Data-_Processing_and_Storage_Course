package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String id;
    private String firstname;
    private String surname;
    private String gender;
    private final List<Person> siblings;
    private final List<Person> sisters;
    private final List<Person> brothers;
    private final List<Person> children;
    private final List<Person> sons;
    private final List<Person> daughters;
    private Person spouce;
    private Person husband;
    private Person wife;
    private final List<Person> parents;
    private Person father;
    private Person mother;

    public Person() {
        this.siblings = new ArrayList<>();
        this.sisters = new ArrayList<>();
        this.brothers = new ArrayList<>();

        this.children = new ArrayList<>();
        this.sons = new ArrayList<>();
        this.daughters = new ArrayList<>();

        this.parents = new ArrayList<>();
    }

    public void setId(String id) {
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Different data ID: " + this.id + " != " + id);
        }
        this.id = id;
    }

    public void setFirstname(String firstname) {
        if (this.firstname != null && !this.firstname.equals(firstname)) {
            throw new IllegalStateException("Different data FIRSTNAME: " + this.firstname + " != " + firstname);
        }
        this.firstname = firstname;
    }

    public void setSurname(String surname) {
        if (this.surname != null && !this.surname.equals(surname)) {
            throw new IllegalStateException("Different data SURNAME: " + this.surname + " != " + surname);
        }
        this.surname = surname;
    }

    public void setGender(String gender) {
        if (this.gender != null && !this.gender.equals(gender)) {
            throw new IllegalStateException("Different data GENDER: " + this.gender + " != " + gender);
        }
        this.gender = gender;
    }

    public void setSpouce(Person spouce) {
        if (this.spouce != null && !this.spouce.equals(spouce)) {
            throw new IllegalStateException("Person can't have more than one spouce");
        }
        this.spouce = spouce;
    }

    public void setHusband(Person husband) {
        if (this.husband != null && !this.husband.equals(husband)) {
            throw new IllegalStateException("Person can't have more than one husband");
        }
        if (this.wife != null && husband != null) {
            throw new IllegalStateException("Person can't have husband if he has wife");
        }
        this.husband = husband;
    }

    public void setWife(Person wife) {
        if (this.wife != null && !this.wife.equals(wife)) {
            throw new IllegalStateException("Person can't have more than one wife");
        }

        if (this.husband != null && wife != null) {
            throw new IllegalStateException("Person can't have wife if he has husband");
        }
        this.wife = wife;
    }

    public void addParent(Person parent) {
        if (parents.stream().noneMatch(p -> p.equals(parent))) {
            parents.add(parent);
        }
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

    public void addSibling(Person sibling) {
        if (siblings.stream().noneMatch(s -> s.equals(sibling))) {
            siblings.add(sibling);
        }
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

    public void addChild(Person child) {
        if (children.stream().noneMatch(c -> c.equals(child))) {
            children.add(child);
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

    public void merge(Person person) {
        if (person.id != null) {
            setId(person.id);
        }
        if (person.firstname != null) {
            setFirstname(person.firstname);
        }
        if (person.surname != null) {
            setSurname(person.surname);
        }
        if (person.gender != null) {
            setGender(person.gender);
        }
        if (!person.siblings.isEmpty()) {
            if (!this.siblings.isEmpty()) {
                person.siblings.forEach(this::addSibling);
            } else {
                this.siblings.addAll(person.siblings);
            }
        }
        if (!person.sisters.isEmpty()) {
            if (!this.sisters.isEmpty()) {
                person.sisters.forEach(this::addSister);
            } else {
                this.sisters.addAll(person.sisters);
            }
        }
        if (!person.brothers.isEmpty()) {
            if (!this.brothers.isEmpty()) {
                person.brothers.forEach(this::addBrother);
            } else {
                this.brothers.addAll(person.brothers);
            }
        }
        if (!person.children.isEmpty()) {
            if (!this.children.isEmpty()) {
                person.children.forEach(this::addChild);
            } else {
                this.children.addAll(person.children);
            }
        }
        if (!person.sons.isEmpty()) {
            if (!this.sons.isEmpty()) {
                person.sons.forEach(this::addSon);
            } else {
                this.sons.addAll(person.sons);
            }
        }
        if (!person.daughters.isEmpty()) {
            if (!this.daughters.isEmpty()) {
                person.daughters.forEach(this::addDaughter);
            } else {
                this.daughters.addAll(person.daughters);
            }
        }
        if (person.spouce != null) {
            setSpouce(person.spouce);
        }
        if (person.husband != null) {
            setHusband(person.husband);
        }
        if (person.wife != null) {
            setWife(person.wife);
        }
        if (!person.parents.isEmpty()) {
            if (!this.parents.isEmpty()) {
                person.parents.forEach(this::addParent);
            } else {
                this.parents.addAll(person.parents);
            }
        }
        if (person.father != null) {
            setFather(person.father);
        }
        if (person.mother != null) {
            setMother(person.mother);
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
        return false;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", gender='" + gender + '\'' +
                ", siblings=" + siblings +
                ", sisters=" + sisters +
                ", brothers=" + brothers +
                ", children=" + children +
                ", sons=" + sons +
                ", daughters=" + daughters +
                ", spouce=" + spouce +
                ", husband=" + husband +
                ", wife=" + wife +
                ", parents=" + parents +
                ", father=" + father +
                ", mother=" + mother +
                '}';
    }
}
