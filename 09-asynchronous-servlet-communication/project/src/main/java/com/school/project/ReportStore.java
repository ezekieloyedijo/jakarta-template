package com.school.project;

import jakarta.servlet.ServletContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * THE STORAGE: every report request, with CRUD.
 * A ConcurrentHashMap is needed: worker threads and request threads use it at the same time.
 */
public class ReportStore {

    public static final String ATTRIBUTE_NAME = "reportStore";
    public static final List<String> CLASSES = List.of("JSS1", "JSS2", "JSS3", "SS1", "SS2", "SS3");

    private final Map<Integer, Report> reports = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public static ReportStore from(ServletContext app) {
        return (ReportStore) app.getAttribute(ATTRIBUTE_NAME);
    }

    public Report add(String title, String className, int students) {
        int id = nextId.getAndIncrement();
        Report r = Report.pending(id, title, className, students);
        reports.put(id, r);
        return r;
    }

    public List<Report> findAll() {
        List<Report> list = new ArrayList<>(reports.values());
        list.sort(Comparator.comparingInt(Report::getId));
        return list;
    }

    public Report findById(int id) {
        return reports.get(id);
    }

    /** UPDATE: new details make the old results wrong, so the report goes back to PENDING. */
    public boolean update(int id, String title, String className, int students) {
        return reports.computeIfPresent(id, (key, old) -> Report.pending(id, title, className, students)) != null;
    }

    /** Called by the worker thread when a report has been generated. False if it was deleted meanwhile. */
    public boolean markReady(int id, List<Integer> scores, long tookMillis) {
        return reports.computeIfPresent(id, (key, old) -> new Report(id, old.getTitle(), old.getClassName(),
                old.getStudents(), Report.Status.READY, scores, LocalDateTime.now().withNano(0), tookMillis)) != null;
    }

    public boolean delete(int id) {
        return reports.remove(id) != null;
    }

    /** Every problem with the form. Empty = all good. */
    public static List<String> validate(String title, String className, String students) {
        List<String> errors = new ArrayList<>();
        if (title == null || title.isBlank()) errors.add("The title is required.");
        else if (title.length() > 60) errors.add("The title must be at most 60 characters.");
        if (!CLASSES.contains(className)) errors.add("Choose a class from the list.");
        Integer n = toNumber(students);
        if (n == null || n < 5 || n > 60) errors.add("Number of students must be a whole number from 5 to 60.");
        return errors;
    }

    public static Integer toNumber(String text) {
        try {
            return Integer.valueOf(text == null ? "" : text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
