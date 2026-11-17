package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Produces only a SMALL PIECE of HTML (not a whole page), so that another servlet can INCLUDE it.
 * (Visit /fragment directly and you'll just see the yellow box on its own.)
 */
@WebServlet("/fragment")
public class FragmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8"); // ignored when included: the includer owns the headers
        response.getWriter().println(
                "<div class='note'>📦 I am FragmentServlet. My output was <b>included</b>. "
                        + "I can see the attribute too: <i>" + request.getAttribute("message") + "</i></div>");
    }
}
