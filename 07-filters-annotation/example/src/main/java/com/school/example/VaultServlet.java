package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Only reached when SecretKeyFilter lets the request through. It doesn't check the key itself. */
@WebServlet("/secret/vault")
public class VaultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page(request.getContextPath(), "The vault",
                "<p class='msg ok'>💰 You are in! The filter checked the key, so this servlet doesn't have to.</p>"));
    }
}
