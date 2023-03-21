package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.OrderItem;

import java.util.List;

@Dao
public interface OrderItemDao {
    @Insert
    Long insert(OrderItem order_item);

    @Insert
    List<Long> insertAll(OrderItem... order_items);

    @Update
    int update(OrderItem order_item);

    @Delete
    int delete(OrderItem order_item);

    @Query("SELECT * FROM order_items WHERE id = :id")
    OrderItem getSpecificOrderItem(int id);

    @Query("SELECT * FROM order_items WHERE menu_item_id = :menuId AND course_id = :courseId")
    OrderItem getSpecificOrderItem(int menuId, int courseId);

    @Query("SELECT * FROM order_items WHERE course_id = :course_id AND menu_item_id = :menu_item_id")
    OrderItem getOrderByCourseIdAndMenuItem(int course_id, int menu_item_id);

    @Query("SELECT * FROM order_items")
    List<OrderItem> getAllOrderItems();

    @Query("SELECT * FROM order_items WHERE course_id = :id")
    List<OrderItem> getAllCourseItems(int id);

    @Query("SELECT * FROM order_items WHERE course_id IN (:ids)")
    List<OrderItem> getAllOrderItems(String ids);
}
