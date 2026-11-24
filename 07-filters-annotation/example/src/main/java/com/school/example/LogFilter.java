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
 * YOUR FIRST FILTER: it sees EVERY request ("/*") before any servlet does,
 * writes one line in the terminal, and lets the request continue.
 */
@WebFilter("/*")
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;     // we know it's HTTP, so cast it
        System.out.println("[LOG] " + request.getMethod() + " " + request.getRequestURI());

        chain.doFilter(req, res);      // "pass it on": the next filter, or the servlet
    }
}
