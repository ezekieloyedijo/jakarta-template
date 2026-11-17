package com.school.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Notice: NO @WebServlet annotation here!
 * This servlet is registered the "classic" way, in WEB-INF/web.xml:
 *   <servlet>         = WHO   (nickname "classic" -> this class)
 *   <servlet-mapping> = WHERE (URL /classic -> nickname "classic")
 */
public class ClassicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("""
                <!DOCTYPE html><html><head><link rel="stylesheet" href="css/style.css"></head><body>
                <h1>I am ClassicServlet</h1>
                <p>I have no annotation. The container found me because <code>web.xml</code> maps
                   <code>/classic</code> to me.</p>
                <p><a href="./">Back</a></p></body></html>
                """);
    }
}
