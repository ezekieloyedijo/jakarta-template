package com.school.project;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * THE STORAGE AND THE RULES: the list of subjects, kept in the application (ServletContext),
 * plus the CRUD methods, the validation, and the grade rules.
 *
 * The JSPs only call these methods. The real Java work stays in a normal class,
 * where it is easy to read and test.
 */
public class GradeBook {

    public static final String ATTRIBUTE_NAME = "gradeBook";

    private final Map<Integer, Subject> subjects = new TreeMap<>();   // sorted by id
    private int nextId = 1;

    /** The one shared grade book. Created (with sample subjects) the first time a page asks for it. */
    public static GradeBook of(ServletContext application) {
        synchronized (application) {
            GradeBook book = (GradeBook) application.getAttribute(ATTRIBUTE_NAME);
            if (book == null) {
                book = new GradeBook();
                book.add("Mathematics", 78);
                book.add("English Language", 64);
                book.add("Biology", 55);
                application.setAttribute(ATTRIBUTE_NAME, book);
            }
            return book;
        }
    }

    // ---------- CRUD ----------

    public synchronized Subject add(String name, int score) {
        Subject s = new Subject(nextId++, name, score);
        subjects.put(s.getId(), s);
        return s;
    }

    public synchronized List<Subject> all() {
        return new ArrayList<>(subjects.values());
    }

    /** The subject with this id, or null. */
    public synchronized Subject find(int id) {
        return subjects.get(id);
    }

    /** Returns false if there is no subject with this id. */
    public synchronized boolean update(int id, String name, int score) {
        if (!subjects.containsKey(id)) return false;
        subjects.put(id, new Subject(id, name, score));
        return true;
    }

    public synchronized boolean delete(int id) {
        return subjects.remove(id) != null;
    }

    // ---------- results ----------

    /** The average score, rounded to 1 decimal place. 0 when there are no subjects. */
    public synchronized double average() {
        if (subjects.isEmpty()) return 0;
        int total = 0;
        for (Subject s : subjects.values()) total += s.getScore();
        return Math.round(total * 10.0 / subjects.size()) / 10.0;
    }

    /** The grading scale used by many West African schools. */
    public static String gradeFor(double score) {
        if (score >= 70) return "A";
        if (score >= 60) return "B";
        if (score >= 50) return "C";
        if (score >= 45) return "D";
        if (score >= 40) return "E";
        return "F";
    }

    // ---------- checking the form ----------

    /** Every problem with what the user typed. An empty list means "all good". */
    public static List<String> validate(String name, String scoreText) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("Subject name is required.");
        } else if (name.length() > 40) {
            errors.add("Subject name must be at most 40 characters.");
        }
        Integer score = toScore(scoreText);
        if (score == null) {
            errors.add("Score must be a whole number from 0 to 100.");
        }
        return errors;
    }

    /** The score as a number, or null if it is not a whole number from 0 to 100. */
    public static Integer toScore(String text) {
        try {
            int n = Integer.parseInt(text == null ? "" : text.trim());
            return (n >= 0 && n <= 100) ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Reads an id from the URL or a hidden field. Null if it is not a number. */
    public static Integer toId(String text) {
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Make user-typed text safe to put inside HTML. */
    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
