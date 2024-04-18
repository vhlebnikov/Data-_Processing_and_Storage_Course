package ru.nsu.khlebnikov.parser;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String firstname;
    private String surname;
    private String gender;
    private int siblingsNumber;
    private List<Person> siblings;
    private List<Person> sisters;
    private List<Person> brothers;
    private int childrenNumber;
    private List<Person> children;
    private List<Person> sons;
    private List<Person> daughters;
    private Person spouce;
    private Person husband;
    private Person wife;
    private List<Person> parents;
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

    public void copy(Person p) {
        this.id = p.id;
        this.firstname = p.firstname;
        this.surname = p.surname;
        this.gender = p.gender;
        this.siblingsNumber = p.siblingsNumber;
        this.siblings = new ArrayList<>(p.siblings);
        this.sisters = new ArrayList<>(p.sisters);
        this.brothers = new ArrayList<>(p.brothers);
        this.childrenNumber = p.childrenNumber;
        this.children = new ArrayList<>(p.children);
        this.sons = new ArrayList<>(p.sons);
        this.daughters = new ArrayList<>(p.daughters);
        this.spouce = p.spouce;
        this.husband = p.husband;
        this.wife = p.wife;
        this.parents = new ArrayList<>(p.parents);
        this.father = p.father;
        this.mother = p.mother;
    }

    public Person saveOnlyMainInfo() {
        Person person = new Person();
        person.setId(this.id);
        person.setFirstname(this.firstname);
        person.setSurname(this.surname);
        person.setGender(this.gender);
        return person;
    }

    public void setId(String id) {
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("Different data ID: " + this.id + " != " + id);
        }
        if (this.siblings != null) {
            this.siblings.forEach(s -> {
                if (id.equals(s.id)) {
                    throw new IllegalStateException("Person can't be his own sibling");
                }
            });
        }
        if (this.sisters != null) {
            this.sisters.forEach(s -> {
                if (id.equals(s.id)) {
                    throw new IllegalStateException("Person can't be his own sister");
                }
            });
        }
        if (this.brothers != null) {
            this.brothers.forEach(s -> {
                if (id.equals(s.id)) {
                    throw new IllegalStateException("Person can't be his own brother");
                }
            });
        }
        if (this.spouce != null && id.equals(spouce.id)) {
            throw new IllegalStateException("Person can't be his own spouce");
        }
        if (this.husband != null && id.equals(husband.id)) {
            throw new IllegalStateException("Person can't be his own husband");
        }
        if (this.wife != null && id.equals(wife.id)) {
            throw new IllegalStateException("Person can't be his own wife");
        }
        if (this.parents != null) {
            parents.forEach(p -> {
                if (id.equals(p.id)) {
                    throw new IllegalStateException("Person can't be his own parent");
                }
            });
        }
        if (this.father != null && id.equals(father.id)) {
            throw new IllegalStateException("Person can't be his own father");
        }
        if (this.mother != null && id.equals(mother.id)) {
            throw new IllegalStateException("Person can't be his own mother");
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
            throw new IllegalStateException("Person can't have more than one spouce: " + this.spouce + " != " + spouce);
        }
        this.spouce = spouce;
    }

    public void setHusband(Person husband) {
        if (this.husband != null && !this.husband.equals(husband)) {
            throw new IllegalStateException("Person can't have more than one husband: " + this.husband + " != " + husband);
        }
        if (this.wife != null && husband != null) {
            throw new IllegalStateException("Person can't have husband if he has wife: " + this.wife + " != " + husband);
        }
        this.husband = husband;
    }

    public void setWife(Person wife) {
        if (this.wife != null && !this.wife.equals(wife)) {
            throw new IllegalStateException("Person can't have more than one wife: " + this.wife + " != " + wife);
        }

        if (this.husband != null && wife != null) {
            throw new IllegalStateException("Person can't have wife if he has husband: " + this.husband + " != " + wife);
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
            throw new IllegalStateException("Person can't have more than one father: " + this.father + " != " + father);
        }
        this.father = father;
    }

    public void setMother(Person mother) {
        if (this.mother != null && !this.mother.equals(mother)) {
            throw new IllegalStateException("Person can't have more than one mother: " + this.mother + " != " + mother);
        }
        this.mother = mother;
    }

    public void addSibling(Person sibling) {
        if (siblings.stream().noneMatch(s -> s.equals(sibling))) {
            siblings.add(sibling);
        }
    }

    public void removeSibling(Person person) {
        setSiblings(siblings.stream().filter(s -> !s.equals(person)).toList());
    }

    public void addSister(Person sister) {
        if (sisters.stream().noneMatch(s -> s.equals(sister))) {
            sisters.add(sister);
        }
    }

    public void removeSister(Person person) {
        setSisters(sisters.stream().filter(s -> !s.equals(person)).toList());
    }

    public void addBrother(Person brother) {
        if (brothers.stream().noneMatch(b -> b.equals(brother))) {
            brothers.add(brother);
        }
    }

    public void removeBrother(Person person) {
        setBrothers(brothers.stream().filter(b -> !b.equals(person)).toList());
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

    public void setSiblingsNumber(int siblingsNumber) {
        if (this.siblingsNumber != 0 && this.siblingsNumber != siblingsNumber) {
            throw new IllegalStateException("Different siblings numbers");
        }
        this.siblingsNumber = siblingsNumber;
    }

    public void setChildrenNumber(int childrenNumber) {
        if (this.childrenNumber != 0 && this.childrenNumber != childrenNumber) {
            throw new IllegalStateException("Different children numbers");
        }
        this.childrenNumber = childrenNumber;
    }

    public void setSiblings(List<Person> siblings) {
        this.siblings = siblings;
    }

    public void setSisters(List<Person> sisters) {
        this.sisters = sisters;
    }

    public void setBrothers(List<Person> brothers) {
        this.brothers = brothers;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public void setSons(List<Person> sons) {
        this.sons = sons;
    }

    public void setDaughters(List<Person> daughters) {
        this.daughters = daughters;
    }

    public void setParents(List<Person> parents) {
        this.parents = parents;
    }

    public boolean merge(Person person) {
        Person copy = new Person();
        copy.copy(this);
        try {
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
            if (person.siblingsNumber != 0) {
                setSiblingsNumber(person.siblingsNumber);
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
            if (person.childrenNumber != 0) {
                setChildrenNumber(person.childrenNumber);
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
            return true;
        } catch (IllegalStateException e) {
            this.copy(copy);
            return false;
        }
    }

    public int getSiblingsNumber() {
        return siblingsNumber;
    }

    public int getChildrenNumber() {
        return childrenNumber;
    }

    public String getId() {
        return id;
    }

    public Object getIdAsInt() {
        if (id == null) {
            return null;
        }
        return Integer.parseInt(id.substring(1));
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

    public List<Person> getSiblings() {
        return siblings;
    }

    public List<Person> getChildren() {
        return children;
    }

    public Person getSpouce() {
        return spouce;
    }

    public List<Person> getParents() {
        return parents;
    }

    public boolean weakEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        if (this.firstname != null && person.firstname != null &&
                this.surname != null && person.surname != null) {
            return firstname.equals(person.firstname) && surname.equals(person.surname);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        if (this.id != null && person.id != null) {
            return id.equals(person.id);
        }
        if (this.gender != null && person.gender != null) {
            if (!this.gender.equals(person.gender)) {
                return false;
            }
            if ((this.gender.equals("male") && person.husband != null) ||
                    person.gender.equals("male") && this.husband != null) {
                return false;
            }
            if ((this.gender.equals("female") && person.wife != null) ||
                    person.gender.equals("female") && this.wife != null) {
                return false;
            }
        }
        if (this.wife != null && person.husband != null) {
            return false;
        }
        if (this.husband != null && person.wife != null) {
            return false;
        }
        if (this.firstname != null && person.firstname != null &&
                this.surname != null && person.surname != null) {
            return firstname.equals(person.firstname) && surname.equals(person.surname);
        }
        return false;
    }

    @Override
    public String toString() {
        return "\nPERSON{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", g='" + gender + '\'' +
                ", siblingsNumber=" + siblingsNumber +
                ", realSibSize=" + siblings.size() +
                ", siblings=" + siblings +
                ", childrenNumber=" + childrenNumber +
                ", realChiNum=" + children.size() +
                ", children=" + children +
                ", spouce=" + spouce +
                "}";
    }
}
