package com.school.project;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/** THE HTML: the shared layout, the notice form, and escaping. */
public final class Page {

    private Page() {
    }

    /** The logged-in member's name, or null. Never creates a session. */
    public static String user(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute("user");
    }

    public static String page(HttpServletRequest request, String title, String content) {
        String base = request.getContextPath();
        String user = user(request);
        String right = user == null
                ? "<a href='%s/login'>Log in</a>".formatted(base)
                : """
                  <a href="%s/members/notices">Notices</a>
                  <a href="%s/members/notices/add">New notice</a>
                  <span class="user">👤 %s</span>
                  <form action="%s/logout" method="post" style="display:inline;margin-left:12px">
                    <button class="btn small secondary" type="submit">Log out</button>
                  </form>
                  """.formatted(base, base, escape(user), base);
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s | Notice Board</title>
                  <link rel="stylesheet" href="%s/css/style.css">
                </head>
                <body>
                  <header>
                    <a class="brand" href="%s/">📌 Notice Board</a>
                    <nav>%s</nav>
                  </header>
                  <main>
                    <h1>%s</h1>
                    %s
                  </main>
                </body>
                </html>
                """.formatted(escape(title), base, base, right, escape(title), content);
    }

    /**
     * The Add / Edit form, filled with what the user typed.
     * action is "add" or "edit" (both live under /members/notices/). id is null when adding.
     */
    public static String form(HttpServletRequest request, String action, Integer id, String title, String body,
                              List<String> errors, String button) {
        String notices = request.getContextPath() + "/members/notices";
        StringBuilder box = new StringBuilder();
        if (!errors.isEmpty()) {
            box.append("<div class='msg error'><b>Please fix the following:</b><ul>");
            for (String e : errors) box.append("<li>").append(escape(e)).append("</li>");
            box.append("</ul></div>");
        }
        String hidden = id == null ? "" : "<input type='hidden' name='id' value='" + id + "'>";
        return """
                %s
                <form class="card" action="%s/%s" method="post">
                  %s
                  <label>Title <input name="title" value="%s" maxlength="80" required></label>
                  <label>Notice <textarea name="body" maxlength="1000" required>%s</textarea></label>
                  <div class="buttons">
                    <button class="btn" type="submit">%s</button>
                    <a class="btn secondary" href="%s">Cancel</a>
                  </div>
                </form>
                """.formatted(box, notices, action, hidden, escape(title), escape(body), button, notices);
    }

    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
