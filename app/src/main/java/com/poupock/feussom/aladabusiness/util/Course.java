package com.poupock.feussom.aladabusiness.util;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "courses")
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String created_at;
    private int user_id;
    @ColumnInfo(defaultValue = "0")
    private int guest_table_id;
    private int order_id;
    private String code;
    @Ignore private GuestTable guestTable;
    @Ignore private User creator;
    @Ignore private Order order;
    private int status;
    @Ignore
    private List<OrderItem> items;

    public Course(int id, String title, String created_at, int user_id, int guest_table_id, int order_id, int status, String code) {
        this.id = id;
        this.title = title;
        this.created_at = created_at;
        this.user_id = user_id;
        this.guest_table_id = guest_table_id;
        this.order_id = order_id;
        this.status = status;
        this.code = code;
    }

    @Ignore
    public Course(int id, String title, String created_at, int user_id, int guest_table_id, int order_id, int status, GuestTable guestTable, User creator, Order order,
                  List<OrderItem> items, String code) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.created_at = created_at;
        this.user_id = user_id;
        this.guest_table_id = guest_table_id;
        this.order_id = order_id;
        this.guestTable = guestTable;
        this.creator = creator;
        this.order = order;
        this.status = status;
        this.items = items;
    }

    @Ignore
    public Course(int id) {
        this.id = id;
    }

    @Ignore
    public Course() {
    }

    public static int getActiveCourseIndex(List<Course> courseList) {
        int index = 0;
        for(int i=0; i < courseList.size(); i++){
            if(courseList.get(i).getStatus() == Constant.STATUS_OPEN){
                index = (i);
            }
        }
        return index;
    }

    public static JSONArray buildJsonArray(List<Course> courseList) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < courseList.size() ; i++){
            array.put(courseList.get(i).getJSONObject());
        }

        return array;
    }

    public JSONObject getJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("title", this.getTitle());
            object.put("created_at", this.getCreated_at());
            object.put("order_id", this.getOrder_id());
            object.put("code", this.getCode());
            object.put("items", OrderItem.buildJsonArray(this.getOrderItems()));
        }catch (JSONException ex){

        }
        return object;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return items;
    }

    public void setOrderItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static List<Course> buildListFromObjects(List<Object> data) {
        List<Course> values = new ArrayList<>();
        if (data != null){
            for (int i=0 ; i<data.size(); i++){
                values.add(getObjectFromObject(data.get(i)));
            }
        }
        return values;
    }

    public static Course getObjectFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), Course.class);
    }
}
