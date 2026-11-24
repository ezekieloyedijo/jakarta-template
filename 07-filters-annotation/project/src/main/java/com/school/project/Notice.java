package com.school.project;

import java.time.LocalDate;

/** THE DATA: one notice on the board. */
public final class Notice {

    private final int id;
    private final String title;
    private final String body;
    private final String author;
    private final LocalDate posted;

    public Notice(int id, String title, String body, String author, LocalDate posted) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.posted = posted;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDate getPosted() {
        return posted;
    }
}
