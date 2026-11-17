package com.school.project;

import jakarta.servlet.ServletContext;

/**
 * Builds the common page layout (header + menu + footer) for every page,
 * using the owner's details from the <context-param>s in web.xml.
 */
public final class Layout {

    private Layout() {
    }

    public static String page(ServletContext app, String title, String content) {
        String owner = app.getInitParameter("ownerName");
        String ownerTitle = app.getInitParameter("ownerTitle");
        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"><title>%s | %s</title>
                <link rel="stylesheet" href="css/style.css"></head>
                <body>
                  <header>
                    <strong>%s</strong>: %s
                    <nav><a href="./">Home</a> <a href="about">About</a> <a href="projects">Projects</a> <a href="contact">Contact</a></nav>
                  </header>
                  <h1>%s</h1>
                  %s
                  <footer>Built with Jakarta Servlets, deployed as a WAR.</footer>
                </body>
                </html>
                """.formatted(title, owner, owner, ownerTitle, title, content);
    }

    /** Make user-typed text safe to show inside HTML (see topic 13). */
    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
