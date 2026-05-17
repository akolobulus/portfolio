package com.fashionstore.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.fashionstore.model.CartItem;

public class CheckoutDialog extends JDialog {
    private final Map<Integer, CartItem> cart;
    private final ClientDashboardForm parent;
    private final Runnable onPaymentComplete;

    private final JPanel cards;
    private final CardState[] steps = {CardState.CART, CardState.SHIPPING, CardState.PAYMENT};
    private int stepIndex = 0;

    private JRadioButton standardShipping;
    private JRadioButton expressShipping;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField zipField;
    private JTextField cardNameField;
    private JTextField cardNumberField;
    private JTextField cardExpiryField;
    private JTextField cardCvvField;
    private JButton nextButton;
    private JButton backButton;
    private JLabel summaryLabel;

    public CheckoutDialog(ClientDashboardForm parent, Map<Integer, CartItem> cart, Runnable onPaymentComplete) {
        super(parent, "Checkout", true);
        this.parent = parent;
        this.cart = cart;
        this.onPaymentComplete = onPaymentComplete;

        setSize(920, 640);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 47, 88));
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Checkout");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        header.add(createStepper(), BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        cards = new JPanel(new java.awt.CardLayout());
        cards.add(createCartReviewPanel(), CardState.CART.name());
        cards.add(createShippingPanel(), CardState.SHIPPING.name());
        cards.add(createPaymentPanel(), CardState.PAYMENT.name());
        add(cards, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(14, 24, 14, 24));
        footer.setBackground(new Color(245, 247, 250));

        summaryLabel = new JLabel("Order total: ₦" + String.format("%.2f", calculateTotal()));
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryLabel.setForeground(new Color(33, 47, 62));
        footer.add(summaryLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backButton.setBackground(new Color(189, 195, 199));
        backButton.setForeground(new Color(33, 47, 62));
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(110, 40));
        backButton.addActionListener(e -> onBackClicked());
        buttonPanel.add(backButton);

        nextButton = new JButton("Continue");
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nextButton.setBackground(new Color(42, 167, 134));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setPreferredSize(new Dimension(170, 40));
        nextButton.addActionListener(this::onNextClicked);
        buttonPanel.add(nextButton);

        footer.add(buttonPanel, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);

        updateStep();
    }

    private JPanel createStepper() {
        JPanel stepper = new JPanel();
        stepper.setOpaque(false);
        stepper.setLayout(new GridLayout(1, steps.length, 16, 0));

        for (CardState step : steps) {
            JPanel item = new JPanel();
            item.setOpaque(false);
            item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1, true),
                    new EmptyBorder(10, 12, 10, 12)));
            JLabel label = new JLabel(step.title);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            item.add(label);
            stepper.add(item);
        }

        return stepper;
    }

    private JPanel createCartReviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 251, 253));

        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(Color.WHITE);
        orderPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(209, 213, 219)), new EmptyBorder(18, 18, 18, 18)));

        JLabel orderTitle = new JLabel("Your Order");
        orderTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        orderTitle.setForeground(new Color(33, 47, 62));
        orderPanel.add(orderTitle);
        orderPanel.add(Box.createVerticalStrut(16));

        for (CartItem item : cart.values()) {
            orderPanel.add(createOrderItemRow(item));
            orderPanel.add(Box.createVerticalStrut(12));
        }

        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setOpaque(false);
        summary.setBorder(new EmptyBorder(16, 0, 0, 0));
        summary.add(createSummaryRow("Subtotal", calculateTotal()));
        summary.add(createSummaryRow("Estimated Shipping", getShippingFee()));
        summary.add(createSummaryRow("Estimated Tax", calculateTotal() * 0.08));

        JLabel total = new JLabel("Total: ₦" + String.format("%.2f", calculateTotal() + getShippingFee() + calculateTotal() * 0.08));
        total.setFont(new Font("Segoe UI", Font.BOLD, 18));
        total.setForeground(new Color(33, 47, 62));
        summary.add(Box.createVerticalStrut(12));
        summary.add(total);

        orderPanel.add(summary);

        panel.add(orderPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createShippingPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 251, 253));

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setOpaque(false);

        JLabel sectionTitle = new JLabel("Shipping & Delivery");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(new Color(33, 47, 62));
        controls.add(sectionTitle);
        controls.add(Box.createVerticalStrut(16));

        standardShipping = new JRadioButton("Standard Ground (₦5.00)");
        expressShipping = new JRadioButton("Express (₦15.00)");
        standardShipping.setSelected(true);
        standardShipping.setBackground(new Color(250, 251, 253));
        expressShipping.setBackground(new Color(250, 251, 253));

        ButtonGroup group = new ButtonGroup();
        group.add(standardShipping);
        group.add(expressShipping);
        controls.add(standardShipping);
        controls.add(Box.createVerticalStrut(8));
        controls.add(expressShipping);
        controls.add(Box.createVerticalStrut(20));

        JLabel addressTitle = new JLabel("Shipping Address");
        addressTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addressTitle.setForeground(new Color(33, 47, 62));
        controls.add(addressTitle);
        controls.add(Box.createVerticalStrut(12));

        nameField = createTextField("Full Name");
        addressField = createTextField("Street Address");
        cityField = createTextField("City");
        stateField = createTextField("State");
        zipField = createTextField("ZIP Code");

        controls.add(nameField);
        controls.add(Box.createVerticalStrut(10));
        controls.add(addressField);
        controls.add(Box.createVerticalStrut(10));
        controls.add(cityField);
        controls.add(Box.createVerticalStrut(10));
        controls.add(stateField);
        controls.add(Box.createVerticalStrut(10));
        controls.add(zipField);

        panel.add(controls, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 251, 253));

        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);

        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setOpaque(false);

        JLabel paymentTitle = new JLabel("Payment Method");
        paymentTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        paymentTitle.setForeground(new Color(33, 47, 62));
        paymentPanel.add(paymentTitle);
        paymentPanel.add(Box.createVerticalStrut(16));

        cardNameField = createTextField("Cardholder Name");
        cardNumberField = createTextField("Card Number");
        cardExpiryField = createTextField("Expiration (MM/YY)");
        cardCvvField = createTextField("CVV");

        paymentPanel.add(cardNameField);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(cardNumberField);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(cardExpiryField);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(cardCvvField);

        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.Y_AXIS));
        addressPanel.setOpaque(false);

        JLabel billingTitle = new JLabel("Billing Address");
        billingTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        billingTitle.setForeground(new Color(33, 47, 62));
        addressPanel.add(billingTitle);
        addressPanel.add(Box.createVerticalStrut(16));

        JTextArea billingNote = new JTextArea("Payment will be processed securely.");
        billingNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        billingNote.setLineWrap(true);
        billingNote.setWrapStyleWord(true);
        billingNote.setOpaque(false);
        billingNote.setEditable(false);
        billingNote.setFocusable(false);
        billingNote.setBorder(null);
        addressPanel.add(billingNote);

        grid.add(paymentPanel);
        grid.add(addressPanel);

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private void onNextClicked(ActionEvent event) {
        if (stepIndex < steps.length - 1) {
            stepIndex++;
            updateStep();
            return;
        }

        if (!validateShipping()) {
            return;
        }
        if (!validatePayment()) {
            return;
        }

        onPaymentComplete.run();
        dispose();
    }

    private void onBackClicked() {
        if (stepIndex > 0) {
            stepIndex--;
            updateStep();
        }
    }

    private void updateStep() {
        CardState state = steps[stepIndex];
        ((java.awt.CardLayout) cards.getLayout()).show(cards, state.name());

        backButton.setVisible(stepIndex > 0);

        if (state == CardState.CART) {
            nextButton.setText("Continue to Shipping");
        } else if (state == CardState.SHIPPING) {
            nextButton.setText("Continue to Payment");
        } else {
            nextButton.setText("Place Order (₦" + String.format("%.2f", calculateTotal() + getShippingFee() + calculateTotal() * 0.08) + ")");
        }

        summaryLabel.setText("Order total: ₦" + String.format("%.2f", calculateTotal() + getShippingFee() + calculateTotal() * 0.08));
    }

    private boolean validateShipping() {
        if (nameField.getText().isBlank() || addressField.getText().isBlank() || cityField.getText().isBlank() || stateField.getText().isBlank() || zipField.getText().isBlank()) {
            showError("Please fill in all shipping address fields.");
            return false;
        }
        return true;
    }

    private boolean validatePayment() {
        if (cardNameField.getText().isBlank() || cardNumberField.getText().isBlank() || cardExpiryField.getText().isBlank() || cardCvvField.getText().isBlank()) {
            showError("Please fill in all payment fields.");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Missing Information", javax.swing.JOptionPane.WARNING_MESSAGE);
    }

    private JPanel createOrderItemRow(CartItem item) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel name = new JLabel(item.getProduct().getName() + "  x" + item.getQuantity());
        name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        name.setForeground(new Color(45, 58, 76));
        row.add(name, BorderLayout.WEST);

        JLabel price = new JLabel("₦" + String.format("%.2f", item.getProduct().getPrice() * item.getQuantity()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 14));
        price.setForeground(new Color(33, 47, 62));
        row.add(price, BorderLayout.EAST);

        return row;
    }

    private JPanel createSummaryRow(String labelText, double amount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(99, 110, 114));
        row.add(label, BorderLayout.WEST);

        JLabel amountLabel = new JLabel("₦" + String.format("%.2f", amount));
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amountLabel.setForeground(new Color(33, 47, 62));
        row.add(amountLabel, BorderLayout.EAST);
        return row;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 38));
        field.setBorder(BorderFactory.createLineBorder(new Color(207, 216, 220)));
        field.setToolTipText(placeholder);
        return field;
    }

    private double calculateTotal() {
        return cart.values().stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
    }

    private double getShippingFee() {
        return standardShipping != null && expressShipping != null && expressShipping.isSelected() ? 15.00 : 5.00;
    }

    private enum CardState {
        CART("Cart"), SHIPPING("Shipping"), PAYMENT("Payment");

        private final String title;

        CardState(String title) {
            this.title = title;
        }
    }
}
