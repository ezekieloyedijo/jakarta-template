package com.school.project;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * EVENT 1: the app STARTS. Runs once, before the first request:
 *   - read the database address from web.xml (<context-param> "dbUrl")
 *   - create the table if it isn't there yet, and add 3 books if it is empty
 *   - put the BookDao in the ServletContext, so every servlet can use it
 * EVENT 2: the app STOPS. Unregister the JDBC driver so Tomcat can unload the app cleanly.
 */
@WebListener
public class DatabaseListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext app = event.getServletContext();
        String url = app.getInitParameter("dbUrl");

        try {
            Class.forName("org.h2.Driver");                 // load the H2 driver from WEB-INF/lib
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("The H2 driver is missing. Is it in pom.xml?", e);
        }

        BookDao dao = new BookDao(url);
        dao.createTableIfMissing();
        if (dao.count() == 0) {
            dao.insert("Things Fall Apart", "Chinua Achebe", 1958, 4);
            dao.insert("Purple Hibiscus", "Chimamanda Ngozi Adichie", 2003, 3);
            dao.insert("The Lion and the Jewel", "Wole Soyinka", 1959, 2);
        }
        app.setAttribute(BookDao.ATTRIBUTE_NAME, dao);
        System.out.println("[DB] Library ready at " + url + " with " + dao.count() + " books");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            if (d.getClass().getClassLoader() == getClass().getClassLoader()) {
                try {
                    DriverManager.deregisterDriver(d);
                } catch (SQLException ignored) {
                    // shutting down anyway
                }
            }
        }
        System.out.println("[DB] Library closed");
    }
}
