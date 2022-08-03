package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Order;

import java.util.List;

public class OrderCourseListRelation {
    @Embedded public Order order;

    @Relation(
        parentColumn = "id",
        entityColumn = "order_id"
    )

    public List<Course> courses;

    public Order buildOrderObject() {
        Order order = this.order;
        order.setCourseList(this.courses);
        return order;
    }
}
