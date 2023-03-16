package com.poupock.feussom.aladabusiness.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "payment_method")
public class PaymentMethod {

    @PrimaryKey(autoGenerate = true)
    int id;
    String description;
    String name;
    String created_at;


    public PaymentMethod(int id, String description, String name, String created_at) {
        this.id = id;
        this.description = description;
        this.created_at = created_at;
        this.name = name;
    }

    @Ignore
    public PaymentMethod(int id) {
        this.id = id;
    }

    @Ignore
    public PaymentMethod() {
    }

    public static boolean has(List<PaymentMethod> roleList, String value) {
        for (int i = 0 ; i < roleList.size() ; i ++ ) {
            if (roleList.get(i).getName().equals(value)) return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static List<PaymentMethod> buildListFromObjects(List<Object> data) {
        List<PaymentMethod> values = new ArrayList<>();
        if (data != null){
            for (int i=0 ; i<data.size(); i++){
                values.add(getObjectFromObject(data.get(i)));
            }
        }
        return values;
    }

    public static PaymentMethod getObjectFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), PaymentMethod.class);
    }
}
