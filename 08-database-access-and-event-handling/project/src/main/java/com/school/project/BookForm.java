package com.school.project;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/** What the user typed in the Add / Edit form (as text), and the rules that check it. */
public final class BookForm {

    private final String title;
    private final String author;
    private final String year;
    private final String copies;

    private BookForm(String title, String author, String year, String copies) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.copies = copies;
    }

    public static BookForm empty() {
        return new BookForm("", "", "", "1");
    }

    public static BookForm of(Book b) {
        return new BookForm(b.getTitle(), b.getAuthor(), String.valueOf(b.getYear()), String.valueOf(b.getCopies()));
    }

    public static BookForm read(HttpServletRequest request) {
        return new BookForm(clean(request.getParameter("title")), clean(request.getParameter("author")),
                clean(request.getParameter("year")), clean(request.getParameter("copies")));
    }

    /** Every problem. An empty list means the form is fine. */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (title.isEmpty()) errors.add("The title is required.");
        else if (title.length() > 100) errors.add("The title must be at most 100 characters.");
        if (author.isEmpty()) errors.add("The author is required.");
        else if (author.length() > 60) errors.add("The author must be at most 60 characters.");
        Integer y = toNumber(year);
        if (y == null || y < 1450 || y > Year.now().getValue()) {
            errors.add("The year must be a whole number from 1450 to " + Year.now().getValue() + ".");
        }
        Integer c = toNumber(copies);
        if (c == null || c < 0 || c > 999) errors.add("Copies must be a whole number from 0 to 999.");
        return errors;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getYear() {
        return year;
    }

    public String getCopies() {
        return copies;
    }

    /** Only call these after validate() returned no errors. */
    public int yearAsNumber() {
        return toNumber(year);
    }

    public int copiesAsNumber() {
        return toNumber(copies);
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
