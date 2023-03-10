package com.poupock.feussom.aladabusiness.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "internal_points")
public class InternalPoint {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private int user_id;
    @Ignore private User creator;
    private String created_at;

    public InternalPoint(int id, String title, int user_id, String created_at) {
        this.id = id;
        this.title = title;
        this.user_id = user_id;
        this.created_at = created_at;
    }

    @Ignore
    public InternalPoint(int id, String title, int user_id, User creator, String created_at) {
        this.id = id;
        this.created_at = created_at;
        this.title = title;
        this.user_id = user_id;
        this.creator = creator;
    }

    @Ignore
    public InternalPoint(int id) {
        this.id = id;
    }

    @Ignore
    public InternalPoint() {
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

    public static List<InternalPoint> buildListFromObjects(List<Object> data) {
        List<InternalPoint> values = new ArrayList<>();
        if (data != null){
            for (int i=0 ; i<data.size(); i++){
                values.add(getObjectFromObject(data.get(i)));
            }
        }
        return values;
    }

    public static InternalPoint getObjectFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), InternalPoint.class);
    }
}
