package com.school.project;

import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SIMPLE CALCULATOR: uses every idea from the topic 04 lesson.
 *
 *  - URL mapping with two addresses           (Step 5)
 *  - init() for one-time setup                  (Step 3)
 *  - reading and validating parameters          (Steps 6 & 8)
 *  - status codes for bad input                 (Step 7)
 *  - ServletConfig init param                   (Step 9)
 *  - ServletContext attribute shared by ALL users, thread-safe (Steps 4 & 9)
 */
@WebServlet(
        urlPatterns = {"/calculator", "/calc"},
        loadOnStartup = 1,
        initParams = @WebInitParam(name = "decimalPlaces", value = "2"))
public class CalculatorServlet extends HttpServlet {

    private int decimalPlaces;      // read once in init(), then only READ: safe to share
    private AtomicInteger totalCalculations;

    @Override
    public void init() {
        decimalPlaces = Integer.parseInt(getInitParameter("decimalPlaces"));

        // Put a shared counter on the app's "notice board" (ServletContext) so any servlet could read it
        totalCalculations = new AtomicInteger();
        getServletContext().setAttribute("totalCalculations", totalCalculations);

        System.out.println("CalculatorServlet ready. Rounding to " + decimalPlaces + " decimal places.");
    }

    /** GET: show an empty calculator. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        show(response, "", "", "+", null, null);
    }

    /** POST: read, validate, calculate, show. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. READ (local variables only: each request gets its own copies)
        String aText = request.getParameter("a");
        String bText = request.getParameter("b");
        String op = request.getParameter("op");

        // 2. VALIDATE
        BigDecimal a;
        BigDecimal b;
        try {
            a = new BigDecimal(aText.trim());
            b = new BigDecimal(bText.trim());
        } catch (NullPointerException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400: the user's input is wrong
            show(response, aText, bText, op, null, "Both boxes must contain numbers, e.g. 12 or 3.5");
            return;
        }
        if (op == null || !"+-*/".contains(op) || op.length() != 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(response, aText, bText, "+", null, "Please choose an operation.");
            return;
        }
        if (op.equals("/") && b.signum() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(response, aText, bText, op, null, "You can't divide by zero.");
            return;
        }

        // 3. CALCULATE (the real "business logic", which is just a few lines!)
        BigDecimal result = switch (op) {
            case "+" -> a.add(b);
            case "-" -> a.subtract(b);
            case "*" -> a.multiply(b);
            default -> a.divide(b, decimalPlaces, RoundingMode.HALF_UP);
        };
        result = result.setScale(decimalPlaces, RoundingMode.HALF_UP).stripTrailingZeros();

        int count = totalCalculations.incrementAndGet();
        System.out.println("Calculation #" + count + ": " + a + " " + op + " " + b + " = " + result.toPlainString());

        // 4. RESPOND
        show(response, aText, bText, op,
                a.toPlainString() + " " + op + " " + b.toPlainString() + " = <b>" + result.toPlainString() + "</b>", null);
    }

    /** Draws the page. The form keeps the user's values so they don't have to retype them. */
    private void show(HttpServletResponse response, String a, String b, String op,
                      String result, String error) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        String message = "";
        if (error != null) message = "<div class='error'>⚠️ " + error + "</div>";
        if (result != null) message = "<div class='ok'>" + result + "</div>";

        response.getWriter().println("""
                <!DOCTYPE html>
                <html><head><meta charset="UTF-8"><title>Simple Calculator</title>
                <link rel="stylesheet" href="css/style.css"></head>
                <body>
                <h1>🧮 Simple Calculator</h1>
                %s
                <form action="calculator" method="post" novalidate>
                  <label>First number <input name="a" value="%s"></label>
                  <label>Operation
                    <select name="op">
                      <option value="+" %s>+ add</option>
                      <option value="-" %s>- subtract</option>
                      <option value="*" %s>× multiply</option>
                      <option value="/" %s>÷ divide</option>
                    </select></label>
                  <label>Second number <input name="b" value="%s"></label>
                  <button type="submit">Calculate</button>
                </form>
                <p><small>Calculations done by ALL users since the server started: <b>%d</b>
                (stored in the ServletContext, shared by everyone)</small></p>
                </body></html>
                """.formatted(message, escape(a), sel(op, "+"), sel(op, "-"), sel(op, "*"), sel(op, "/"),
                escape(b), totalCalculations.get()));
    }

    private String sel(String current, String option) {
        return option.equals(current) ? "selected" : "";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
