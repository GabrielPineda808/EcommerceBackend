package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery();
        ){
            while (rs.next()){
                //int categoryId, String name, String description
                Category category = new Category(rs.getInt("category_id"), rs.getString("name"), rs.getString("description"));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        String query = "SELECT * FROM categories " +
                       "WHERE category_id = ?";
        try (Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1,categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return new Category(rs.getInt("category_id"), rs.getString("name"), rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String sql = "INSERT INTO categories(name, description) VALUES (?,?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ){
            ps.setString(1,category.getName());
            ps.setString(2,category.getDescription());
            int row = ps.executeUpdate();
            if (row > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = ps.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int orderId = generatedKeys.getInt(1);

                    // get the newly inserted category
                    return getById(orderId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String q = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
        try(Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(q);
        ){
            ps.setInt(3,categoryId);
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
