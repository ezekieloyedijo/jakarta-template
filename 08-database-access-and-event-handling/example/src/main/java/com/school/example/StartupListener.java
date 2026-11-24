package com.school.example;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

/**
 * A LISTENER: Tomcat calls it when the APP STARTS and when it STOPS. No request needed.
 * We use the start event to create the table and put some rows in it, before any user arrives.
 */
@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            Class.forName("org.h2.Driver");          // make sure the H2 driver is loaded inside Tomcat

            try (Connection con = Db.connect();
                 Statement st = con.createStatement()) {
                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS fruit (
                            id    INT AUTO_INCREMENT PRIMARY KEY,
                            name  VARCHAR(40) NOT NULL,
                            price INT NOT NULL
                        )""");
                st.executeUpdate("INSERT INTO fruit (name, price) VALUES ('Mango', 300), ('Orange', 150), ('Pawpaw', 900)");
            }
            System.out.println("[STARTUP] App started: table 'fruit' created with 3 rows");
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException("Could not set up the database", e);   // the app will not start
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Unregister the driver this app loaded, so Tomcat can unload the app cleanly
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            if (d.getClass().getClassLoader() == getClass().getClassLoader()) {
                try {
                    DriverManager.deregisterDriver(d);
                } catch (SQLException ignored) {
                    // we are shutting down anyway
                }
            }
        }
        System.out.println("[STARTUP] App stopped");
    }
}
