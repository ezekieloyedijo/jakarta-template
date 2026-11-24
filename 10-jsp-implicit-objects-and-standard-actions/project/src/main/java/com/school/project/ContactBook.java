package com.school.project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * THE STORAGE, as a JavaBean too: the pages create it with
 *     <jsp:useBean id="book" class="com.school.project.ContactBook" scope="application"/>
 * The FIRST page to run that line creates it (calling this no-argument constructor);
 * every page after that FINDS the same object in the application scope.
 */
public class ContactBook {

    public static final List<String> ROLES = List.of("Student", "Parent", "Teacher");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+ ]{7,15}$");

    private final Map<Integer, Contact> contacts = new TreeMap<>();
    private int nextId = 1;

    public ContactBook() {
        save(sample("Amaka Obi", "0803 123 4567", "amaka@example.com", "Student"));
        save(sample("Mr. Tunde Bakare", "0812 555 0101", "tbakare@example.com", "Parent"));
        save(sample("Mrs. Grace Eze", "0705 222 3344", "grace.eze@example.com", "Teacher"));
        System.out.println("[BOOK] ContactBook created by <jsp:useBean> with 3 sample contacts");
    }

    /** Sorted by name. Copies, so pages can't change the saved contacts. */
    public synchronized List<Contact> getAll() {
        List<Contact> list = new ArrayList<>();
        for (Contact c : contacts.values()) list.add(c.copy());
        list.sort(Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    /** A copy of the contact with this id, or null. */
    public synchronized Contact find(int id) {
        Contact c = contacts.get(id);
        return c == null ? null : c.copy();
    }

    /**
     * CREATE when id is 0 (a new id is given), UPDATE when the id exists.
     * Returns false if the id is not 0 and doesn't exist.
     */
    public synchronized boolean save(Contact contact) {
        Contact c = contact.copy();
        if (c.getId() == 0) {
            c.setId(nextId++);
        } else if (!contacts.containsKey(c.getId())) {
            return false;
        }
        contacts.put(c.getId(), c);
        return true;
    }

    public synchronized boolean delete(int id) {
        return contacts.remove(id) != null;
    }

    /** Every problem with the contact. An empty list = all good. */
    public static List<String> validate(Contact c) {
        List<String> errors = new ArrayList<>();
        if (c.getName().isEmpty()) errors.add("The name is required.");
        else if (c.getName().length() > 50) errors.add("The name must be at most 50 characters.");
        if (!PHONE.matcher(c.getPhone()).matches()) errors.add("The phone must be 7 to 15 digits (spaces and + allowed).");
        if (!c.getEmail().isEmpty() && !EMAIL.matcher(c.getEmail()).matches()) errors.add("The email doesn't look right.");
        if (!ROLES.contains(c.getRole())) errors.add("Choose Student, Parent, or Teacher.");
        return errors;
    }

    /** Reads an id typed in a URL or form: a whole number, or null. */
    public static Integer toId(String text) {
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static Contact sample(String name, String phone, String email, String role) {
        Contact c = new Contact();
        c.setName(name);
        c.setPhone(phone);
        c.setEmail(email);
        c.setRole(role);
        return c;
    }
}
