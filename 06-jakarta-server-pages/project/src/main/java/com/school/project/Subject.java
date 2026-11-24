package com.school.project;

/** THE DATA: one subject on the report card, with its score out of 100. */
public final class Subject {

    private final int id;
    private final String name;
    private final int score;

    public Subject(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    /** The letter grade for this score. */
    public String getGrade() {
        return GradeBook.gradeFor(score);
    }
}
