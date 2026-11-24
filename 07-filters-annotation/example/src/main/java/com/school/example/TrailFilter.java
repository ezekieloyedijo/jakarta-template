package com.school.example;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FILTER ORDER. This one class is registered TWICE in web.xml, as "first" and "second",
 * each with its own init parameter "label". web.xml decides the order: the <filter-mapping>
 * that is written first runs first. (@WebFilter has no way to set the order.)
 *
 * Each copy prints "in" and "out" in the terminal, and adds its "in" step to a list called "trail"
 * that travels with the request, so ChainServlet can show it on the page.
 */
public class TrailFilter implements Filter {

    private String label;

    @Override
    public void init(FilterConfig config) {
        label = config.getInitParameter("label");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        trail(req).add(label + ": in");
        System.out.println("[TRAIL] " + label + ": in");

        chain.doFilter(req, res);

        System.out.println("[TRAIL] " + label + ": out");
    }

    @SuppressWarnings("unchecked")
    static List<String> trail(ServletRequest req) {
        List<String> trail = (List<String>) req.getAttribute("trail");
        if (trail == null) {
            trail = new ArrayList<>();
            req.setAttribute("trail", trail);
        }
        return trail;
    }
}
