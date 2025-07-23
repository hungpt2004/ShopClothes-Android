package com.example.shopclothes_android;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Order implements Parcelable {
    private int id;
    private String userId;
    private List<Product> products;
    private double totalPrice;
    private String createdDate;
    private String status;

    // Constructor for creating new orders (without id)
    public Order(String userId, List<Product> products, double totalPrice, String createdDate, String status) {
        this.userId = userId;
        this.products = products;
        this.totalPrice = totalPrice;
        this.createdDate = createdDate;
        this.status = status;
    }

    // Constructor for loading orders from database (with id)
    public Order(int id, String userId, List<Product> products, double totalPrice, String createdDate, String status) {
        this.userId = userId;
        this.products = products;
        this.totalPrice = totalPrice;
        this.createdDate = createdDate;
        this.status = status;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public List<Product> getProducts() { return products; }
    public double getTotalPrice() { return totalPrice; }
    public String getCreatedDate() { return createdDate; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    protected Order(Parcel in) {
        id = in.readInt();
        userId = in.readString();
        products = in.createTypedArrayList(Product.CREATOR);
        totalPrice = in.readDouble();
        createdDate = in.readString();
        status = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(userId);
        dest.writeTypedList(products);
        dest.writeDouble(totalPrice);
        dest.writeString(createdDate);
        dest.writeString(status);
    }
}
