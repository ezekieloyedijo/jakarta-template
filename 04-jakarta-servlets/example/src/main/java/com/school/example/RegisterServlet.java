package com.school.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ONE URL, TWO METHODS: the classic form pattern.
 *   GET  /register -> doGet():  show the empty form
 *   POST /register -> doPost(): read EVERY kind of input, validate, respond
 */
// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/register"
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("Club registration", form()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8"); // BEFORE reading any parameter

        // 1. READ: every value arrives as a String (or null if it wasn't sent)
        String name = request.getParameter("name");           // text box
        String ageText = request.getParameter("age");         // number box (still a String!)
        String level = request.getParameter("level");         // radio buttons: ONE value
        String club = request.getParameter("club");           // dropdown: ONE value
        String[] hobbies = request.getParameterValues("hobby"); // checkboxes: MANY values, or null

        // 2. VALIDATE: never trust the browser
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("Name is required.");
        }
        int age = 0;
        try {
            age = Integer.parseInt(ageText);
            if (age < 10 || age > 100) errors.add("Age must be between 10 and 100.");
        } catch (NumberFormatException e) {
            errors.add("Age must be a whole number (you typed: \"" + Html.escape(ageText) + "\").");
        }
        if (level == null) {
            errors.add("Please choose a level.");
        }

        response.setContentType("text/html;charset=UTF-8");

        // 3a. ERRORS: status 400 and show the form again
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            StringBuilder list = new StringBuilder("<div class='error'><b>Please fix:</b><ul>");
            for (String e : errors) list.append("<li>").append(e).append("</li>");
            list.append("</ul></div>");
            response.getWriter().println(Html.page("Club registration", list + form()));
            return;
        }

        // 3b. SUCCESS
        String hobbyText = (hobbies == null) ? "(none ticked)" : Html.escape(String.join(", ", hobbies));
        response.getWriter().println(Html.page("Registered! 🎉", """
                <div class="ok">Welcome, <b>%s</b>!</div>
                <table>
                  <tr><th>getParameter("age") as int</th><td>%d (next year you'll be %d)</td></tr>
                  <tr><th>getParameter("level") (radio)</th><td>%s</td></tr>
                  <tr><th>getParameter("club") (dropdown)</th><td>%s</td></tr>
                  <tr><th>getParameterValues("hobby") (checkboxes)</th><td>%s</td></tr>
                </table>
                <p><a href="register">Register someone else</a></p>
                """.formatted(Html.escape(name), age, age + 1, Html.escape(level), Html.escape(club), hobbyText)));
    }

    private String form() {
        // 'novalidate' switches OFF the browser's checks so you can SEE the server-side validation work
        return """
                <form action="register" method="post" novalidate>
                  <label>Name <input name="name"></label>
                  <label>Age <input name="age" type="number"></label>
                  <p>Level:
                    <label><input type="radio" name="level" value="Beginner"> Beginner</label>
                    <label><input type="radio" name="level" value="Advanced"> Advanced</label></p>
                  <label>Club
                    <select name="club">
                      <option>Coding Club</option><option>Debate Club</option><option>Chess Club</option>
                    </select></label>
                  <p>Hobbies (tick any):
                    <label><input type="checkbox" name="hobby" value="Football"> Football</label>
                    <label><input type="checkbox" name="hobby" value="Music"> Music</label>
                    <label><input type="checkbox" name="hobby" value="Gaming"> Gaming</label></p>
                  <button type="submit">Register</button>
                </form>
                <p class="note">Try submitting it EMPTY, or with letters as the age, to see validation and the 400 status.</p>
                """;
    }
}
