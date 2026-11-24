package com.school.project;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * EVENT 3 and 4: a session is CREATED or DESTROYED.
 * Counts the readers using the library right now. The number is shown at the bottom of every page.
 */
@WebListener
public class ReadersOnlineListener implements HttpSessionListener {

    private static final AtomicInteger ONLINE = new AtomicInteger();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        ONLINE.incrementAndGet();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        ONLINE.decrementAndGet();
    }

    public static int online() {
        return ONLINE.get();
    }
}
