package com.school.project;

import java.util.List;

/**
 * THE SHOP SHELF: the fixed list of products. It is the same for every customer,
 * so it is NOT stored in the session.
 */
public final class Catalog {

    private static final List<Product> PRODUCTS = List.of(
            new Product("UNI-01", "School uniform", 8500),
            new Product("BAG-01", "School bag", 6000),
            new Product("MTH-01", "Mathematics textbook", 4200),
            new Product("ENG-01", "English textbook", 3900),
            new Product("NBK-10", "Exercise books (pack of 10)", 2500),
            new Product("PEN-12", "Blue pens (box of 12)", 1200));

    private Catalog() {
    }

    public static List<Product> all() {
        return PRODUCTS;
    }

    /** The product with this code, or null if there is none. */
    public static Product find(String code) {
        for (Product p : PRODUCTS) {
            if (p.getCode().equals(code)) {
                return p;
            }
        }
        return null;
    }

    /** "₦8,500" */
    public static String money(int naira) {
        return String.format("₦%,d", naira);
    }
}
