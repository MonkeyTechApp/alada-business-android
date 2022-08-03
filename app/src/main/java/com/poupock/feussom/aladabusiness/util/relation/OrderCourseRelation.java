package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Order;

public class OrderCourseRelation {
    @Embedded
    Order order;

    @Relation(
        parentColumn = "id",
        entityColumn = "order_id"
    )

    public Course course;
}
