package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * TECHNIQUE 2: URL REWRITING.
 * The data travels at the end of every link:  url-rewriting?lang=fr&page=2
 * Every link on the page must carry it again, or it is lost.
 */
@WebServlet("/url-rewriting")
public class UrlRewritingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String lang = request.getParameter("lang");
        if (lang == null) lang = "en";

        String hello = switch (lang) {
            case "fr" -> "Bonjour";
            case "yo" -> "Ẹ n lẹ";
            default -> "Hello";
        };

        int pageNo = toPage(request.getParameter("page"));

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("URL rewriting", """
                <p class="msg ok">%s! You are on page <b>%d</b>, language <b>%s</b>.</p>
                <p>Choose a language:
                   <a href="url-rewriting?lang=en&page=%d">English</a> ·
                   <a href="url-rewriting?lang=fr&page=%d">Français</a> ·
                   <a href="url-rewriting?lang=yo&page=%d">Yorùbá</a></p>
                <p><a class="btn" href="url-rewriting?lang=%s&page=%d">Next page →</a></p>
                <p>Look at the address bar: the data is in the URL. Remove <code>?lang=...</code> and it is forgotten.</p>
                """.formatted(hello, pageNo, Html.escape(lang), pageNo, pageNo, pageNo,
                Html.escape(lang), pageNo + 1)));
    }

    private int toPage(String text) {
        try {
            return Math.max(1, Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return 1;                                        // missing or not a number: page 1
        }
    }
}
