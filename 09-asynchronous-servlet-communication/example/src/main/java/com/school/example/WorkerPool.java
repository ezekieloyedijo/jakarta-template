package com.school.example;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * THE KITCHEN: a small team of 4 background threads (an "executor") that do the slow jobs.
 * Created when the app starts, shut down when it stops (a listener, from topic 08).
 */
@WebListener
public class WorkerPool implements ServletContextListener {

    private static final String ATTRIBUTE_NAME = "workers";

    public static ExecutorService of(ServletContext app) {
        return (ExecutorService) app.getAttribute(ATTRIBUTE_NAME);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setAttribute(ATTRIBUTE_NAME, Executors.newFixedThreadPool(4));
        System.out.println("[POOL] 4 worker threads ready");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        of(event.getServletContext()).shutdownNow();
        System.out.println("[POOL] worker threads stopped");
    }

    /** Pretend to do slow work (a big report, a call to another website...). */
    public static void slowJob(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String page(String title, String body) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s</title>
                  <link rel="stylesheet" href="css/style.css">
                </head>
                <body>
                  <header><a class="brand" href="./">⏳ Async demos</a></header>
                  <main>
                    <h1>%s</h1>
                    %s
                    <p><a href="./">← back to all demos</a></p>
                  </main>
                </body>
                </html>
                """.formatted(title, title, body);
    }
}
