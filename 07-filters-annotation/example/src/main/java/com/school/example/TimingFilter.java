package com.school.example;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * BEFORE AND AFTER: code above chain.doFilter runs on the way IN,
 * code below it runs on the way OUT, after the servlet has finished.
 */
@WebFilter("/*")
public class TimingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        long start = System.currentTimeMillis();                  // BEFORE

        chain.doFilter(req, res);                                 // the servlet runs here

        long took = System.currentTimeMillis() - start;           // AFTER
        String uri = ((HttpServletRequest) req).getRequestURI();
        System.out.println("[TIME] " + uri + " took " + took + " ms");
    }
}
