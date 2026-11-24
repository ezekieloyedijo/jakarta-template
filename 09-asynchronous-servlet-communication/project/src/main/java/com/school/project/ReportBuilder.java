package com.school.project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * THE SLOW JOB. A real app would read thousands of marks from a database and do lots of maths.
 * Here we pretend: wait about 3 seconds, then make up one exam score (30 to 100) per student.
 */
public final class ReportBuilder {

    public static final long WORK_MILLIS = 3000;

    private ReportBuilder() {
    }

    public static List<Integer> build(Report report) throws InterruptedException {
        Thread.sleep(WORK_MILLIS);
        List<Integer> scores = new ArrayList<>();
        for (int i = 0; i < report.getStudents(); i++) {
            scores.add(ThreadLocalRandom.current().nextInt(30, 101));
        }
        return scores;
    }
}
