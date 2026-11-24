package com.school.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * FORWARD vs INCLUDE vs REDIRECT, side by side.
 *
 *   /dispatch                -> the menu
 *   /dispatch?mode=forward   -> server-side hand-over to /target    (address bar does NOT change)
 *   /dispatch?mode=include   -> paste /fragment's output into my page (address bar does NOT change)
 *   /dispatch?mode=redirect  -> tell the BROWSER to go to /target    (address bar CHANGES)
 */
// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/dispatch"
public class DispatchStartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String mode = request.getParameter("mode");

        // A request ATTRIBUTE: a Java object attached to THIS request by OUR code
        request.setAttribute("message", "Hello! I was attached by DispatchStartServlet.");

        if ("forward".equals(mode)) {
            System.out.println(">>> DispatchStart: FORWARDING to /target (inside the server)");
            request.getRequestDispatcher("/target").forward(request, response);
            return; // after forward, the other servlet owns the response, so we stop here

        } else if ("redirect".equals(mode)) {
            System.out.println(">>> DispatchStart: REDIRECTING the browser to /target (302)");
            response.sendRedirect("target?cameFrom=redirect");
            return; // the attribute above is LOST: the browser makes a brand-new request

        } else if ("include".equals(mode)) {
            System.out.println(">>> DispatchStart: INCLUDING /fragment inside my own page");
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<h1>I am DispatchStartServlet (mode = include)</h1>");
            out.println("<p>Everything outside the yellow box is written by me. The box comes from <code>/fragment</code>:</p>");
            request.getRequestDispatcher("/fragment").include(request, response);
            out.println("<p>...and I continue writing after the included part. Look at the address bar: it still says <code>/dispatch</code>.</p>");
            out.println("<p><a href='dispatch'>⬅ Back to the dispatch menu</a></p></body></html>");
            return;
        }

        // No mode: show the menu
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("Forward vs Include vs Redirect", """
                <p>Click each one and <b>watch the browser address bar</b> (and the terminal):</p>
                <ul>
                  <li><a href="dispatch?mode=forward">Forward to /target</a> (server-side, 1 request)</li>
                  <li><a href="dispatch?mode=include">Include /fragment</a> (server-side, 1 request)</li>
                  <li><a href="dispatch?mode=redirect">Redirect to /target</a> (browser-side, 2 requests)</li>
                </ul>
                <p class="note">Tip: open F12, then the <b>Network</b> tab, before clicking "Redirect". You'll see a <b>302</b> followed by a second request.</p>
                """));
    }
}
