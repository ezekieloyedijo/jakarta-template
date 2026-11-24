package com.school.project;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/** THE HTML: layout, the request form, a fixed message box, and escaping. */
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
                  <title>%s | Report Generator</title>
                  <link rel="stylesheet" href="%s/css/style.css">
                </head>
                <body>
                  <header>
                    <a class="brand" href="%s/reports">📊 Report Generator</a>
                    <nav>
                      <a href="%s/reports">All reports</a>
                      <a href="%s/reports/add">New request</a>
                    </nav>
                  </header>
                  <main>
                    <h1>%s</h1>
                    %s
                  </main>
                </body>
                </html>
                """.formatted(escape(title), base, base, base, base, escape(title), content);
    }

    /** Fixed messages only: the text never comes from the URL. */
    public static String message(String code) {
        String text = switch (String.valueOf(code)) {
            case "added" -> "Report request saved. Click Generate to build it.";
            case "updated" -> "Request updated. It must be generated again.";
            case "deleted" -> "Report deleted.";
            case "generated" -> "Report generated.";
            default -> null;
        };
        return text == null ? "" : "<p class='msg ok'>" + text + "</p>";
    }

    /** action is "add" or "edit" (under /reports/). id is null when adding. */
    public static String form(HttpServletRequest request, String action, Integer id, String title, String className,
                              String students, List<String> errors, String button) {
        String base = request.getContextPath() + "/reports";
        StringBuilder box = new StringBuilder();
        if (!errors.isEmpty()) {
            box.append("<div class='msg error'><b>Please fix the following:</b><ul>");
            for (String e : errors) box.append("<li>").append(escape(e)).append("</li>");
            box.append("</ul></div>");
        }
        StringBuilder options = new StringBuilder("<option value=''>-- choose --</option>");
        for (String c : ReportStore.CLASSES) {
            options.append("<option").append(c.equals(className) ? " selected" : "").append(">")
                    .append(c).append("</option>");
        }
        String hidden = id == null ? "" : "<input type='hidden' name='id' value='" + id + "'>";
        return """
                %s
                <form class="card" action="%s/%s" method="post">
                  %s
                  <label>Report title <input name="title" value="%s" maxlength="60" required></label>
                  <label>Class <select name="className" required>%s</select></label>
                  <label>Number of students <input name="students" type="number" min="5" max="60" value="%s" required></label>
                  <div class="buttons">
                    <button class="btn" type="submit">%s</button>
                    <a class="btn secondary" href="%s">Cancel</a>
                  </div>
                </form>
                """.formatted(box, base, action, hidden, escape(title), options, escape(students), button, base);
    }

    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
