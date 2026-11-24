package com.school.project;

import jakarta.servlet.http.HttpServletRequest;

/**
 * THE HTML: the layout shared by every page (header, menu with the cart count, title, content).
 * Links start with request.getContextPath(), so they work from any URL, even /cart/add.
 */
public final class Page {

    private Page() {
    }

    public static String page(HttpServletRequest request, String title, String content) {
        String base = request.getContextPath();
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s | School Shop</title>
                  <link rel="stylesheet" href="%s/css/style.css">
                </head>
                <body>
                  <header>
                    <a class="brand" href="%s/">🛒 School Shop</a>
                    <nav>
                      <a href="%s/shop">Products</a>
                      <a href="%s/cart">My cart (%d)</a>
                    </nav>
                  </header>
                  <main>
                    <h1>%s</h1>
                    %s
                  </main>
                </body>
                </html>
                """.formatted(escape(title), base, base, base, base, Cart.countFor(request), escape(title), content);
    }

    /** A green box for a fixed message, or nothing. Only known codes are shown (never raw user text). */
    public static String message(String code) {
        if (code == null) return "";
        String text = switch (code) {
            case "added" -> "Added to your cart.";
            case "updated" -> "Quantity updated.";
            case "removed" -> "Item removed from your cart.";
            case "cleared" -> "Your cart is now empty.";
            default -> null;
        };
        return text == null ? "" : "<p class='msg ok'>" + text + "</p>";
    }

    public static String error(String text) {
        return "<p class='msg error'>" + escape(text) + "</p>";
    }

    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
