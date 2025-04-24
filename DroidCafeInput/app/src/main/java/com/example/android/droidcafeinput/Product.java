package com.example.android.droidcafeinput;

public class Product {
    private final String description;
    private String name;
    private int imageResId;
    private int count = 1;
    private double price;

    public Product(String name, String description, int imageResId, double price) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
    public int getImageResId() { return imageResId; }

    public int getCount() { return count; }
    public void incrementCount() { count++; }
    public void decrementCount() { if (count > 0) count--; }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
