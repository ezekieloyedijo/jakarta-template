package com.school.project;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * FILTER 3, THE GUARD: protects EVERY page under /members/.
 * Not logged in?  -> redirect to the login page. chain.doFilter is NOT called, so no notice servlet runs.
 * Logged in?      -> let the request through.
 *
 * The four notice servlets contain no login check at all: this one class protects them all,
 * and any page added under /members/ later is protected automatically.
 */
@WebFilter("/members/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (Page.user(request) == null) {
            response.sendRedirect(request.getContextPath() + "/login?msg=login-required");
            return;                                              // STOP here
        }

        // Tell the browser not to keep copies of members' pages, so "Back" after logout shows nothing private
        response.setHeader("Cache-Control", "no-store");
        chain.doFilter(req, res);                                // let it through
    }
}
