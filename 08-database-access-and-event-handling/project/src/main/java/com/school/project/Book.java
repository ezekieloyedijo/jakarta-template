package com.school.project;

/** THE DATA: one book = one ROW of the "book" table. */
public final class Book {

    private final int id;
    private final String title;
    private final String author;
    private final int year;
    private final int copies;

    public Book(int id, String title, String author, int year, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.copies = copies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public int getCopies() {
        return copies;
    }
}
