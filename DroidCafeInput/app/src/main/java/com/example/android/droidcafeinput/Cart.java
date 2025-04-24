package com.example.android.droidcafeinput;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    public static List<Product> items = new ArrayList<>();

    public static void addItem(Product item) {
        for (Product p : items) {
            if (p.getName().equals(item.getName())) {
                p.incrementCount();
                return;
            }
        }
        items.add(item);
    }

    public static List<Product> getItems() {
        return items;
    }

    public static void clearCart() {
        items.clear();
    }

}
