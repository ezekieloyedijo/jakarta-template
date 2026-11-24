package com.school.project;

import java.io.Serializable;

/**
 * THE DATA: one thing the school shop sells. Prices are whole naira.
 * Serializable = Tomcat may save it to disk with the session when it stops.
 */
public final class Product implements Serializable {

    private final String code;
    private final String name;
    private final int price;

    public Product(String code, String name, int price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
