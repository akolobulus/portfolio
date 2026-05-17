package com.fashionstore.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.Product;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:fashion_store.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found.", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (product_id INTEGER PRIMARY KEY AUTOINCREMENT, product_name TEXT NOT NULL, category TEXT NOT NULL, price REAL NOT NULL, quantity INTEGER NOT NULL, image_url TEXT)");
            insertDefaultUser(conn);
            insertDefaultProducts(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void insertDefaultUser(Connection conn) throws SQLException {
        String sql = "INSERT OR IGNORE INTO users(username, password) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, "password123");
            ps.executeUpdate();
        }
    }

    private void insertDefaultProducts(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM products";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String sql = "INSERT INTO products(product_name, category, price, quantity, image_url) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Product 1: Blue T-Shirt
            ps.setString(1, "Royal Blue T-Shirt");
            ps.setString(2, "Shirts");
            ps.setDouble(3, 29.99);
            ps.setInt(4, 50);
            ps.setString(5, "shirt.jpg");
            ps.executeUpdate();

            // Product 2: Designer Jeans
            ps.setString(1, "Jackson Reno Jeans");
            ps.setString(2, "Jeans");
            ps.setDouble(3, 89.99);
            ps.setInt(4, 35);
            ps.setString(5, "jeans.jpg");
            ps.executeUpdate();

            // Product 3: Classic Heels
            ps.setString(1, "Classic Heels");
            ps.setString(2, "Accessories");
            ps.setDouble(3, 49.99);
            ps.setInt(4, 25);
            ps.setString(5, "heels.jpg");
            ps.executeUpdate();
        }
    }

    public boolean checkUserCredentials(String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to verify credentials", e);
        }
    }

    public void addProduct(Product product) {
        String sql = "INSERT INTO products(product_name, category, price, quantity, image_url) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantity());
            ps.setString(5, product.getImageUrl());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add product", e);
        }
    }

    public void updateProduct(Product product) {
        String sql = "UPDATE products SET product_name = ?, category = ?, price = ?, quantity = ?, image_url = ? WHERE product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantity());
            ps.setString(5, product.getImageUrl());
            ps.setInt(6, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_id, product_name, category, price, quantity, image_url FROM products ORDER BY product_id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("image_url")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read products", e);
        }
        return products;
    }

    public List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_id, product_name, category, price, quantity, image_url FROM products WHERE product_name LIKE ? OR category LIKE ? ORDER BY product_id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String wildcard = "%" + query + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("image_url")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search products", e);
        }
        return products;
    }
}
