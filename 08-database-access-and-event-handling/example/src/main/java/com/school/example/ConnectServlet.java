package com.school.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/** STEP 4: open a connection and ask the database about itself. */
@WebServlet("/connect")
public class ConnectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // try-with-resources: the connection is CLOSED automatically at the end of the block
        try (Connection con = Db.connect()) {
            DatabaseMetaData info = con.getMetaData();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println(Db.page("Connected!", """
                    <div class="table-wrap"><table>
                      <tr><th>Database</th><td>%s %s</td></tr>
                      <tr><th>JDBC driver</th><td>%s</td></tr>
                      <tr><th>URL</th><td><code>%s</code></td></tr>
                    </table></div>
                    """.formatted(info.getDatabaseProductName(), info.getDatabaseProductVersion(),
                    info.getDriverName(), Db.URL)));
        } catch (SQLException e) {
            throw new ServletException("Could not connect to the database", e);   // shows a 500 page
        }
    }
}
