package com.school.example;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * BLOCKING A REQUEST + INIT PARAMETERS + LIFE CYCLE.
 * Guards everything under /secret/. Without ?key=open-sesame the request is STOPPED here:
 * chain.doFilter is never called, so the servlet never runs.
 */
@WebFilter(urlPatterns = "/secret/*",
        initParams = @WebInitParam(name = "key", value = "open-sesame"))
public class SecretKeyFilter implements Filter {

    private String key;

    @Override
    public void init(FilterConfig config) {                      // ONCE, when the app starts
        key = config.getInitParameter("key");
        System.out.println("[SECRET] SecretKeyFilter ready");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (key.equals(request.getParameter("key"))) {
            chain.doFilter(req, res);                             // right key: let it through
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // wrong key: block it (403)
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println(Html.page(request.getContextPath(), "Blocked by a filter", """
                    <p class="msg error">⛔ Wrong or missing key. <code>SecretKeyFilter</code> stopped this request,
                       so <code>VaultServlet</code> never ran.</p>
                    <p><a href="vault?key=open-sesame">Try again with the right key</a></p>
                    """));
        }
    }

    @Override
    public void destroy() {                                       // ONCE, when the app stops
        System.out.println("[SECRET] SecretKeyFilter stopped");
    }
}
