package com.school.project;

/**
 * THE DATA: one student. (Plain Java, nothing Jakarta here.)
 *
 * The fields are final, so a Student never changes after it is created.
 * To "edit" a student we simply replace it with a new Student that has the same id.
 * Objects that never change are automatically safe to share between threads.
 */
public class Student {

    private final int id;
    private final String name;
    private final String gender;     // "Male" or "Female"
    private final int age;
    private final String className;  // e.g. "JSS1" or "SS2" (we can't call it "class": that word is taken by Java)

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
