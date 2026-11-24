package com.school.project;

import java.io.Serializable;

/**
 * THE JAVABEAN: one contact card.
 * It follows the bean rules (no-argument constructor + getX/setX), so save.jsp can fill it with ONE line:
 *     <jsp:setProperty name="contact" property="*"/>
 * which calls setName(...), setPhone(...), setEmail(...), setRole(...) and setId(...) from the form fields.
 */
public class Contact implements Serializable {

    private int id;                 // 0 = a new contact that hasn't been saved yet
    private String name = "";
    private String phone = "";
    private String email = "";
    private String role = "";

    public Contact() {
    }

    /** A separate copy, so a page that changes a bean never changes the saved one by accident. */
    public Contact copy() {
        Contact c = new Contact();
        c.id = id;
        c.name = name;
        c.phone = phone;
        c.email = email;
        c.role = role;
        return c;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? "" : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? "" : email.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role == null ? "" : role.trim();
    }

    /** "AO" for "Amaka Obi": shown in the round avatar on the card. */
    public String getInitials() {
        StringBuilder sb = new StringBuilder();
        for (String part : name.split("\\s+")) {
            if (!part.isEmpty() && sb.length() < 2) sb.append(Character.toUpperCase(part.charAt(0)));
        }
        return sb.toString();
    }
}
