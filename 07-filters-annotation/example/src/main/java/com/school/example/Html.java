package com.school.example;

/** A tiny helper that wraps some HTML in a full page. */
public final class Html {

    private Html() {
    }

    public static String page(String base, String title, String body) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s</title>
                  <link rel="stylesheet" href="%s/css/style.css">
                </head>
                <body>
                  <header><a class="brand" href="%s/">🛡️ Filter demos</a></header>
                  <main>
                    <h1>%s</h1>
                    %s
                    <p><a href="%s/">← back to all demos</a></p>
                  </main>
                </body>
                </html>
                """.formatted(title, base, base, title, body, base);
    }
}
