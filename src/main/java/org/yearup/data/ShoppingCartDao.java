package org.yearup.data;

import org.springframework.web.bind.annotation.RequestParam;
import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int id);
    // add additional method signatures here
    void addProductToCart(int userId, int productId);
    void deleteProductFromCart(int userId);
    void updateCart(int userId, int quantity, int productId);
}
