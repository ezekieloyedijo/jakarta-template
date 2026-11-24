package com.school.example;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Two kinds of configuration:
 *   - ServletConfig  (init params): belong to THIS servlet only. <init-param> inside its <servlet> in web.xml.
 *   - ServletContext (context params): belong to the WHOLE app.   <context-param> in web.xml.
 */
// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/config"
public class ConfigServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ServletConfig: the note taped to THIS desk
        String deskName = getInitParameter("deskName");
        int maxAmount = Integer.parseInt(getInitParameter("maxAmount"));

        // ServletContext: the branch notice board everyone can read
        ServletContext app = getServletContext();
        String school = app.getInitParameter("schoolName");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("Configuration", """
                <h2>ServletConfig: only for this servlet (&lt;init-param&gt; in web.xml)</h2>
                <table>
                  <tr><th>deskName</th><td>%s</td></tr>
                  <tr><th>maxAmount (converted to int)</th><td>%,d</td></tr>
                </table>
                <h2>ServletContext: for the whole app (&lt;context-param&gt; in web.xml)</h2>
                <table>
                  <tr><th>schoolName</th><td>%s</td></tr>
                </table>
                """.formatted(deskName, maxAmount, school)));
    }
}
