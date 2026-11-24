package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** DELETE: POST /cart/remove  (code=UNI-01) takes one product out of the cart. */
@WebServlet("/cart/remove")
public class RemoveFromCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cart cart = Cart.of(request.getSession());
        if (!cart.remove(request.getParameter("code"))) {
            AddToCartServlet.sendError(request, response, "That product is not in your cart.");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/cart?msg=removed");
    }
}
