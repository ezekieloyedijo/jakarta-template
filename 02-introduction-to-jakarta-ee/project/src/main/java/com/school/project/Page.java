package com.school.project;

/** A small helper so every servlet produces the same page layout. (Plain Java, nothing Jakarta here.) */
public final class Page {

    private Page() {
    }

    public static String wrap(String title, String content) {
        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"><title>Mini Web App</title>
                <link rel="stylesheet" href="css/style.css"></head>
                <body>
                  <h1>%s</h1>
                  %s
                  <p><a href="./">Home</a></p>
                </body>
                </html>
                """.formatted(title, content);
    }

    /** Make user input safe to put inside HTML (explained in topic 13). */
    public static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
