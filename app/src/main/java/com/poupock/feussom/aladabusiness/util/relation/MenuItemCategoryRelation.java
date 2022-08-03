package com.poupock.feussom.aladabusiness.util.relation;

import android.view.Menu;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;

public class MenuItemCategoryRelation {

   @Embedded
    public MenuItemCategory menuItemCategory;

   @Relation(
       parentColumn = "id",
       entityColumn = "menu_item_category_id"
   )

   public MenuItem menuItem;
}
