package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Date;

/**
 * TECHNIQUE 4: HttpSession.
 * The number of visits is stored ON THE SERVER, in this user's session.
 * The browser only carries the session ID, in a cookie named JSESSIONID.
 */
@WebServlet("/session-counter")
public class SessionCounterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();          // find this user's session, or create one

        Integer visits = (Integer) session.getAttribute("visits");   // null the first time
        visits = (visits == null) ? 1 : visits + 1;
        session.setAttribute("visits", visits);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("Session counter", """
                <p class="msg ok">Visits in this session: <b>%d</b></p>
                <div class="table-wrap"><table>
                  <tr><th>Session ID (also in the JSESSIONID cookie)</th><td>%s</td></tr>
                  <tr><th>New session?</th><td>%s</td></tr>
                  <tr><th>Created</th><td>%s</td></tr>
                  <tr><th>Times out after (idle)</th><td>%d minutes</td></tr>
                </table></div>
                <p>Open this page in a <b>private window</b>: you get a new session, starting again at 1.</p>
                <p><a class="btn danger" href="session-end">Log out (end the session)</a></p>
                """.formatted(visits, session.getId(), session.isNew() ? "yes" : "no",
                new Date(session.getCreationTime()), session.getMaxInactiveInterval() / 60)));
    }
}
