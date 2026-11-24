package com.school.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * STEPS 5 to 7:
 *   GET  /fruits -> SELECT every row and turn the ResultSet into a table
 *   POST /fruits -> INSERT a row with a PreparedStatement (the ? marks are filled in safely)
 */
@WebServlet("/fruits")
public class FruitServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        StringBuilder rows = new StringBuilder();
        try (Connection con = Db.connect();
             PreparedStatement ps = con.prepareStatement("SELECT id, name, price FROM fruit ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {                                  // move to the next row; false when none are left
                rows.append("<tr><td>%d</td><td>%s</td><td class='num'>₦%d</td></tr>".formatted(
                        rs.getInt("id"), Db.escape(rs.getString("name")), rs.getInt("price")));
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Db.page("Fruits in the database", """
                <div class="table-wrap"><table>
                  <thead><tr><th>id</th><th>name</th><th class="num">price</th></tr></thead>
                  <tbody>%s</tbody>
                </table></div>
                <h2>Add a fruit</h2>
                <form class="card" method="post" action="fruits">
                  <label>Name <input name="name" maxlength="40" required></label>
                  <label>Price (₦) <input name="price" type="number" min="0" required></label>
                  <button class="btn">Insert row</button>
                </form>
                <p class="note">Try a name like <code>Kiwi'); DROP TABLE fruit; --</code>. Thanks to the
                   PreparedStatement it is saved as plain text, and the table is safe (topic 13 explains why).</p>
                """.formatted(rows)));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        int price;
        try {
            price = Integer.parseInt(request.getParameter("price"));
        } catch (NumberFormatException e) {
            response.sendError(400, "The price must be a whole number.");
            return;
        }
        if (name == null || name.isBlank() || name.length() > 40 || price < 0) {
            response.sendError(400, "Give a name (up to 40 letters) and a price of 0 or more.");
            return;
        }

        String sql = "INSERT INTO fruit (name, price) VALUES (?, ?)";      // ? = a value comes later
        try (Connection con = Db.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());                                   // the 1st ?
            ps.setInt(2, price);                                            // the 2nd ?
            int added = ps.executeUpdate();                                 // how many rows changed
            System.out.println("[FRUIT] inserted " + added + " row");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        response.sendRedirect("fruits");
    }
}
