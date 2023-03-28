package com.poupock.feussom.aladabusiness.util;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import org.intellij.lang.annotations.Identifier;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "businesses")
public class Business {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int category_id;
    private String category_name;
    private String owner_name;
    private String owner_phone;
    private String location;
    private double longitude;
    private double latitude;
    private String imageUrl;
    private String imageLocalUrl;
    private int zone_id;
    private String description;
    private String hours;
    private String phone;
    private String phone2;
    private int floors;
    private String created_at;
    @ColumnInfo(defaultValue = "1")
    private int salePointCount;
    @Ignore List<MenuItemCategory> menu_categories;
    @Ignore List<MenuItem> menus;
    @Ignore List<GuestTable> tables;
    @Ignore List<Order> orders;
    @Ignore List<User> users;
    @Ignore private UserPivot pivot;

    public Business(int id, String name, int category_id, String category_name, String owner_name, String owner_phone, String location,
                    double longitude, double latitude, String imageUrl, String imageLocalUrl, int zone_id,
                    String description, String hours, String phone, String phone2, int floors, int salePointCount,
                    String created_at) {
        this.id = id;
        this.name = name;
        this.category_id = category_id;
        this.category_name = category_name;
        this.owner_name = owner_name;
        this.owner_phone = owner_phone;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.imageUrl = imageUrl;
        this.imageLocalUrl = imageLocalUrl;
        this.zone_id = zone_id;
        this.description = description;
        this.hours = hours;
        this.phone = phone;
        this.phone2 = phone2;
        this.floors = floors;
        this.created_at = created_at;
        this.salePointCount = salePointCount;
    }

    @Ignore
    public Business(int id) {
        this.id = id;
    }

    @Ignore
    public Business() {
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

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_phone() {
        return owner_phone;
    }

    public void setOwner_phone(String owner_phone) {
        this.owner_phone = owner_phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageLocalUrl() {
        return imageLocalUrl;
    }

    public void setImageLocalUrl(String imageLocalUrl) {
        this.imageLocalUrl = imageLocalUrl;
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public void setSalePointCount(int salePointCount) {
        this.salePointCount = salePointCount;
    }

    public int getSalePointCount() {
        return salePointCount;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static Business getFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), Business.class);
    }

    public List<MenuItemCategory> getMenuItemCategories() {
        return menu_categories   ;
    }

    public void setMenuItemCategories(List<MenuItemCategory> menu_categories) {
        this.menu_categories = menu_categories;
    }

    public List<MenuItemCategory> getMenu_categories() {
        return menu_categories;
    }

    public void setMenu_categories(List<MenuItemCategory> menu_categories) {
        this.menu_categories = menu_categories;
    }

    public List<MenuItem> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuItem> menus) {
        this.menus = menus;
    }

    public List<GuestTable> getTables() {
        return tables;
    }

    public void setTables(List<GuestTable> tables) {
        this.tables = tables;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public static List<Business> buildListFromObjects(List<Object> data) {
        List<Business> values = new ArrayList<>();
        if (data != null){
            for (int i=0 ; i<data.size(); i++){
                values.add(getFromObject(data.get(i)));
            }
        }
        return values;
    }

    public UserPivot getPivot() {
        return pivot;
    }

    public void setPivot(UserPivot pivot) {
        this.pivot = pivot;
    }
}
