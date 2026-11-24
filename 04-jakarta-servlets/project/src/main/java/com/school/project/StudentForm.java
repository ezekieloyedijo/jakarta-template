package com.school.project;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * WHAT THE USER TYPED into the Add or Edit form, plus the rules that check it.
 *
 * Every value is kept as a String exactly as typed (even age, e.g. "abc"),
 * so that when something is wrong we can show the form again WITH the user's text in it.
 * Both AddStudentServlet and EditStudentServlet use this class, so the rules are written only once.
 */
public class StudentForm {

    public static final int MIN_AGE = 5;
    public static final int MAX_AGE = 30;

    private final String name;
    private final String gender;
    private final String age;
    private final String className;

    private StudentForm(String name, String gender, String age, String className) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.className = className;
    }

    /** An empty form, for "Add a student". */
    public static StudentForm empty() {
        return new StudentForm("", "", "", "");
    }

    /** A form filled with an existing student, for "Edit". */
    public static StudentForm of(Student s) {
        return new StudentForm(s.getName(), s.getGender(), String.valueOf(s.getAge()), s.getClassName());
    }

    /** Read the four form fields from the request. A missing field becomes "" instead of null. */
    public static StudentForm read(HttpServletRequest request) {
        return new StudentForm(clean(request.getParameter("name")), clean(request.getParameter("gender")),
                clean(request.getParameter("age")), clean(request.getParameter("className")));
    }

    /**
     * Check every rule and return the list of problems. An EMPTY list means the form is valid.
     * Always check on the SERVER: anyone can bypass the browser's "required" checks.
     */
    public List<String> validate(List<String> allowedClasses) {
        List<String> errors = new ArrayList<>();
        if (name.isEmpty()) {
            errors.add("Name is required.");
        } else if (name.length() > 60) {
            errors.add("Name must be 60 characters or fewer.");
        }
        if (!gender.equals("Male") && !gender.equals("Female")) {
            errors.add("Please choose a gender.");
        }
        Integer number = toWholeNumber(age);
        if (number == null || number < MIN_AGE || number > MAX_AGE) {
            errors.add("Age must be a whole number from " + MIN_AGE + " to " + MAX_AGE + ".");
        }
        if (!allowedClasses.contains(className)) {
            errors.add("Please choose a class.");
        }
        return errors;
    }

    /** The age as a number. Only call this after validate() found no errors. */
    public int ageAsNumber() {
        return Integer.parseInt(age);
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

    // ------------------------------------------------------------------ small helpers

    /** The list of classes, read from the "classNames" context-param in web.xml, e.g. "JSS1,JSS2,SS1". */
    public static List<String> allowedClasses(ServletContext app) {
        List<String> classes = new ArrayList<>();
        for (String c : app.getInitParameter("classNames").split(",")) {
            classes.add(c.trim());
        }
        return classes;
    }

    /** Turn text like "12" into the number 12. Returns null for anything that isn't a whole number. */
    public static Integer toWholeNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NullPointerException | NumberFormatException e) {
            return null;
        }
    }

    private static String clean(String text) {
        return text == null ? "" : text.trim();
    }
}
