package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * READ: GET /members/notices shows every notice, newest first.
 * There is no login check here: AuthFilter already did it.
 */
@WebServlet("/members/notices")
public class NoticeListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String base = request.getContextPath() + "/members/notices";
        List<Notice> notices = NoticeStore.from(getServletContext()).findAll();

        StringBuilder cards = new StringBuilder();
        for (Notice n : notices) {
            cards.append("""
                    <div class="item">
                      <h3>%s</h3>
                      <p>%s</p>
                      <p><small>%s · %s</small></p>
                      <div class="buttons">
                        <a class="btn small" href="%s/edit?id=%d">Edit</a>
                        <form action="%s/delete" method="post" data-title="%s"
                              onsubmit="return confirm('Delete ' + this.dataset.title + '?')">
                          <input type="hidden" name="id" value="%d">
                          <button class="btn small danger" type="submit">Delete</button>
                        </form>
                      </div>
                    </div>
                    """.formatted(Page.escape(n.getTitle()), Page.escape(n.getBody()), Page.escape(n.getAuthor()),
                    n.getPosted(), base, n.getId(), base, Page.escape(n.getTitle()), n.getId()));
        }

        String msg = switch (String.valueOf(request.getParameter("msg"))) {
            case "added" -> "<p class='msg ok'>Notice posted.</p>";
            case "updated" -> "<p class='msg ok'>Notice updated.</p>";
            case "deleted" -> "<p class='msg ok'>Notice deleted.</p>";
            default -> "";
        };
        String content = notices.isEmpty()
                ? "<p class='empty'>No notices yet.</p>"
                : "<div class='cards'>" + cards + "</div>";

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "Notices", msg + content
                + "<p style='margin-top:16px'><a class='btn' href='" + base + "/add'>➕ New notice</a></p>"));
    }
}
