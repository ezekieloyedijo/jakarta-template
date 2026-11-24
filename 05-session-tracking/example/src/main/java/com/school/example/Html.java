package com.school.example;

/** A tiny helper that wraps some HTML in a full page, so each demo servlet stays short. */
public final class Html {

    private Html() {
    }

    public static String page(String title, String body) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s</title>
                  <link rel="stylesheet" href="css/style.css">
                </head>
                <body>
                  <header><a class="brand" href="./">🍪 Session Tracking demos</a></header>
                  <main>
                    <h1>%s</h1>
                    %s
                    <p><a href="./">← back to all demos</a></p>
                  </main>
                </body>
                </html>
                """.formatted(escape(title), escape(title), body);
    }

    /** Make user-typed text safe to show inside HTML. */
    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
