package com.fashionstore.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.fashionstore.db.DatabaseManager;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.Product;

public class ClientDashboardForm extends JFrame {
    private final JPanel productCardsPanel;
    private final JTextField searchField;
    private final Map<Integer, CartItem> cart = new LinkedHashMap<>();
    private final String username;
    private final JButton cartButton;

    public ClientDashboardForm(String username) {
        this.username = username;
        setTitle("Fashion Store - Client Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color topColor = new Color(255, 255, 255);
                Color bottomColor = new Color(243, 246, 249);
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
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.X_AXIS));

        JLabel logoLabel = new JLabel("Fashion Store");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(new Color(33, 47, 61));
        brandPanel.add(logoLabel);

        JLabel subtitleLabel = new JLabel("  •  Client Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(115, 127, 140));
        brandPanel.add(subtitleLabel);

        headerPanel.add(brandPanel, BorderLayout.WEST);

        JPanel headerRight = new JPanel();
        headerRight.setOpaque(false);
        headerRight.setLayout(new BoxLayout(headerRight, BoxLayout.X_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(72, 85, 99));
        headerRight.add(Box.createHorizontalGlue());
        headerRight.add(welcomeLabel);
        headerRight.add(Box.createHorizontalStrut(12));

        cartButton = new JButton("View Cart (0)");
        cartButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cartButton.setBackground(new Color(42, 167, 134));
        cartButton.setForeground(Color.WHITE);
        cartButton.setFocusPainted(false);
        cartButton.setPreferredSize(new Dimension(140, 36));
        cartButton.addActionListener(e -> showCartDialog());
        headerRight.add(cartButton);
        headerRight.add(Box.createHorizontalStrut(12));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 36));
        logoutButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
            dispose();
        });
        headerRight.add(logoutButton);

        headerPanel.add(headerRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(248, 250, 252));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setBorder(new EmptyBorder(16, 20, 18, 20));
        controlPanel.setPreferredSize(new Dimension(0, 80));

        JPanel searchWrapper = new JPanel(new BorderLayout(10, 0));
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 216, 220), 1),
                new EmptyBorder(6, 12, 6, 12)));
        searchWrapper.setMaximumSize(new Dimension(550, 42));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchWrapper.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setBackground(new Color(41, 128, 185));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(100, 34));
        searchButton.addActionListener(e -> refreshProducts(searchField.getText().trim()));
        searchWrapper.add(searchButton, BorderLayout.EAST);

        controlPanel.add(searchWrapper);
        controlPanel.add(Box.createHorizontalGlue());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(110, 38));
        refreshButton.addActionListener(e -> refreshProducts(""));
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.SOUTH);

        productCardsPanel = new JPanel();
        productCardsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));
        productCardsPanel.setBackground(new Color(250, 251, 253));
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
            JLabel emptyLabel = new JLabel("No products available. Please refresh or add new items later.");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(117, 117, 117));
            emptyLabel.setBorder(new EmptyBorder(50, 20, 20, 20));
            productCardsPanel.add(emptyLabel);
        } else {
            for (Product product : products) {
                productCardsPanel.add(createProductCard(product));
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
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2d.setColor(new Color(220, 226, 231));
                g2d.setStroke(new java.awt.BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.setPreferredSize(new Dimension(260, 460));
        card.setMaximumSize(new Dimension(260, 460));

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(220, 220));
        imageLabel.setMaximumSize(new Dimension(220, 220));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(248, 249, 250));

        try {
            String imageName = product.getImageUrl();
            if (imageName == null || imageName.isEmpty()) {
                imageName = "default.jpg";
            }
            java.net.URL imageUrl = getClass().getResource("/images/" + imageName);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                Image scaledImage = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setText("No Image");
                imageLabel.setForeground(new Color(148, 148, 148));
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
        } catch (Exception e) {
            imageLabel.setText("No Image");
            imageLabel.setForeground(new Color(148, 148, 148));
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        card.add(imageLabel);
        card.add(Box.createVerticalStrut(12));

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(34, 47, 62));
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(6));

        JLabel categoryLabel = new JLabel(product.getCategory());
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(118, 129, 141));
        categoryLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        card.add(categoryLabel);
        card.add(Box.createVerticalStrut(10));

        JLabel priceLabel = new JLabel("₦" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(39, 174, 96));
        priceLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(4));

        JLabel stockLabel = new JLabel("Stock: " + product.getQuantity());
        stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stockLabel.setForeground(new Color(134, 148, 160));
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(stockLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(Box.createVerticalGlue());

        JButton addToCartButton = new JButton(product.getQuantity() > 0 ? "Add to Cart" : "Out of Stock");
        addToCartButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addToCartButton.setBackground(product.getQuantity() > 0 ? new Color(42, 167, 134) : new Color(180, 180, 180));
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setFocusPainted(false);
        addToCartButton.setPreferredSize(new Dimension(220, 40));
        addToCartButton.setMaximumSize(new Dimension(220, 40));
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCartButton.setEnabled(product.getQuantity() > 0);
        addToCartButton.addActionListener(e -> {
            addToCart(product);
            JOptionPane.showMessageDialog(this, "Added to cart: " + product.getName(), "Cart Updated", JOptionPane.INFORMATION_MESSAGE);
        });
        card.add(addToCartButton);

        return card;
    }

    private void addToCart(Product product) {
        CartItem existing = cart.get(product.getId());
        int quantityToAdd = 1;
        if (existing != null) {
            quantityToAdd = existing.getQuantity() + 1;
        }

        Product current = DatabaseManager.getInstance().getAllProducts().stream()
                .filter(p -> p.getId() == product.getId())
                .findFirst()
                .orElse(product);

        if (quantityToAdd > current.getQuantity()) {
            JOptionPane.showMessageDialog(this, "Cannot add more than available stock.", "Stock Limit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cart.put(product.getId(), new CartItem(product, quantityToAdd));
        updateCartButtonText();
    }

    private void updateCartButtonText() {
        int itemCount = cart.values().stream().mapToInt(CartItem::getQuantity).sum();
        cartButton.setText("View Cart (" + itemCount + ")");
    }

    private void showCartDialog() {
        CheckoutDialog checkoutDialog = new CheckoutDialog(this, cart,
                () -> completePurchase());
        checkoutDialog.setVisible(true);
    }

    private double calculateTotal() {
        return cart.values().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    void completePurchase() {
        for (CartItem item : cart.values()) {
            Product current = DatabaseManager.getInstance().getAllProducts().stream()
                    .filter(p -> p.getId() == item.getProduct().getId())
                    .findFirst()
                    .orElse(item.getProduct());
            int remaining = Math.max(0, current.getQuantity() - item.getQuantity());
            DatabaseManager.getInstance().updateProduct(new Product(current.getId(), current.getName(), current.getCategory(), current.getPrice(), remaining, current.getImageUrl()));
        }

        cart.clear();
        updateCartButtonText();
        refreshProducts("");
    }
}
