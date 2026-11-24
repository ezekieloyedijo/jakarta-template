package com.school.project.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MODEL: what the user typed (still as text) and the RULES that check it.
 * The rules belong to the model, not to the controller or the view.
 */
public final class StudentForm {

    public static final List<String> CLASSES = List.of("JSS1", "JSS2", "JSS3", "SS1", "SS2", "SS3");
    public static final List<String> GENDERS = List.of("Male", "Female");
    public static final int MIN_AGE = 5;
    public static final int MAX_AGE = 30;

    private final String name;
    private final String gender;
    private final String age;
    private final String className;

    public StudentForm(String name, String gender, String age, String className) {
        this.name = clean(name);
        this.gender = clean(gender);
        this.age = clean(age);
        this.className = clean(className);
    }

    public static StudentForm empty() {
        return new StudentForm("", "", "", "");
    }

    public static StudentForm of(Student s) {
        return new StudentForm(s.getName(), s.getGender(), String.valueOf(s.getAge()), s.getClassName());
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name.isEmpty()) errors.add("Name is required.");
        else if (name.length() > 60) errors.add("Name must be at most 60 characters.");
        if (!GENDERS.contains(gender)) errors.add("Choose Male or Female.");
        Integer a = toNumber(age);
        if (a == null || a < MIN_AGE || a > MAX_AGE) {
            errors.add("Age must be a whole number from " + MIN_AGE + " to " + MAX_AGE + ".");
        }
        if (!CLASSES.contains(className)) errors.add("Choose a class from the list.");
        return errors;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getClassName() {
        return className;
    }

    /** Only after validate() found no errors. */
    public int ageAsNumber() {
        return toNumber(age);
    }

    public static Integer toNumber(String text) {
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String clean(String text) {
        return text == null ? "" : text.trim();
    }
}
