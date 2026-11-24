package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** UPDATE: POST /cart/update  (code=UNI-01, quantity=3) sets a new quantity for one product. */
@WebServlet("/cart/update")
public class UpdateCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer quantity = Cart.parseQuantity(request.getParameter("quantity"));
        if (quantity == null) {
            AddToCartServlet.sendError(request, response,
                    "The quantity must be a whole number from 1 to " + Cart.MAX_QUANTITY + ".");
            return;
        }

        Cart cart = Cart.of(request.getSession());
        if (!cart.update(request.getParameter("code"), quantity)) {
            AddToCartServlet.sendError(request, response, "That product is not in your cart.");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/cart?msg=updated");
    }
}
