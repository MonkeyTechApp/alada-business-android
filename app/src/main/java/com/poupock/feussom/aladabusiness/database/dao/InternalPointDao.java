package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.InternalPoint;

import java.util.List;

@Dao
public interface InternalPointDao {
    @Insert
    Long insert(InternalPoint internal_point);

    @Insert
    List<Long> insertAll(InternalPoint... internal_points);

    @Update
    int update(InternalPoint internal_point);

    @Delete
    int delete(InternalPoint internal_point);

    @Query("SELECT * FROM internal_points WHERE id = :id")
    InternalPoint getSpecificInternalPoint(int id);


    @Query("SELECT * FROM internal_points")
    List<InternalPoint> getAllInternalPoints();

}
