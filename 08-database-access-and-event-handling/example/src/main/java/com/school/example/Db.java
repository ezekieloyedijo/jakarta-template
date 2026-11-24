package com.school.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Where the database is, and how to open a connection to it.
 *
 * "jdbc:h2:mem:fruitshop" = an H2 database that lives IN MEMORY, called "fruitshop".
 * DB_CLOSE_DELAY=-1        = keep it alive while the app runs, even when no connection is open.
 * (The project uses a database FILE instead, so its data survives a restart.)
 */
public final class Db {

    public static final String URL = "jdbc:h2:mem:fruitshop;DB_CLOSE_DELAY=-1";

    private Db() {
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");    // H2's default user is "sa" with no password
    }

    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
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
                  <header><a class="brand" href="./">🗄️ Database and Listener demos</a></header>
                  <main>
                    <h1>%s</h1>
                    %s
                    <p><a href="./">← back to all demos</a></p>
                  </main>
                </body>
                </html>
                """.formatted(title, title, body);
    }
}
