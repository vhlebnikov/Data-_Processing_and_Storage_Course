package ru.nsu.khlebnikov;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Attributes {
    private final Set<String> attributes;

    public Attributes() {
        this.attributes = new HashSet<>();
    }

    public void addAttribute(String a) {
        attributes.add(a);
    }

    public Set<String> getAttributes() {
        return attributes;
    }
}
