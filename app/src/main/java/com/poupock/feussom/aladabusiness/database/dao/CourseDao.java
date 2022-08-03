package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.relation.CourseWithItemListRelation;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    Long insert(Course course);

    @Insert
    List<Long> insertAll(Course... courses);

    @Update
    int update(Course course);

    @Delete
    int delete(Course course);

    @Query("SELECT * FROM courses WHERE id = :id")
    Course getSpecificCourse(int id);

    @Query("SELECT * FROM courses")
    List<Course> getAllCourses();

    @Query("SELECT * FROM courses WHERE order_id = :id")
    List<Course> getOrderCourses(int id);

    @Transaction
    @Query("SELECT * FROM courses WHERE order_id = :id")
    List<CourseWithItemListRelation> getOrderCoursesWithOrderItem(int id);
}
