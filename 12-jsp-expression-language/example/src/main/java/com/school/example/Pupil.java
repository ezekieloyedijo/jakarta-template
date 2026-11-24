package com.school.example;

/** A small bean for the EL demos. EL reads it through its getters: ${pupil.name} calls getName(). */
public class Pupil {

    private final String name;
    private final int age;
    private final String className;
    private final boolean prefect;

    public Pupil(String name, int age, String className, boolean prefect) {
        this.name = name;
        this.age = age;
        this.className = className;
        this.prefect = prefect;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getClassName() {
        return className;
    }

    public boolean isPrefect() {           // a boolean getter starts with "is": ${pupil.prefect}
        return prefect;
    }
}
