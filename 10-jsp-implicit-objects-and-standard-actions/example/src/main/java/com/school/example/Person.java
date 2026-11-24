package com.school.example;

import java.io.Serializable;

/**
 * A JAVABEAN: a normal class that follows 3 rules, so JSP actions can use it without any Java code:
 *   1. a public constructor with NO parameters   (so <jsp:useBean> can create it)
 *   2. private fields
 *   3. public getX() / setX() for each property  (so <jsp:getProperty> / <jsp:setProperty> can use it)
 */
public class Person implements Serializable {

    private String name = "";
    private int age;

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /** Make user-typed text safe to show inside HTML. */
    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
