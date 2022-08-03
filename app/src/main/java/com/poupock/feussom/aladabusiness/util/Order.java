package com.poupock.feussom.aladabusiness.util;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String code;
    private int user_id;
    private int internal_point_id;
    private int status;
    @ColumnInfo(defaultValue = "0")
    private int guest_table_id;
    @Ignore private GuestTable guestTable;
    @Ignore InternalPoint internalPoint;
    @Ignore private User creator;
    private String created_at;
    @Ignore private List<Course> courseList;


    public Order(int id, String code, int user_id, int internal_point_id, int guest_table_id, int status,
                 String created_at) {
        this.id = id;
        this.created_at = created_at;
        this.code = code;
        this.user_id = user_id;
        this.status = status;
        this.internal_point_id = internal_point_id;
        this.guest_table_id = guest_table_id;
    }

    @Ignore
    public Order(int id, String code, int user_id, int internal_point_id, int guest_table_id, GuestTable guestTable,
                 int status,
                 InternalPoint internalPoint, User creator, List<Course> courseList, String created_at) {
        this.id = id;
        this.created_at = created_at;
        this.code = code;
        this.user_id = user_id;
        this.internal_point_id = internal_point_id;
        this.guest_table_id = guest_table_id;
        this.guestTable = guestTable;
        this.internalPoint = internalPoint;
        this.status = status;
        this.creator = creator;
        this.courseList = courseList;
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
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
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
}
