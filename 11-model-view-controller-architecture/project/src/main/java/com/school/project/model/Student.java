package com.school.project.model;

/** MODEL: one student. Getters only, so a view can read it but never change it. */
public final class Student {

    private final int id;
    private final String name;
    private final String gender;
    private final int age;
    private final String className;

    public Student(int id, String name, String gender, int age, String className) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getClassName() {
        return className;
    }
}
