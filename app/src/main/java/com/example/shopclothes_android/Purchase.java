package com.example.shopclothes_android;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Purchase implements Serializable {
    private String purchaseId;
    private Date purchaseDate;
    private List<Product> items;
    private double subtotal;
    private double shippingFee;
    private double total;
    private String userEmail;

    public Purchase(String purchaseId, List<Product> items, double subtotal, double shippingFee, double total,
            String userEmail) {
        this.purchaseId = purchaseId;
        this.purchaseDate = new Date();
        this.items = items;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.total = total;
        this.userEmail = userEmail;
    }

    // Getters
    public String getPurchaseId() {
        return purchaseId;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public List<Product> getItems() {
        return items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public double getTotal() {
        return total;
    }

    public String getUserEmail() {
        return userEmail;
    }

    // Setters
    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}