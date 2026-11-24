package com.school.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** The destination of the forward and of the redirect. It reports what it can "see". */
// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/target"
public class DispatchTargetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object message = request.getAttribute("message");    // only survives a FORWARD
        String cameFrom = request.getParameter("cameFrom");  // we added it to the redirect URL

        String how = (cameFrom != null) ? "a REDIRECT (the browser made a new request)" : "a FORWARD (inside the server)";

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("""
                <!DOCTYPE html><html><head><meta charset="UTF-8"><link rel="stylesheet" href="css/style.css"></head><body>
                <h1>I am DispatchTargetServlet</h1>
                <table>
                  <tr><th>I was reached by</th><td>%s</td></tr>
                  <tr><th>Request attribute "message"</th><td>%s</td></tr>
                  <tr><th>Look at your address bar</th><td>%s</td></tr>
                </table>
                <p><a href="dispatch">⬅ Back to the dispatch menu</a></p>
                </body></html>
                """.formatted(how,
                message == null ? "<b>null</b> (lost: a redirect is a brand-new request)" : "<b>" + message + "</b>",
                cameFrom != null ? "It says <code>/target...</code>" : "It still says <code>/dispatch?mode=forward</code>"));
    }
}
