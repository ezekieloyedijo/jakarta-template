package com.school.project;

import java.time.LocalDateTime;
import java.util.List;

/**
 * THE DATA: one report request.
 * While it is PENDING, scores is empty. After generating it is READY and holds the scores.
 */
public final class Report {

    public enum Status { PENDING, READY }

    private final int id;
    private final String title;
    private final String className;
    private final int students;
    private final Status status;
    private final List<Integer> scores;
    private final LocalDateTime generatedAt;
    private final long tookMillis;

    public Report(int id, String title, String className, int students, Status status,
                  List<Integer> scores, LocalDateTime generatedAt, long tookMillis) {
        this.id = id;
        this.title = title;
        this.className = className;
        this.students = students;
        this.status = status;
        this.scores = List.copyOf(scores);
        this.generatedAt = generatedAt;
        this.tookMillis = tookMillis;
    }

    /** A brand-new request that has not been generated yet. */
    public static Report pending(int id, String title, String className, int students) {
        return new Report(id, title, className, students, Status.PENDING, List.of(), null, 0);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getClassName() {
        return className;
    }

    public int getStudents() {
        return students;
    }

    public Status getStatus() {
        return status;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public long getTookMillis() {
        return tookMillis;
    }

    public double average() {
        return Math.round(scores.stream().mapToInt(Integer::intValue).average().orElse(0) * 10) / 10.0;
    }

    public int highest() {
        return scores.stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public int lowest() {
        return scores.stream().mapToInt(Integer::intValue).min().orElse(0);
    }
}
