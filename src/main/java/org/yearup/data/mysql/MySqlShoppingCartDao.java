package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    private ProductDao productDao;
    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int id) {
        ShoppingCart shoppingCart = new ShoppingCart();
        String q = "SELECT * FROM shopping_cart WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(q);
        ) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    Product product = productDao.getById(rs.getInt("product_id"));
                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    shoppingCartItem.setProduct(product);
                    shoppingCartItem.setQuantity(rs.getInt("quantity"));
                    shoppingCart.add(shoppingCartItem);
                }
            }
            return shoppingCart;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addProductToCart(int userId, int productId) {
        ShoppingCart shoppingCart = getByUserId(userId);
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?) ";
        String update = "UPDATE shopping_cart SET quantity = ? WHERE product_id = ? AND user_id = ?";
        if(shoppingCart.contains(productId)){
            sql = update;
        }
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            if (shoppingCart.contains(productId)){
                ps.setInt(1,((shoppingCart.get(productId).getQuantity()) + 1));
                ps.setInt(2, productId);
                ps.setInt(3,userId);
            }else {
                ps.setInt(1, userId);
                ps.setInt(2, productId);
                ps.setInt(3, 1);
            }
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteProductFromCart(int userId) {
        String q = "DELETE FROM shopping_cart WHERE user_id = ?";
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(q);
        ){
            ps.setInt(1, userId);
            int row = ps.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCart(int userId, int quantity, int productId) {
        String q = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(q)
        ){
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);

            int row = ps.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
