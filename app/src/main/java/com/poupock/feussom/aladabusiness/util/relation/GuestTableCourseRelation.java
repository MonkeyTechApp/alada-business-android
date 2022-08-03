package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.InternalPoint;
import com.poupock.feussom.aladabusiness.util.User;

public class GuestTableCourseRelation {

    @Embedded
    GuestTable guestTable;

    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )

    public Course course;
}
