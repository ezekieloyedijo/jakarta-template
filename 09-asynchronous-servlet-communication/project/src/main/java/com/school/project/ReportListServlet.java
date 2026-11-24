package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** READ: GET /reports lists every request with its status and the buttons for what you can do next. */
@WebServlet("/reports")
public class ReportListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String base = request.getContextPath() + "/reports";
        List<Report> reports = ReportStore.from(getServletContext()).findAll();

        StringBuilder rows = new StringBuilder();
        for (Report r : reports) {
            boolean ready = r.getStatus() == Report.Status.READY;
            String status = ready ? "<span class='badge ok'>ready</span>" : "<span class='badge wait'>pending</span>";
            // Generate is a POST form: it changes data. The button text changes while the browser waits.
            String main = ready
                    ? "<a class='btn small' href='%s/view?id=%d'>View</a>".formatted(base, r.getId())
                    : """
                      <form action="%s/generate" method="post"
                            onsubmit="this.querySelector('button').textContent='Generating…'">
                        <input type="hidden" name="id" value="%d">
                        <button class="btn small" type="submit">Generate</button>
                      </form>""".formatted(base, r.getId());
            rows.append("""
                    <tr>
                      <td>%d</td><td>%s</td><td>%s</td><td>%d</td><td>%s</td>
                      <td class="actions">
                        %s
                        <a class="btn small secondary" href="%s/edit?id=%d">Edit</a>
                        <form action="%s/delete" method="post" data-title="%s"
                              onsubmit="return confirm('Delete ' + this.dataset.title + '?')">
                          <input type="hidden" name="id" value="%d">
                          <button class="btn small danger" type="submit">Delete</button>
                        </form>
                      </td>
                    </tr>
                    """.formatted(r.getId(), Page.escape(r.getTitle()), r.getClassName(), r.getStudents(), status,
                    main, base, r.getId(), base, Page.escape(r.getTitle()), r.getId()));
        }

        String content = reports.isEmpty()
                ? "<p class='empty'>No report requests yet.</p>"
                : """
                  <div class="table-wrap"><table>
                    <thead><tr><th>#</th><th>Title</th><th>Class</th><th>Students</th><th>Status</th><th></th></tr></thead>
                    <tbody>%s</tbody>
                  </table></div>
                  """.formatted(rows);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "All reports", Page.message(request.getParameter("msg"))
                + content
                + "<p class='note'>Generating takes about 3 seconds. It runs on a <b>worker thread</b>, so while you "
                + "wait, Tomcat's request thread is already serving other users.</p>"));
    }
}
