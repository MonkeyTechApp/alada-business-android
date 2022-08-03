package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.InternalPoint;
import com.poupock.feussom.aladabusiness.util.User;

public class UserCourseRelation {

    @Embedded
    User user;

    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )

    public Course course;
}
