package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * THE PROBLEM: a normal servlet doing a 3-second job.
 * Tomcat's request thread is STUCK here for the whole 3 seconds, unable to serve anyone else.
 */
@WebServlet("/blocking")
public class BlockingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestThread = Thread.currentThread().getName();
        long start = System.currentTimeMillis();
        System.out.println("[BLOCKING] " + requestThread + " starts the slow job and waits...");

        WorkerPool.slowJob(3000);                                    // the request thread waits here

        System.out.println("[BLOCKING] " + requestThread + " is free again");
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(WorkerPool.page("Blocking servlet", """
                <p class="msg error">Done after %d ms.</p>
                <p>The whole time, Tomcat's thread <code>%s</code> was <b>blocked</b>: it could not serve anyone else.</p>
                """.formatted(System.currentTimeMillis() - start, requestThread)));
    }
}
