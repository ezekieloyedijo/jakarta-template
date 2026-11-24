package com.school.example;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A SESSION LISTENER: Tomcat calls it every time a session is CREATED or DESTROYED
 * (by invalidate() or by timeout). We use it to count the visitors who are "online".
 */
@WebListener
public class SessionCounterListener implements HttpSessionListener {

    // static + Atomic: one shared counter, safe when many sessions start at the same moment
    private static final AtomicInteger ACTIVE = new AtomicInteger();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        int now = ACTIVE.incrementAndGet();
        System.out.println("[SESSION] created " + event.getSession().getId() + " -> online: " + now);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        int now = ACTIVE.decrementAndGet();
        System.out.println("[SESSION] destroyed " + event.getSession().getId() + " -> online: " + now);
    }

    public static int active() {
        return ACTIVE.get();
    }
}
