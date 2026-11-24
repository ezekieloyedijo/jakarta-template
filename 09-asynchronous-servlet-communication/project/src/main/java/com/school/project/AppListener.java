package com.school.project;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * When the app starts: create the report store (with 2 sample requests) and the WORKER POOL,
 * the background threads that build reports. When it stops: shut the workers down.
 */
@WebListener
public class AppListener implements ServletContextListener {

    public static final String WORKERS = "workers";

    public static ExecutorService workers(ServletContext app) {
        return (ExecutorService) app.getAttribute(WORKERS);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext app = event.getServletContext();

        ReportStore store = new ReportStore();
        store.add("First term results", "SS1", 25);
        store.add("Mock exam", "JSS3", 30);
        app.setAttribute(ReportStore.ATTRIBUTE_NAME, store);

        app.setAttribute(WORKERS, Executors.newFixedThreadPool(3));   // at most 3 reports built at the same time
        System.out.println("[APP] Report store and 3 worker threads ready");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        workers(event.getServletContext()).shutdownNow();
        System.out.println("[APP] Worker threads stopped");
    }
}
