package com.example.foodex;// CartItemAdapter.java

import java.io.Serializable;

public class CartItem implements Serializable {
    private String itemName;
    private String itemPrice;
    private String make;
    private String workType;
    private String username;
    private int quantity;

    // Default, no-argument constructor required for Firestore deserialization
    public CartItem() {
        // Default constructor is needed for Firestore to deserialize objects
    }

    // Constructor with parameters
    public CartItem(String itemName, String itemPrice, String make, String workType, String username, int quantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.make = make;
        this.workType = workType;
        this.username = username;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    // Getter method for itemPrice
    public String getItemPrice() {
        return itemPrice;
    }

    // Getter method for make
    public String getMake() {
        return make;
    }

    // Getter method for workType
    public String getWorkType() {
        return workType;
    }

    // Getter method for username
    public String getUsername() {
        return username;
    }

    public int getQuantity() {
        return quantity;
    }
}
