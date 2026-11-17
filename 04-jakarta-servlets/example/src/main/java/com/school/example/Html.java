package com.school.example;

/** Small helper so every servlet in this example produces the same page layout. Plain Java, nothing Jakarta. */
public final class Html {

    private Html() {
    }

    public static String page(String title, String body) {
        return """
                <!DOCTYPE html>
                <html><head><meta charset="UTF-8"><title>%s</title>
                <link rel="stylesheet" href="css/style.css"></head>
                <body>
                <h1>%s</h1>
                %s
                <p><a href="./">⬅ Back to the example menu</a></p>
                </body></html>
                """.formatted(title, title, body);
    }

    /** Make text safe to put inside HTML (topic 13 explains why). */
    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
