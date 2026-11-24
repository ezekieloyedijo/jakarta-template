package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The shop shelf: GET /shop shows every product with an "Add to cart" form.
 * It does NOT need the session: the products are the same for everyone.
 */
@WebServlet("/shop")
public class ShopServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String base = request.getContextPath();
        StringBuilder rows = new StringBuilder();
        for (Product p : Catalog.all()) {
            rows.append("""
                    <tr>
                      <td>%s</td><td class="num">%s</td>
                      <td class="actions">
                        <form class="inline-form" action="%s/cart/add" method="post">
                          <input type="hidden" name="code" value="%s">
                          <input type="number" name="quantity" value="1" min="1" max="%d" aria-label="Quantity">
                          <button class="btn small" type="submit">Add to cart</button>
                        </form>
                      </td>
                    </tr>
                    """.formatted(Page.escape(p.getName()), Catalog.money(p.getPrice()), base,
                    p.getCode(), Cart.MAX_QUANTITY));
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "Products", """
                <div class="table-wrap"><table>
                  <thead><tr><th>Product</th><th class="num">Price</th><th></th></tr></thead>
                  <tbody>%s</tbody>
                </table></div>
                """.formatted(rows)));
    }
}
