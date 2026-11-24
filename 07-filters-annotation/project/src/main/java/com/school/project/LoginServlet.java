package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * GET  /login -> the login form
 * POST /login -> check the username and password; on success put the member's name in the session
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String msg = request.getParameter("msg");
        String note = "";
        if ("login-required".equals(msg)) note = "<p class='msg error'>Please log in to see that page.</p>";
        if ("logged-out".equals(msg)) note = "<p class='msg ok'>You have logged out.</p>";
        show(request, response, note, "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String name = Accounts.check(username, request.getParameter("password"));

        if (name == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);            // 401
            show(request, response, "<p class='msg error'>Wrong username or password.</p>", username);
            return;
        }

        HttpSession session = request.getSession();
        request.changeSessionId();       // a NEW session ID after login: an old, stolen ID becomes useless (topic 13)
        session.setAttribute("user", name);
        response.sendRedirect(request.getContextPath() + "/members/notices");
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String note, String username)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "Members' login", """
                %s
                <form class="card" action="%s/login" method="post">
                  <label>Username <input name="username" value="%s" autocomplete="username" required></label>
                  <label>Password <input name="password" type="password" autocomplete="current-password" required></label>
                  <button class="btn" type="submit">Log in</button>
                </form>
                <p class="note">Try <b>teacher</b> / <b>teach123</b> or <b>prefect</b> / <b>prefect123</b>.</p>
                """.formatted(note, request.getContextPath(), Page.escape(username))));
    }
}
