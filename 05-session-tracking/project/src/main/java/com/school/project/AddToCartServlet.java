package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * CREATE: POST /cart/add  (code=UNI-01, quantity=2)
 * Checks the input, puts the product in this customer's cart, then redirects to the cart.
 */
@WebServlet("/cart/add")
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product product = Catalog.find(request.getParameter("code"));
        Integer quantity = Cart.parseQuantity(request.getParameter("quantity"));

        if (product == null || quantity == null) {
            sendError(request, response, "Choose a real product and a quantity from 1 to " + Cart.MAX_QUANTITY + ".");
            return;
        }

        Cart cart = Cart.of(request.getSession());          // the session is created here if needed
        if (!cart.add(product.getCode(), quantity)) {
            sendError(request, response, "You can buy at most " + Cart.MAX_QUANTITY + " of one product.");
            return;
        }

        // Post/Redirect/Get: refreshing the cart page will not add the product again
        response.sendRedirect(request.getContextPath() + "/cart?msg=added");
    }

    static void sendError(HttpServletRequest request, HttpServletResponse response, String text) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "Sorry",
                Page.error(text) + "<p><a href='" + request.getContextPath() + "/cart'>Back to my cart</a></p>"));
    }
}
