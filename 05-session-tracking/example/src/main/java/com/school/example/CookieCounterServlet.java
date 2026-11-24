package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * TECHNIQUE 3: COOKIES.
 * The number of visits is stored IN THE BROWSER, in a cookie called "visits".
 * The browser sends it back with every request; we read it, add 1, and send it back.
 */
@WebServlet("/cookie-counter")
public class CookieCounterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // DELETE: same name + max age 0 = "browser, throw this cookie away"
        if (request.getParameter("reset") != null) {
            Cookie gone = new Cookie("visits", "");
            gone.setMaxAge(0);
            response.addCookie(gone);
            response.sendRedirect("cookie-counter");
            return;
        }

        // READ: the browser sends ALL its cookies for this site; find ours
        int visits = 0;
        Cookie[] cookies = request.getCookies();           // null when there are no cookies at all
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("visits")) {
                    visits = toNumber(c.getValue());
                }
            }
        }
        visits++;

        // CREATE / UPDATE: send the new value; the browser keeps it for 1 day
        Cookie cookie = new Cookie("visits", String.valueOf(visits));
        cookie.setMaxAge(60 * 60 * 24);                      // in seconds
        cookie.setHttpOnly(true);                            // JavaScript can't read it (safer)
        response.addCookie(cookie);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("Cookie counter", """
                <p class="msg ok">You have visited this page <b>%d</b> time(s).</p>
                <p>Press <b>F5</b> to count up. Close the browser and come back: the number is still there
                   (the cookie lasts 1 day). Open F12 → Application → Cookies to see it.</p>
                <p><a class="btn danger" href="cookie-counter?reset=1">Delete the cookie</a></p>
                """.formatted(visits)));
    }

    private int toNumber(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;                                        // someone edited the cookie: start again
        }
    }
}
