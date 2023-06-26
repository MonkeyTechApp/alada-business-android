package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.relation.OrderCourseListRelation;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    Long insert(Order order);

    @Insert
    List<Long> insertAll(Order... orders);

    @Update
    int update(Order order);

    @Delete
    int delete(Order order);

    @Query("SELECT * FROM orders WHERE id = :id")
    Order getSpecificOrder(long id);

    @Query("SELECT * FROM orders WHERE code = :code")
    Order getSpecificOrder(String code);

    @Query("SELECT * FROM orders WHERE guest_table_id = :id AND status = :status")
    Order getSpecificOrderFromTableId(int id, int status);

    @Transaction
    @Query("SELECT * FROM orders WHERE guest_table_id = :guest_table_id AND status = :status")
    OrderCourseListRelation getOrderWithCourseList(int guest_table_id, int status);

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :order_id")
    OrderCourseListRelation getOrderWithCourseList(long order_id);

    @Query("SELECT * FROM orders")
    List<Order> getAllOrders();

    @Query("SELECT * FROM orders WHERE created_at >= DATE('now')")
    List<Order> getTodayOrders();

    @Query("SELECT * FROM orders WHERE guest_table_id = :table_id AND status = :status")
    List<Order> getTableOrders(int table_id, int status);

    @Query("SELECT * FROM orders WHERE updated_at > uploaded_at")
    List<Order> getToBeUploadedOrders();

    @Query("DELETE FROM orders")
    void emptyTable();
}
