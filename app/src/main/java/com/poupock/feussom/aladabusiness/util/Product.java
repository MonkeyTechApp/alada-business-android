package com.poupock.feussom.aladabusiness.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true) int id;
    String name;
    @Ignore List<Variation> variations;

    @Ignore
    public Product(int id, String name, List<Variation> variations) {
        this.id = id;
        this.name = name;
        this.variations = variations;
    }

    public Product(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Ignore
    public Product(int id) {
        this.id = id;
    }

    @Ignore
    public Product() {
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

    public List<Variation> getVariations() {
        return variations;
    }

    public void setVariations(List<Variation> variations) {
        this.variations = variations;
    }
}
