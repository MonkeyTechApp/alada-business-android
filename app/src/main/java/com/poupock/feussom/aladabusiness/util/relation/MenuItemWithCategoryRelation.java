package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;

public class MenuItemWithCategoryRelation {

   @Embedded
    public MenuItemCategory menuItemCategory;

   @Relation(
       parentColumn = "id",
       entityColumn = "menu_item_category_id"
   )

   public MenuItem menuItem;
}
