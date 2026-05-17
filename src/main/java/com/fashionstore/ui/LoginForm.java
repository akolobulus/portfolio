package com.fashionstore.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.fashionstore.db.DatabaseManager;

public class LoginForm extends JFrame {
    private final JTextField emailField;
    private final JPasswordField passwordField;

    public LoginForm() {
        setTitle("Fashion Store - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        // Main background panel with light gray
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setLayout(null);

        // White card panel centered
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        cardPanel.setLayout(null);
        cardPanel.setOpaque(false);
        int cardWidth = 450;
        int cardHeight = 420;
        int cardX = (800 - cardWidth) / 2;
        int cardY = (900 - cardHeight) / 2;
        cardPanel.setBounds(cardX, cardY, cardWidth, cardHeight);

        // Login heading
        JLabel loginHeading = new JLabel("Login");
        loginHeading.setFont(new Font("Segoe UI", Font.BOLD, 36));
        loginHeading.setForeground(new Color(0, 0, 0));
        loginHeading.setHorizontalAlignment(JLabel.CENTER);
        loginHeading.setBounds(0, 30, cardWidth, 50);
        cardPanel.add(loginHeading);

        // Email address label
        JLabel emailLabel = new JLabel("Email address");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(80, 80, 80));
        emailLabel.setBounds(40, 100, 370, 20);
        cardPanel.add(emailLabel);

        // Email input field
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBounds(40, 125, 370, 45);
        emailField.setBorder(null);
        emailField.setOpaque(true);
        emailField.setBackground(new Color(245, 245, 250));
        emailField.setCaretColor(new Color(0, 102, 204));
        emailField.setMargin(new java.awt.Insets(8, 12, 8, 12));
        
        // Custom border painting for input field
        emailField.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintSafely(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintSafely(g);
            }
        });
        
        // Add placeholder functionality
        setPlaceholder(emailField, "Enter email");
        cardPanel.add(emailField);

        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(80, 80, 80));
        passwordLabel.setBounds(40, 185, 370, 20);
        cardPanel.add(passwordLabel);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBounds(40, 210, 370, 45);
        passwordField.setBorder(null);
        passwordField.setOpaque(true);
        passwordField.setBackground(new Color(245, 245, 250));
        passwordField.setCaretColor(new Color(0, 102, 204));
        passwordField.setMargin(new java.awt.Insets(8, 12, 8, 12));
        setPlaceholder(passwordField, "Password");
        cardPanel.add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Blue button background
                g2d.setColor(new Color(0, 102, 204));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Button text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String text = getText();
                java.awt.FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, x, y);
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loginButton.setBounds(40, 285, 370, 50);
        loginButton.addActionListener(e -> login());
        cardPanel.add(loginButton);

        mainPanel.add(cardPanel);
        getContentPane().add(mainPanel);
    }

    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(new Color(180, 180, 180));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(0, 0, 0));
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(180, 180, 180));
                }
            }
        });
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Check for placeholder text
        if (email.isEmpty() || email.equals("Enter email") || password.isEmpty() || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // For now, use email as username for authentication
        boolean authenticated = DatabaseManager.getInstance().checkUserCredentials(email, password);
        if (authenticated) {
            SwingUtilities.invokeLater(() -> {
                if ("admin".equalsIgnoreCase(email)) {
                    DashboardForm dashboard = new DashboardForm(email);
                    dashboard.setVisible(true);
                } else {
                    ClientDashboardForm clientDashboard = new ClientDashboardForm(email);
                    clientDashboard.setVisible(true);
                }
            });
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login failed. Check your email and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
