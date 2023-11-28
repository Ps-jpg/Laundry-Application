package com.example.foodex;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String deliveryAddress;
    private String pickupTime;
    private String dropTime;
    private String specialInstructions;
    private String totalAmount;
    private String status;
    private Date timestamp;
    public String username;
    private List<CartActivity.CartItem> cartItems;



    // Default constructor required for Firestore
    public Order() {
        // Default constructor is necessary for Firestore
    }

    // Constructor with parameters
    public Order(String orderId, String username, String deliveryAddress, String pickupTime, String dropTime,
                 String specialInstructions, String totalAmount, String status, List<CartActivity.CartItem> cartItems) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.pickupTime = pickupTime;
        this.dropTime = dropTime;
        this.specialInstructions = specialInstructions;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timestamp = new Date();
        this.username = username;
        this.cartItems = cartItems;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getDropTime() {
        return dropTime;
    }

    public void setDropTime(String dropTime) {
        this.dropTime = dropTime;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // Add getters and setters for the new timestamp field
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    // Getter for cartItems
    public List<CartActivity.CartItem> getCartItems() {
        return cartItems;
    }

    // Setter for cartItems
    public void setCartItems(List<CartActivity.CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
