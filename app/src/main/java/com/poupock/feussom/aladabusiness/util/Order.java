package com.poupock.feussom.aladabusiness.util;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String code;
    private int user_id;
    private int internal_point_id;
    private int status;
    @ColumnInfo(defaultValue =  "1")
    private int payment_method_id ;
    @ColumnInfo(defaultValue = "0")
    private int guest_table_id;
    @Ignore private GuestTable guestTable;
    @Ignore InternalPoint internalPoint;
    @Ignore private User creator;
    private String created_at;
    @Ignore private List<Course> courses;


    public Order(int id, String code, int user_id, int internal_point_id, int guest_table_id, int status, int payment_method_id,
                 String created_at) {
        this.id = id;
        this.payment_method_id = payment_method_id;
        this.created_at = created_at;
        this.code = code;
        this.user_id = user_id;
        this.status = status;
        this.internal_point_id = internal_point_id;
        this.guest_table_id = guest_table_id;
    }

    @Ignore
    public Order(int id, String code, int user_id, int internal_point_id, int guest_table_id, GuestTable guestTable,
                 int status, int payment_method_id,
                 InternalPoint internalPoint, User creator, List<Course> courses, String created_at) {
        this.id = id;
        this.created_at = created_at;
        this.payment_method_id = payment_method_id;
        this.code = code;
        this.user_id = user_id;
        this.internal_point_id = internal_point_id;
        this.guest_table_id = guest_table_id;
        this.guestTable = guestTable;
        this.internalPoint = internalPoint;
        this.status = status;
        this.creator = creator;
        this.courses = courses;
    }

    @Ignore
    public Order(int id) {
        this.id = id;
    }

    @Ignore
    public Order() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public List<Course> getCourseList() {
        return courses;
    }

    public void setCourseList(List<Course> courses) {
        this.courses = courses;
    }

    public int getInternal_point_id() {
        return internal_point_id;
    }

    public void setInternal_point_id(int internal_point_id) {
        this.internal_point_id = internal_point_id;
    }

    public int getGuest_table_id() {
        return guest_table_id;
    }

    public void setGuest_table_id(int guest_table_id) {
        this.guest_table_id = guest_table_id;
    }

    public GuestTable getGuestTable() {
        return guestTable;
    }

    public void setGuestTable(GuestTable guestTable) {
        this.guestTable = guestTable;
    }

    public InternalPoint getInternalPoint() {
        return internalPoint;
    }

    public void setInternalPoint(InternalPoint internalPoint) {
        this.internalPoint = internalPoint;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTotal() {
        double total = 0;
        if(this.getCourseList() != null){
            for (int i = 0 ; i < this.getCourseList().size(); i++){
                if(this.getCourseList().get(i).getOrderItems() != null){
                    for (int j = 0 ; j < this.getCourseList().get(i).getOrderItems().size(); j++){
                        total = total + (this.getCourseList().get(i).getOrderItems().get(j).getQuantity() *
                            this.getCourseList().get(i).getOrderItems().get(j).getPrice());
                    }
                }
            }
        }
        return total;
    }

    public static List<Order> buildListFromObjects(List<Object> data) {
        List<Order> values = new ArrayList<>();
        if (data != null){
            for (int i=0 ; i<data.size(); i++){
                values.add(getObjectFromObject(data.get(i)));
            }
        }
        return values;
    }

    public static Order getObjectFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), Order.class);
    }


    public JSONObject buildParams(int business_id) {
        JSONObject params = new JSONObject();
        try {
            params.put("status", this.getStatus()+"");
            params.put("code", this.getCode());
            params.put("ordered_at", this.getCreated_at());
            params.put("guest_table_id", this.guest_table_id+"");
            params.put("business_id", business_id+"");
            params.put("course", Course.buildJsonArray(this.getCourseList()));
        }catch (JSONException ex){

        }
        return params;
    }

    public int getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(int payment_method_id) {
        this.payment_method_id = payment_method_id;
    }
}
