package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class CourseWithItemListRelation {
    @Embedded public Course course;

    @Relation(
        parentColumn = "id",
        entityColumn = "course_id"
    )

    public List<OrderItem> items;

    public Course buildCourseObject() {
        Course course = this.course;
        course.setOrderItems(this.items);
        return course;
    }

    public static List<Course> getListCourseFromRelation(List<CourseWithItemListRelation> courseWithItemListRelations){
        List<Course> courses = new ArrayList<>();
        for (int i = 0 ; i < courseWithItemListRelations.size(); i++){
            courses.add(courseWithItemListRelations.get(i).buildCourseObject());
        }
        return courses;
    }
}
