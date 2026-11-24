package com.school.project;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/about"
public class AboutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext app = getServletContext();

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Layout.page(app, "About me", """
                <p>Name: <b>%s</b></p>
                <p>Role: %s</p>
                <p>Email: <a href="mailto:%s">%s</a></p>
                <p>I'm learning Jakarta EE: servlets, JSP, sessions, databases, and security.</p>
                <p><small>(All of these details come from <code>WEB-INF/web.xml</code>.)</small></p>
                """.formatted(app.getInitParameter("ownerName"), app.getInitParameter("ownerTitle"),
                app.getInitParameter("ownerEmail"), app.getInitParameter("ownerEmail"))));
    }
}
