package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.MenuItem;

import java.util.List;

@Dao
public interface MenuItemDao {
    @Insert
    Long insert(MenuItem menu_item);

    @Insert
    List<Long> insertAll(MenuItem... menu_items);

    @Update
    int update(MenuItem menu_item);

    @Delete
    int delete(MenuItem menu_item);

    @Query("SELECT * FROM menu_items WHERE id = :id")
    MenuItem getSpecificMenuItem(int id);

    @Query("SELECT * FROM menu_items")
    List<MenuItem> getAllMenuItems();

    @Query("SELECT * FROM menu_items WHERE menu_category_id = :id")
    List<MenuItem> getSpecificCategoryItems(int id);

    @Query("DELETE FROM menu_items")
    void emptyTable();
}
