package com.poupock.feussom.aladabusiness.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity(tableName = "menu_items")
public class MenuItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String pic_local_path;
    private String pic_server_path;
    private int menu_item_category_id;
    private int user_id;
    private double price;
    private String created_at;
    @Ignore User creator;
    @Ignore private MenuItemCategory menuItemCategory;

    public MenuItem(int id, String title, String description, String pic_local_path, String pic_server_path,
                    int menu_item_category_id, int user_id, double price, String created_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pic_local_path = pic_local_path;
        this.pic_server_path = pic_server_path;
        this.menu_item_category_id = menu_item_category_id;
        this.user_id = user_id;
        this.created_at = created_at;
        this.price = price;
    }

    @Ignore
    public MenuItem(int id, String title, String description, String pic_local_path, String pic_server_path,
                    int menu_item_category_id, int user_id, double price, User creator, MenuItemCategory menuItemCategory,
                    String created_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pic_local_path = pic_local_path;
        this.pic_server_path = pic_server_path;
        this.menu_item_category_id = menu_item_category_id;
        this.user_id = user_id;
        this.price = price;
        this.created_at = created_at;
        this.creator = creator;
        this.menuItemCategory = menuItemCategory;
    }

    @Ignore
    public MenuItem(int id) {
        this.id = id;
    }

    @Ignore
    public MenuItem() {
    }

    public static int getMenuIndexInList(List<OrderItem> orderItems, MenuItem menuItem) {
        int index = -1;
        if(orderItems == null)
            return -1;

        for (int i =0; i <orderItems.size(); i++){
            if(orderItems.get(i).getMenu_item_id() == menuItem.getId()){
                index = i;
            }
        }
        return index;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic_local_path() {
        return pic_local_path;
    }

    public void setPic_local_path(String pic_local_path) {
        this.pic_local_path = pic_local_path;
    }

    public String getPic_server_path() {
        return pic_server_path;
    }

    public void setPic_server_path(String pic_server_path) {
        this.pic_server_path = pic_server_path;
    }

    public int getMenu_item_category_id() {
        return menu_item_category_id;
    }

    public void setMenu_item_category_id(int menu_item_category_id) {
        this.menu_item_category_id = menu_item_category_id;
    }

    public MenuItemCategory getMenuItemCategory() {
        return menuItemCategory;
    }

    public void setMenuItemCategory(MenuItemCategory menuItemCategory) {
        this.menuItemCategory = menuItemCategory;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static MenuItem getFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), MenuItem.class);
    }

    public static List<MenuItem> buildListFromObjects(List<Object> data) {
        List<MenuItem> values = new ArrayList<>();
        if (data != null){
            for (int i=0 ; i<data.size(); i++){
                values.add(getFromObject(data.get(i)));
            }
        }
        return values;
    }
}
