package com.school.project.controller;

import com.school.project.model.StudentDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** When the app starts: create the DAO with 3 sample students and share it (topic 08). */
@WebListener
public class AppListener implements ServletContextListener {

    public static final String DAO = "studentDao";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        StudentDao dao = new StudentDao();
        dao.insert("Amaka Obi", "Female", 15, "SS1");
        dao.insert("Tolu Adeyemi", "Male", 12, "JSS2");
        dao.insert("Chidi Okeke", "Male", 17, "SS3");
        event.getServletContext().setAttribute(DAO, dao);
    }
}
