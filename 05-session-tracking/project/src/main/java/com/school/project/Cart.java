package com.school.project;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * THE CART: one per customer, kept in that customer's HttpSession.
 * It remembers "product code -> quantity" and does the cart's CRUD:
 * add (create), lines (read), update, remove (delete), and clear.
 */
public class Cart implements Serializable {

    public static final String ATTRIBUTE_NAME = "cart";
    public static final int MAX_QUANTITY = 20;

    // LinkedHashMap keeps the items in the order they were added
    private final Map<String, Integer> quantities = new LinkedHashMap<>();

    /** This customer's cart. The first time, a new empty cart is made and put in the session. */
    public static Cart of(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(ATTRIBUTE_NAME);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(ATTRIBUTE_NAME, cart);
        }
        return cart;
    }

    /** How many items are in the cart, WITHOUT creating a session for visitors who have none. */
    public static int countFor(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return 0;
        Cart cart = (Cart) session.getAttribute(ATTRIBUTE_NAME);
        return cart == null ? 0 : cart.count();
    }

    /** CREATE: put more of a product in the cart. Returns false if that would go over the limit. */
    public synchronized boolean add(String code, int quantity) {
        int newQuantity = quantities.getOrDefault(code, 0) + quantity;
        if (newQuantity > MAX_QUANTITY) {
            return false;
        }
        quantities.put(code, newQuantity);
        return true;
    }

    /** UPDATE: set a new quantity for a product already in the cart. False if it isn't in the cart. */
    public synchronized boolean update(String code, int quantity) {
        if (!quantities.containsKey(code)) {
            return false;
        }
        quantities.put(code, quantity);
        return true;
    }

    /** DELETE: take one product out of the cart. False if it wasn't there. */
    public synchronized boolean remove(String code) {
        return quantities.remove(code) != null;
    }

    /** READ: one line per product, ready to show in a table. */
    public synchronized List<Line> lines() {
        List<Line> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> e : quantities.entrySet()) {
            lines.add(new Line(Catalog.find(e.getKey()), e.getValue()));
        }
        return lines;
    }

    public synchronized int count() {
        int count = 0;
        for (int q : quantities.values()) count += q;
        return count;
    }

    public synchronized int total() {
        int total = 0;
        for (Line line : lines()) total += line.subtotal();
        return total;
    }

    /**
     * Reads a quantity typed by the user. Returns null if it is not a whole number from 1 to MAX_QUANTITY.
     */
    public static Integer parseQuantity(String text) {
        try {
            int q = Integer.parseInt(text == null ? "" : text.trim());
            return (q >= 1 && q <= MAX_QUANTITY) ? q : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** One row of the cart table: a product and how many of it. */
    public record Line(Product product, int quantity) implements Serializable {
        public int subtotal() {
            return product.getPrice() * quantity;
        }
    }
}
