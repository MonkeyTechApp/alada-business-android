package com.poupock.feussom.aladabusiness.util;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "order_items")
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("menu_id")
    private int menu_item_id;
    private double price;
    private String created_at;
    private int quantity;
    private int course_id;
    private long uploaded_at;
    private long updated_at;
    private String variation_id;
    @Ignore
    private Course course;
    @Ignore
    private MenuItem menuItem;

    public OrderItem(int id, int menu_item_id, double price, String created_at, int quantity, int course_id
        , long uploaded_at , long updated_at, String variation_id) {
        this.id = id;
        this.variation_id = variation_id;
        this.menu_item_id = menu_item_id;
        this.price = price;
        this.created_at = created_at;
        this.quantity = quantity;
        this.course_id = course_id;
        this.updated_at = updated_at;
        this.uploaded_at = uploaded_at;
    }

    @Ignore
    public OrderItem(int id, int menu_item_id, double price, String created_at, int quantity, int course_id
            , long uploaded_at , long updated_at) {
        this.id = id;
        this.menu_item_id = menu_item_id;
        this.price = price;
        this.created_at = created_at;
        this.quantity = quantity;
        this.course_id = course_id;
        this.updated_at = updated_at;
        this.uploaded_at = uploaded_at;
    }

    @Ignore
    public OrderItem(int id, int menu_item_id, double price, String created_at, int quantity, int course_id) {
        this.id = id;
        this.menu_item_id = menu_item_id;
        this.price = price;
        this.created_at = created_at;
        this.quantity = quantity;
        this.course_id = course_id;
    }

    @Ignore
    public OrderItem(int id, int menu_item_id, double price, String created_at, int quantity, int course_id, Course course, MenuItem menuItem
            , int uploaded_at , int updated_at) {
        this.id = id;
        this.menu_item_id = menu_item_id;
        this.price = price;
        this.created_at = created_at;
        this.quantity = quantity;
        this.course_id = course_id;
        this.course = course;
        this.menuItem = menuItem;
        this.updated_at = updated_at;
        this.uploaded_at = uploaded_at;
    }

    @Ignore
    public OrderItem(int id) {
        this.id = id;
    }

    @Ignore
    public OrderItem() {
    }

    public static JSONArray buildJsonArray(List<OrderItem> data) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < data.size() ; i++){
            array.put(data.get(i).getJSONObject());
        }

        return array;
    }

    private JSONObject getJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", this.getId());
            object.put("price", this.getPrice());
            object.put("quantity", this.getQuantity());
            object.put("menu_item_id", this.getMenu_item_id());
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

    public long getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(long uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public static List<OrderItem> buildListFromObjects(List<Object> data) {
        List<OrderItem> values = new ArrayList<>();
        if (data != null){
            for (int i=0 ; i<data.size(); i++){
                values.add(getObjectFromObject(data.get(i)));
            }
        }
        return values;
    }

    public static OrderItem getObjectFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), OrderItem.class);
    }

    public String getVariation_id() {
        return variation_id;
    }

    public void setVariation_id(String variation_id) {
        this.variation_id = variation_id;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
