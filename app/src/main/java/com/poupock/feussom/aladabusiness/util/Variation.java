package com.poupock.feussom.aladabusiness.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "variations")
public class Variation {
    @PrimaryKey(autoGenerate = true) int id;
    int product_id;
    int user_id;
    double price_adjustment;
    String size;
    String color;
    @Ignore private double itemPrice;

    @Ignore
    public Variation(int id) {
        this.id = id;
    }

    @Ignore
    public Variation() {
    }

    public Variation(int id, int product_id, int user_id, double price_adjustment, String size, String color) {
        this.id = id;
        this.product_id = product_id;
        this.user_id = user_id;
        this.price_adjustment = price_adjustment;
        this.size = size;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getPrice_adjustment() {
        return price_adjustment;
    }

    public void setPrice_adjustment(double price_adjustment) {
        this.price_adjustment = price_adjustment;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
