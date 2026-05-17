package com.fashionstore.model;

public class Product {
    private final int id;
    private final String name;
    private final String category;
    private final double price;
    private final int quantity;
    private final String imageUrl;

    public Product(int id, String name, String category, double price, int quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public Product(String name, String category, double price, int quantity, String imageUrl) {
        this(0, name, category, price, quantity, imageUrl);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
