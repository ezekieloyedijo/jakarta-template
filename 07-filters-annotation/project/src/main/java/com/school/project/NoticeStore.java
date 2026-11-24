package com.school.project;

import jakarta.servlet.ServletContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** THE STORAGE: every notice, kept in the ServletContext and shared by all members. */
public class NoticeStore {

    private static final String ATTRIBUTE_NAME = "noticeStore";

    private final Map<Integer, Notice> notices = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    /** The one shared store. Made (with two sample notices) the first time it is needed. */
    public static NoticeStore from(ServletContext app) {
        synchronized (app) {
            NoticeStore store = (NoticeStore) app.getAttribute(ATTRIBUTE_NAME);
            if (store == null) {
                store = new NoticeStore();
                store.add("Inter-house sports", "Sports day is on Friday. All students wear house colours.", "Mrs. Bello");
                store.add("Library hours", "The library now closes at 5 pm on weekdays.", "Head Prefect");
                app.setAttribute(ATTRIBUTE_NAME, store);
            }
            return store;
        }
    }

    public Notice add(String title, String body, String author) {
        int id = nextId.getAndIncrement();
        Notice n = new Notice(id, title, body, author, LocalDate.now());
        notices.put(id, n);
        return n;
    }

    /** Newest first. */
    public List<Notice> findAll() {
        List<Notice> list = new ArrayList<>(notices.values());
        list.sort(Comparator.comparingInt(Notice::getId).reversed());
        return list;
    }

    public Notice findById(int id) {
        return notices.get(id);
    }

    /** Keeps the original author and date; changes the title and the text. False if the id doesn't exist. */
    public boolean update(int id, String title, String body) {
        Notice old = notices.get(id);
        if (old == null) return false;
        notices.put(id, new Notice(id, title, body, old.getAuthor(), old.getPosted()));
        return true;
    }

    public boolean delete(int id) {
        return notices.remove(id) != null;
    }

    /** Every problem with the form. Empty list = all good. */
    public static List<String> validate(String title, String body) {
        List<String> errors = new ArrayList<>();
        if (title == null || title.isBlank()) errors.add("The title is required.");
        else if (title.length() > 80) errors.add("The title must be at most 80 characters.");
        if (body == null || body.isBlank()) errors.add("The notice text is required.");
        else if (body.length() > 1000) errors.add("The notice text must be at most 1000 characters.");
        return errors;
    }

    public static Integer toId(String text) {
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
