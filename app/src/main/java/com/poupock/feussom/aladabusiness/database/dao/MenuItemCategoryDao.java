package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.MenuItemCategory;

import java.util.List;

@Dao
public interface MenuItemCategoryDao {
    @Insert
    Long insert(MenuItemCategory menu_item_category);

    @Insert
    List<Long> insertAll(MenuItemCategory... menu_item_categories);

    @Update
    int update(MenuItemCategory menu_item_category);

    @Delete
    int delete(MenuItemCategory menu_item_category);

    @Query("SELECT * FROM menu_item_categories WHERE id = :id")
    MenuItemCategory getSpecificMenuItemCategory(int id);

    @Query("SELECT * FROM menu_item_categories")
    List<MenuItemCategory> getAllMenuItemCategories();

    @Query("SELECT * FROM menu_item_categories WHERE name = :name")
    MenuItemCategory getSpecificMenuItemCategory(String name);

    @Query("DELETE FROM menu_item_categories")
    void emptyTable();
}
