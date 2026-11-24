package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * READ: GET /students shows every student.
 *
 * Registered in WEB-INF/web.xml with <load-on-startup>1</load-on-startup>, so Tomcat creates it
 * (and runs init()) when the app STARTS. That matters: init() creates the StudentStore that
 * all the other servlets use, so it must exist before the first request arrives.
 */
public class StudentListServlet extends HttpServlet {

    @Override
    public void init() {
        StudentStore store = new StudentStore();

        // An init-param from web.xml: a setting that belongs to THIS servlet only (ServletConfig)
        if ("true".equals(getInitParameter("loadSampleData"))) {
            store.add("Amaka Obi", "Female", 15, "SS1");
            store.add("Tolu Adeyemi", "Male", 12, "JSS2");
            store.add("Chidi Okeke", "Male", 17, "SS3");
        }

        // Put the store on the app's notice board (ServletContext), shared by every servlet
        getServletContext().setAttribute(StudentStore.ATTRIBUTE_NAME, store);
        System.out.println("StudentListServlet ready: " + store.findAll().size() + " sample students loaded.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Student> students = StudentStore.from(getServletContext()).findAll();

        String content = message(request.getParameter("msg"))
                + "<p class='toolbar'><span>" + students.size() + " student(s)</span>"
                + "<a class='btn' href='add-student'>+ Add student</a></p>"
                + Page.table(students);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(getServletContext(), "All students", content));
    }

    /**
     * After Add, Edit, or Delete, the other servlets REDIRECT here with ?msg=added (or updated / deleted).
     * We only show our own fixed sentences, never the raw text from the URL.
     */
    private String message(String msg) {
        if (msg == null) return "";
        return switch (msg) {
            case "added" -> "<div class='msg ok'>✅ Student added.</div>";
            case "updated" -> "<div class='msg ok'>✅ Student updated.</div>";
            case "deleted" -> "<div class='msg ok'>🗑️ Student deleted.</div>";
            default -> "";
        };
    }
}
