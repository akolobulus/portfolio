package com.fashionstore.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.fashionstore.db.DatabaseManager;
import com.fashionstore.model.Product;

public class DashboardForm extends JFrame {
    private final JPanel productCardsPanel;
    private final JTextField searchField;

    public DashboardForm(String username) {
        setTitle("Fashion Store - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Header with gradient background
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color topColor = new Color(41, 128, 185);
                Color bottomColor = new Color(52, 152, 219);
                int width = getWidth();
                int height = getHeight();

                for (int i = 0; i < height; i++) {
                    float ratio = (float) i / height;
                    int r = (int) (topColor.getRed() * (1 - ratio) + bottomColor.getRed() * ratio);
                    int g_val = (int) (topColor.getGreen() * (1 - ratio) + bottomColor.getGreen() * ratio);
                    int b = (int) (topColor.getBlue() * (1 - ratio) + bottomColor.getBlue() * ratio);
                    g2d.setColor(new Color(r, g_val, b));
                    g2d.drawLine(0, i, width, i);
                }
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 90));

        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.X_AXIS));
        brandPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel logoLabel = new JLabel("Fashion Store");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        brandPanel.add(logoLabel);

        JLabel subtitleLabel = new JLabel("  •  Unified Inventory Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(235, 245, 255));
        brandPanel.add(subtitleLabel);

        headerPanel.add(brandPanel, BorderLayout.WEST);

        JPanel headerRight = new JPanel();
        headerRight.setOpaque(false);
        headerRight.setLayout(new BoxLayout(headerRight, BoxLayout.X_AXIS));
        headerRight.setBorder(new EmptyBorder(10, 0, 10, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(Color.WHITE);
        headerRight.add(Box.createHorizontalGlue());
        headerRight.add(welcomeLabel);
        headerRight.add(Box.createHorizontalStrut(18));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 32));
        logoutButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
            dispose();
        });
        headerRight.add(logoutButton);

        headerPanel.add(headerRight, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Search and control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(249, 250, 252));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setBorder(new EmptyBorder(16, 20, 18, 20));
        controlPanel.setPreferredSize(new Dimension(0, 80));

        JPanel searchWrapper = new JPanel();
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        searchWrapper.setLayout(new BorderLayout(10, 0));
        searchWrapper.setMaximumSize(new Dimension(500, 40));

        JLabel searchLabel = new JLabel("Search Products");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchLabel.setForeground(new Color(99, 110, 114));
        searchWrapper.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(300, 28));
        searchWrapper.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setBackground(new Color(41, 128, 185));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(100, 32));
        searchButton.addActionListener(e -> refreshProducts(searchField.getText().trim()));
        searchWrapper.add(searchButton, BorderLayout.EAST);

        controlPanel.add(searchWrapper);
        controlPanel.add(Box.createHorizontalGlue());

        JButton addButton = new JButton("Add Product");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(130, 38));
        addButton.addActionListener(e -> showAddProductDialog());
        controlPanel.add(addButton);
        controlPanel.add(Box.createHorizontalStrut(12));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(110, 38));
        refreshButton.addActionListener(e -> refreshProducts(""));
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Product cards panel with wrap-style layout and smaller card widths
        productCardsPanel = new JPanel();
        productCardsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 20));
        productCardsPanel.setBackground(new Color(250, 250, 250));
        productCardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(productCardsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refreshProducts("");
    }

    private void refreshProducts(String searchQuery) {
        productCardsPanel.removeAll();

        List<Product> products = searchQuery.isEmpty()
            ? DatabaseManager.getInstance().getAllProducts()
            : DatabaseManager.getInstance().searchProducts(searchQuery);

        if (products.isEmpty()) {
            JLabel emptyLabel = new JLabel("No products found. Add some products to get started!");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(127, 140, 141));
            emptyLabel.setBorder(new EmptyBorder(50, 20, 20, 20));
            productCardsPanel.add(emptyLabel);
        } else {
            for (Product product : products) {
                JPanel cardPanel = createProductCard(product);
                productCardsPanel.add(cardPanel);
            }
        }

        productCardsPanel.revalidate();
        productCardsPanel.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new java.awt.BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setPreferredSize(new Dimension(250, 380));
        card.setMaximumSize(new Dimension(250, 380));

        // Product image (larger, square format)
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(180, 180));
        imageLabel.setMaximumSize(new Dimension(180, 180));
        imageLabel.setMinimumSize(new Dimension(180, 180));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(240, 240, 245));
        
        try {
            // Load from resources using product name or image URL
            String imageName = product.getImageUrl();
            if (imageName == null || imageName.isEmpty()) {
                imageName = product.getName().toLowerCase().replace(" ", "") + ".jpg";
            }
            
            // Try to load from resources first
            java.net.URL imageUrl = getClass().getResource("/images/" + imageName);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                Image scaledImage = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setText("No Image");
                imageLabel.setForeground(new Color(150, 150, 150));
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
        } catch (Exception e) {
            imageLabel.setText("Image Error");
            imageLabel.setForeground(new Color(150, 150, 150));
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        
        card.add(imageLabel);
        card.add(Box.createVerticalStrut(10));

        // Product name
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(nameLabel);
        
        card.add(Box.createVerticalStrut(5));

        // Product category
        JLabel categoryLabel = new JLabel(product.getCategory());
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        categoryLabel.setForeground(new Color(127, 140, 141));
        categoryLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        categoryLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(categoryLabel);
        
        card.add(Box.createVerticalStrut(8));

        // Product price
        JLabel priceLabel = new JLabel("₦" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(46, 204, 113));
        priceLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        priceLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(priceLabel);

        // Product stock
        JLabel quantityLabel = new JLabel("Stock: " + product.getQuantity());
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        quantityLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        quantityLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(quantityLabel);
        
        card.add(Box.createVerticalStrut(10));

        // Action buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(200, 35));

        JButton editButton = new JButton("Edit");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        editButton.setPreferredSize(new Dimension(85, 30));
        editButton.setMaximumSize(new Dimension(85, 30));
        editButton.setBackground(new Color(241, 196, 15));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> showEditProductDialog(product));
        buttonPanel.add(editButton);
        
        buttonPanel.add(Box.createHorizontalStrut(5));

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deleteButton.setPreferredSize(new Dimension(85, 30));
        deleteButton.setMaximumSize(new Dimension(85, 30));
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteProduct(product));
        buttonPanel.add(deleteButton);

        card.add(buttonPanel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setBounds(10, 10, 150, 20);
        panel.add(nameLabel);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 10, 260, 25);
        panel.add(nameField);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setBounds(10, 45, 150, 20);
        panel.add(categoryLabel);
        JTextField categoryField = new JTextField();
        categoryField.setBounds(150, 45, 260, 25);
        panel.add(categoryField);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(10, 80, 150, 20);
        panel.add(priceLabel);
        JTextField priceField = new JTextField();
        priceField.setBounds(150, 80, 260, 25);
        panel.add(priceField);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(10, 115, 150, 20);
        panel.add(quantityLabel);
        JTextField quantityField = new JTextField();
        quantityField.setBounds(150, 115, 260, 25);
        panel.add(quantityField);

        JLabel imageLabel = new JLabel("Image File:");
        imageLabel.setBounds(10, 150, 150, 20);
        panel.add(imageLabel);
        
        // Image selection dropdown
        String[] availableImages = {"dress.jpg", "dress2.jpg", "heels.jpg", "heels2.jpg", 
                                    "jeans.jpg", "jeans2.jpg", "sheo.jpg", "sheo2.jpg", 
                                    "shirt.jpg", "shirt2.jpg"};
        javax.swing.JComboBox<String> imageCombo = new javax.swing.JComboBox<>(availableImages);
        imageCombo.setBounds(150, 150, 260, 25);
        panel.add(imageCombo);

        JButton addButton = new JButton("Add Product");
        addButton.setBounds(150, 290, 260, 35);
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String priceText = priceField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String imageFile = (String) imageCombo.getSelectedItem();

            if (name.isEmpty() || category.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                int quantity = Integer.parseInt(quantityText);
                Product newProduct = new Product(name, category, price, quantity, imageFile);
                DatabaseManager.getInstance().addProduct(newProduct);
                JOptionPane.showMessageDialog(dialog, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshProducts("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price and quantity must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditProductDialog(Product product) {
        JDialog dialog = new JDialog(this, "Edit Product", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setBounds(10, 10, 150, 20);
        panel.add(nameLabel);
        JTextField nameField = new JTextField(product.getName());
        nameField.setBounds(150, 10, 260, 25);
        panel.add(nameField);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setBounds(10, 45, 150, 20);
        panel.add(categoryLabel);
        JTextField categoryField = new JTextField(product.getCategory());
        categoryField.setBounds(150, 45, 260, 25);
        panel.add(categoryField);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(10, 80, 150, 20);
        panel.add(priceLabel);
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        priceField.setBounds(150, 80, 260, 25);
        panel.add(priceField);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(10, 115, 150, 20);
        panel.add(quantityLabel);
        JTextField quantityField = new JTextField(String.valueOf(product.getQuantity()));
        quantityField.setBounds(150, 115, 260, 25);
        panel.add(quantityField);

        JLabel imageLabel = new JLabel("Image File:");
        imageLabel.setBounds(10, 150, 150, 20);
        panel.add(imageLabel);
        
        String[] availableImages = {"dress.jpg", "dress2.jpg", "heels.jpg", "heels2.jpg", 
                                    "jeans.jpg", "jeans2.jpg", "sheo.jpg", "sheo2.jpg", 
                                    "shirt.jpg", "shirt2.jpg"};
        javax.swing.JComboBox<String> imageCombo = new javax.swing.JComboBox<>(availableImages);
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            imageCombo.setSelectedItem(product.getImageUrl());
        }
        imageCombo.setBounds(150, 150, 260, 25);
        panel.add(imageCombo);

        JButton updateButton = new JButton("Update Product");
        updateButton.setBounds(150, 290, 260, 35);
        updateButton.setBackground(new Color(241, 196, 15));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String priceText = priceField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String imageFile = (String) imageCombo.getSelectedItem();

            if (name.isEmpty() || category.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                int quantity = Integer.parseInt(quantityText);
                Product updatedProduct = new Product(product.getId(), name, category, price, quantity, imageFile);
                DatabaseManager.getInstance().updateProduct(updatedProduct);
                JOptionPane.showMessageDialog(dialog, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshProducts("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price and quantity must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(updateButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteProduct(Product product) {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete \"" + product.getName() + "\"?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            DatabaseManager.getInstance().deleteProduct(product.getId());
            JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshProducts("");
        }
    }
}
