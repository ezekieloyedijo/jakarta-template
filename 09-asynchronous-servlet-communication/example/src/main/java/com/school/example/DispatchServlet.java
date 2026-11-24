package com.school.example;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalTime;

/**
 * dispatch() instead of complete(): the worker does the slow job, puts the result in the request,
 * then hands the request to a JSP, which builds the page (like forward in topic 06).
 */
@WebServlet(urlPatterns = "/async-dispatch", asyncSupported = true)
public class DispatchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        AsyncContext async = request.startAsync();

        WorkerPool.of(getServletContext()).submit(() -> {
            WorkerPool.slowJob(2000);
            async.getRequest().setAttribute("result", "The weather report is ready: sunny, 31°C.");
            async.getRequest().setAttribute("finishedAt", LocalTime.now().withNano(0).toString());
            async.dispatch("/WEB-INF/views/result.jsp");             // the JSP writes the page and finishes
        });
    }
}
