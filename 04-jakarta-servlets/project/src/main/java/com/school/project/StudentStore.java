package com.school.project;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * THE STORAGE: keeps every student in memory and does the 5 jobs (create, read, update, delete, search).
 *
 * There is only ONE StudentStore for the whole app. It lives in the ServletContext (the app's
 * "notice board"), so every servlet uses the same one. Many users can use it at the same time,
 * so we use thread-safe types: ConcurrentHashMap and AtomicInteger.
 *
 * Everything is lost when Tomcat stops. Saving to a real database comes in topic 08.
 */
public class StudentStore {

    /** The name the store is saved under in the ServletContext. */
    public static final String ATTRIBUTE_NAME = "studentStore";

    private final Map<Integer, Student> students = new ConcurrentHashMap<>();   // id -> student
    private final AtomicInteger nextId = new AtomicInteger(1);

    /** Fetch THE store from the app's notice board. (StudentListServlet.init() put it there.) */
    public static StudentStore from(ServletContext app) {
        return (StudentStore) app.getAttribute(ATTRIBUTE_NAME);
    }

    /** CREATE: give the new student the next id number and save them. */
    public Student add(String name, String gender, int age, String className) {
        int id = nextId.getAndIncrement();
        Student student = new Student(id, name, gender, age, className);
        students.put(id, student);
        return student;
    }

    /** READ ALL: every student, sorted by id. */
    public List<Student> findAll() {
        List<Student> all = new ArrayList<>(students.values());
        all.sort(Comparator.comparingInt(Student::getId));
        return all;
    }

    /** READ ONE: the student with this id, or null if there is none. */
    public Student findById(int id) {
        return students.get(id);
    }

    /** UPDATE: replace the student that has the same id. Returns false if that id doesn't exist. */
    public boolean update(Student student) {
        return students.replace(student.getId(), student) != null;
    }

    /** DELETE: remove the student. Returns false if that id doesn't exist. */
    public boolean delete(int id) {
        return students.remove(id) != null;
    }

    /** SEARCH: students whose name or class contains the text (capital letters don't matter). */
    public List<Student> search(String text) {
        String wanted = text.toLowerCase();
        List<Student> found = new ArrayList<>();
        for (Student s : findAll()) {
            if (s.getName().toLowerCase().contains(wanted) || s.getClassName().toLowerCase().contains(wanted)) {
                found.add(s);
            }
        }
        return found;
    }
}
