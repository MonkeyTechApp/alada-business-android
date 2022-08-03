package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;
import com.poupock.feussom.aladabusiness.util.OrderItem;

public class OrderItemWithMenuItemRelation {

   @Embedded
    public OrderItem orderItem;

   @Relation(
       parentColumn = "menu_item_id",
       entityColumn = "id"
   )

   public MenuItem menuItem;
}
