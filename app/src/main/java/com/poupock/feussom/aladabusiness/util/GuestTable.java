package com.poupock.feussom.aladabusiness.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "guest_tables")
public class GuestTable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("name")
    private String title;
    private int internal_point_id;
    private int user_id;
    private int capacity;
    @Ignore private InternalPoint internalPoint;
    @Ignore private User creator;
    private String created_at;
    @Ignore private List<Order> orders;

    public GuestTable(int id, String title, int internal_point_id, int user_id, int capacity, String created_at) {
        this.id = id;
        this.title = title;
        this.created_at = created_at;
        this.internal_point_id = internal_point_id;
        this.user_id = user_id;
        this.capacity = capacity;
    }

    @Ignore
    public GuestTable(int id, String title, int internal_point_id, int user_id, int capacity, InternalPoint internalPoint,
                      User creator, String created_at) {
        this.id = id;
        this.created_at = created_at;
        this.title = title;
        this.internal_point_id = internal_point_id;
        this.user_id = user_id;
        this.capacity = capacity;
        this.internalPoint = internalPoint;
        this.creator = creator;
    }

    @Ignore
    public GuestTable(int id) {
        this.id = id;
    }

    @Ignore
    public GuestTable() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public InternalPoint getInternalPoint() {
        return internalPoint;
    }

    public void setInternalPoint(InternalPoint internalPoint) {
        this.internalPoint = internalPoint;
    }

    public int getInternal_point_id() {
        return internal_point_id;
    }

    public void setInternal_point_id(int internal_point_id) {
        this.internal_point_id = internal_point_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static GuestTable getFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), GuestTable.class);
    }

    public static List<GuestTable> buildListFromObjects(List<Object> data) {
        List<GuestTable> values = new ArrayList<>();
        if (data != null){
            for (int i=0 ; i<data.size(); i++){
                values.add(getFromObject(data.get(i)));
            }
        }
        return values;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
