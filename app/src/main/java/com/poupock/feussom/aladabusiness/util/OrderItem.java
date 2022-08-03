package com.poupock.feussom.aladabusiness.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items")
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int menu_item_id;
    private double price;
    private String created_at;
    private int quantity;
    private int course_id;
    @Ignore
    private Course course;
    @Ignore
    private MenuItem menuItem;

    public OrderItem(int id, int menu_item_id, double price, String created_at, int quantity, int course_id) {
        this.id = id;
        this.menu_item_id = menu_item_id;
        this.price = price;
        this.created_at = created_at;
        this.quantity = quantity;
        this.course_id = course_id;
    }

    @Ignore
    public OrderItem(int id, int menu_item_id, double price, String created_at, int quantity, int course_id, Course course, MenuItem menuItem) {
        this.id = id;
        this.menu_item_id = menu_item_id;
        this.price = price;
        this.created_at = created_at;
        this.quantity = quantity;
        this.course_id = course_id;
        this.course = course;
        this.menuItem = menuItem;
    }

    @Ignore
    public OrderItem(int id) {
        this.id = id;
    }

    @Ignore
    public OrderItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenu_item_id() {
        return menu_item_id;
    }

    public void setMenu_item_id(int menu_item_id) {
        this.menu_item_id = menu_item_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}
