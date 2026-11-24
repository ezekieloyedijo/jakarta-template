package com.school.project;

import jakarta.servlet.ServletContext;

import java.util.List;

/**
 * THE HTML: builds every page of the app, so the servlets stay short.
 * (In topic 06 we'll write pages as JSP files instead of Java strings.)
 */
public final class Page {

    private Page() {
    }

    /** The full page: header with the school name and the menu, then the title and the content. */
    public static String page(ServletContext app, String title, String content) {
        String school = escape(app.getInitParameter("schoolName"));
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s | %s</title>
                  <link rel="stylesheet" href="css/style.css">
                </head>
                <body>
                  <header>
                    <a class="brand" href="./">🏫 %s</a>
                    <nav>
                      <a href="students">Students</a>
                      <a href="add-student">Add student</a>
                      <a href="search">Search</a>
                    </nav>
                  </header>
                  <main>
                    <h1>%s</h1>
                    %s
                  </main>
                </body>
                </html>
                """.formatted(escape(title), school, school, escape(title), content);
    }

    /** A table of students, each row with an Edit link and a Delete button. */
    public static String table(List<Student> students) {
        if (students.isEmpty()) {
            return "<p class='empty'>No students found.</p>";
        }
        StringBuilder rows = new StringBuilder();
        for (Student s : students) {
            rows.append("""
                    <tr>
                      <td>%d</td><td>%s</td><td>%s</td><td>%d</td><td>%s</td>
                      <td class="actions">
                        <a class="btn small" href="edit-student?id=%d">Edit</a>
                        <form action="delete-student" method="post" data-name="%s"
                              onsubmit="return confirm('Delete ' + this.dataset.name + '?')">
                          <input type="hidden" name="id" value="%d">
                          <button class="btn small danger" type="submit">Delete</button>
                        </form>
                      </td>
                    </tr>
                    """.formatted(s.getId(), escape(s.getName()), s.getGender(), s.getAge(),
                    escape(s.getClassName()), s.getId(), escape(s.getName()), s.getId()));
        }
        return """
                <div class="table-wrap">
                <table>
                  <thead><tr><th>ID</th><th>Name</th><th>Gender</th><th>Age</th><th>Class</th><th></th></tr></thead>
                  <tbody>
                %s  </tbody>
                </table>
                </div>
                """.formatted(rows);
    }

    /**
     * The Add / Edit form. It is filled with the values in "form", so the user never has to retype.
     * For Edit, "id" is sent along in a hidden field. For Add, id is null.
     */
    public static String form(String action, StudentForm form, List<String> errors,
                              List<String> classes, String buttonText, Integer id) {
        StringBuilder errorBox = new StringBuilder();
        if (!errors.isEmpty()) {
            errorBox.append("<div class='msg error'><b>Please fix the following:</b><ul>");
            for (String e : errors) {
                errorBox.append("<li>").append(escape(e)).append("</li>");
            }
            errorBox.append("</ul></div>");
        }

        StringBuilder options = new StringBuilder("<option value=''>-- choose --</option>");
        for (String c : classes) {
            options.append("<option").append(selected(c, form.getClassName())).append(">")
                    .append(escape(c)).append("</option>");
        }

        String hiddenId = id == null ? "" : "<input type='hidden' name='id' value='" + id + "'>";

        return """
                %s
                <form class="card" action="%s" method="post">
                  %s
                  <label>Full name
                    <input name="name" value="%s" maxlength="60" required></label>
                  <fieldset>
                    <legend>Gender</legend>
                    <label class="inline"><input type="radio" name="gender" value="Male"%s> Male</label>
                    <label class="inline"><input type="radio" name="gender" value="Female"%s> Female</label>
                  </fieldset>
                  <label>Age
                    <input name="age" type="number" min="%d" max="%d" value="%s" required></label>
                  <label>Class
                    <select name="className" required>%s</select></label>
                  <div class="buttons">
                    <button class="btn" type="submit">%s</button>
                    <a class="btn secondary" href="students">Cancel</a>
                  </div>
                </form>
                """.formatted(errorBox, action, hiddenId, escape(form.getName()),
                checked("Male", form.getGender()), checked("Female", form.getGender()),
                StudentForm.MIN_AGE, StudentForm.MAX_AGE, escape(form.getAge()), options, buttonText);
    }

    /** Make user-typed text safe to put inside HTML, so it shows as text instead of running as code (topic 13). */
    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String selected(String option, String current) {
        return option.equals(current) ? " selected" : "";
    }

    private static String checked(String option, String current) {
        return option.equals(current) ? " checked" : "";
    }
}
