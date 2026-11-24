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
 * FILTER 2: writes one line per request in the terminal, AFTER the servlet has run,
 * so it can show the status code and the time taken:
 *     [ACCESS] POST /members/notices/add -> 302 in 3 ms (user: Mrs. Bello)
 */
@WebFilter("/*")
public class AccessLogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        long start = System.currentTimeMillis();                 // on the way IN

        chain.doFilter(req, res);

        String user = Page.user(request);                        // on the way OUT
        System.out.printf("[ACCESS] %s %s -> %d in %d ms (user: %s)%n", request.getMethod(),
                request.getRequestURI(), response.getStatus(), System.currentTimeMillis() - start,
                user == null ? "-" : user);
    }
}
