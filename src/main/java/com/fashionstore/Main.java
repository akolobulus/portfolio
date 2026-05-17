package com.fashionstore;

import javax.swing.SwingUtilities;

import com.fashionstore.db.DatabaseManager;
import com.fashionstore.ui.LoginForm;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager db = DatabaseManager.getInstance();
            db.initializeDatabase();
            new LoginForm().setVisible(true);
        });
    }
}
