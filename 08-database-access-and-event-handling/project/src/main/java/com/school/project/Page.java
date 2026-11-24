package com.school.project;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/** THE HTML: the layout, the book table, the book form, and escaping. */
public final class Page {

    private Page() {
    }

    public static String page(HttpServletRequest request, String title, String content) {
        request.getSession();          // every visitor gets a session, so ReadersOnlineListener can count them
        String base = request.getContextPath();
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s | School Library</title>
                  <link rel="stylesheet" href="%s/css/style.css">
                </head>
                <body>
                  <header>
                    <a class="brand" href="%s/books">📚 School Library</a>
                    <nav>
                      <a href="%s/books">All books</a>
                      <a href="%s/books/add">Add book</a>
                    </nav>
                  </header>
                  <main>
                    <h1>%s</h1>
                    %s
                  </main>
                  <footer>📖 Readers online: %d · Books are saved in an H2 database file.</footer>
                </body>
                </html>
                """.formatted(escape(title), base, base, base, base, escape(title), content,
                ReadersOnlineListener.online());
    }

    public static String table(HttpServletRequest request, List<Book> books) {
        if (books.isEmpty()) {
            return "<p class='empty'>The library has no books yet.</p>";
        }
        String base = request.getContextPath() + "/books";
        StringBuilder rows = new StringBuilder();
        for (Book b : books) {
            String badge = b.getCopies() == 0 ? "<span class='badge err'>none left</span>"
                    : "<span class='badge ok'>" + b.getCopies() + "</span>";
            rows.append("""
                    <tr>
                      <td>%s</td><td>%s</td><td>%d</td><td>%s</td>
                      <td class="actions">
                        <a class="btn small" href="%s/edit?id=%d">Edit</a>
                        <form action="%s/delete" method="post" data-title="%s"
                              onsubmit="return confirm('Delete ' + this.dataset.title + '?')">
                          <input type="hidden" name="id" value="%d">
                          <button class="btn small danger" type="submit">Delete</button>
                        </form>
                      </td>
                    </tr>
                    """.formatted(escape(b.getTitle()), escape(b.getAuthor()), b.getYear(), badge,
                    base, b.getId(), base, escape(b.getTitle()), b.getId()));
        }
        return """
                <div class="table-wrap"><table>
                  <thead><tr><th>Title</th><th>Author</th><th>Year</th><th>Copies</th><th></th></tr></thead>
                  <tbody>%s</tbody>
                </table></div>
                """.formatted(rows);
    }

    /** action is "add" or "edit" (under /books/). id is null when adding. */
    public static String form(HttpServletRequest request, String action, Integer id, BookForm form,
                              List<String> errors, String button) {
        String base = request.getContextPath() + "/books";
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
                  <label>Title <input name="title" value="%s" maxlength="100" required></label>
                  <label>Author <input name="author" value="%s" maxlength="60" required></label>
                  <label>Year published <input name="year" type="number" min="1450" value="%s" required></label>
                  <label>Copies on the shelf <input name="copies" type="number" min="0" max="999" value="%s" required></label>
                  <div class="buttons">
                    <button class="btn" type="submit">%s</button>
                    <a class="btn secondary" href="%s">Cancel</a>
                  </div>
                </form>
                """.formatted(box, base, action, hidden, escape(form.getTitle()), escape(form.getAuthor()),
                escape(form.getYear()), escape(form.getCopies()), button, base);
    }

    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
