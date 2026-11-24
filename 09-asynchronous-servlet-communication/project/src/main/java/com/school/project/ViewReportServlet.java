package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** READ ONE: GET /reports/view?id=1 shows a generated report. */
@WebServlet("/reports/view")
public class ViewReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Report r = EditReportServlet.find(request, response);
        if (r == null) return;

        String base = request.getContextPath() + "/reports";
        String content;
        if (r.getStatus() != Report.Status.READY) {
            content = "<p class='msg error'>This report has not been generated yet. "
                    + "<a href='" + base + "'>Back to the list</a>.</p>";
        } else {
            StringBuilder rows = new StringBuilder();
            List<Integer> scores = r.getScores();
            for (int i = 0; i < scores.size(); i++) {
                rows.append("<tr><td>Student %d</td><td class='num'>%d</td></tr>".formatted(i + 1, scores.get(i)));
            }
            content = """
                    %s
                    <div class="tiles">
                      <div class="tile"><b>%s</b><span>Average</span></div>
                      <div class="tile"><b>%d</b><span>Highest</span></div>
                      <div class="tile"><b>%d</b><span>Lowest</span></div>
                    </div>
                    <p>Class <b>%s</b>, %d students. Generated at %s in %d ms on a worker thread.</p>
                    <div class="table-wrap"><table>
                      <thead><tr><th>Student</th><th class="num">Score</th></tr></thead>
                      <tbody>%s</tbody>
                    </table></div>
                    <p style="margin-top:16px"><a class="btn secondary" href="%s">← All reports</a></p>
                    """.formatted(Page.message(request.getParameter("msg")), r.average(), r.highest(), r.lowest(),
                    r.getClassName(), r.getStudents(), r.getGeneratedAt(), r.getTookMillis(), rows, base);
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, r.getTitle(), content));
    }
}
