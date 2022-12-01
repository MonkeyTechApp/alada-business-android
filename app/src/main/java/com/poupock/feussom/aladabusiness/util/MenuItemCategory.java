package com.poupock.feussom.aladabusiness.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

@Entity(tableName = "menu_item_categories")
public class MenuItemCategory {

    private String created_at;
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int user_id;
    @Ignore private User creator;

    public MenuItemCategory(int id, String name, int user_id, String created_at) {
        this.id = id;
        this.name = name;
        this.user_id = user_id;
        this.created_at = created_at;
    }

    @Ignore
    public MenuItemCategory(int id, String name, int user_id, User creator, String created_at) {
        this.id = id;
        this.name = name;
        this.user_id = user_id;
        this.creator = creator;
        this.created_at = created_at;
    }

    @Ignore
    public MenuItemCategory(int id) {
        this.id = id;
    }

    @Ignore
    public MenuItemCategory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static MenuItemCategory getFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), MenuItemCategory.class);
    }
}
