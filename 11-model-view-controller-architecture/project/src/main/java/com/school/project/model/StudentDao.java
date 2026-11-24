package com.school.project.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MODEL: where the students are kept, and every way to change them.
 * It is kept in memory here; swapping it for the database DAO of topic 08 would not change
 * the controller or the views at all. That is one of the gifts of MVC.
 */
public class StudentDao {

    private final Map<Integer, Student> students = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public Student insert(String name, String gender, int age, String className) {
        int id = nextId.getAndIncrement();
        Student s = new Student(id, name, gender, age, className);
        students.put(id, s);
        return s;
    }

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>(students.values());
        list.sort(Comparator.comparingInt(Student::getId));
        return list;
    }

    /** Students whose name or class contains the text (not case-sensitive). */
    public List<Student> search(String text) {
        String q = text.toLowerCase(Locale.ROOT);
        List<Student> found = new ArrayList<>();
        for (Student s : findAll()) {
            if (s.getName().toLowerCase(Locale.ROOT).contains(q) || s.getClassName().toLowerCase(Locale.ROOT).contains(q)) {
                found.add(s);
            }
        }
        return found;
    }

    public Student findById(int id) {
        return students.get(id);
    }

    public boolean update(int id, String name, String gender, int age, String className) {
        return students.replace(id, new Student(id, name, gender, age, className)) != null;
    }

    public boolean delete(int id) {
        return students.remove(id) != null;
    }
}
