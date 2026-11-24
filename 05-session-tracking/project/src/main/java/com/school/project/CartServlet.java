package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * READ: GET /cart shows THIS customer's cart, taken from their session.
 * Each row has a quantity form (update) and a Remove button (delete).
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cart cart = Cart.of(request.getSession());
        String base = request.getContextPath();

        String content;
        if (cart.lines().isEmpty()) {
            content = "<p class='empty'>Your cart is empty. <a href='%s/shop'>Go shopping</a>.</p>".formatted(base);
        } else {
            StringBuilder rows = new StringBuilder();
            for (Cart.Line line : cart.lines()) {
                Product p = line.product();
                rows.append("""
                        <tr>
                          <td>%s</td>
                          <td class="num">%s</td>
                          <td>
                            <form class="inline-form" action="%s/cart/update" method="post">
                              <input type="hidden" name="code" value="%s">
                              <input type="number" name="quantity" value="%d" min="1" max="%d" aria-label="Quantity">
                              <button class="btn small secondary" type="submit">Update</button>
                            </form>
                          </td>
                          <td class="num">%s</td>
                          <td class="actions">
                            <form action="%s/cart/remove" method="post">
                              <input type="hidden" name="code" value="%s">
                              <button class="btn small danger" type="submit">Remove</button>
                            </form>
                          </td>
                        </tr>
                        """.formatted(Page.escape(p.getName()), Catalog.money(p.getPrice()), base, p.getCode(),
                        line.quantity(), Cart.MAX_QUANTITY, Catalog.money(line.subtotal()), base, p.getCode()));
            }
            content = """
                    <div class="table-wrap"><table>
                      <thead><tr><th>Product</th><th class="num">Price</th><th>Quantity</th>
                                 <th class="num">Subtotal</th><th></th></tr></thead>
                      <tbody>%s</tbody>
                      <tfoot><tr><td colspan="3">Total (%d items)</td><td class="num">%s</td><td></td></tr></tfoot>
                    </table></div>
                    <div class="buttons" style="margin-top:16px">
                      <a class="btn" href="%s/shop">Continue shopping</a>
                      <form action="%s/cart/clear" method="post" onsubmit="return confirm('Empty the whole cart?')">
                        <button class="btn danger" type="submit">Empty cart</button>
                      </form>
                    </div>
                    """.formatted(rows, cart.count(), Catalog.money(cart.total()), base, base);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "My cart",
                Page.message(request.getParameter("msg")) + content));
    }
}
