package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.Business;

import java.util.List;

@Dao
public interface BusinessDao {
    /**
     *
     * @param Business
     * @return The row id of the business entity created.
     */
    @Insert
    Long insert(Business Business);

    /**
     *
     * @param Businesses
     * @return List of id of all the rows created
     */
    @Insert
    List<Long> insertAll(Business... Businesses);

    /**
     *
     * @param Business
     * @return the number of rows updated.
     */
    @Update
    int update(Business Business);

    /**
     *
     * @param Business
     * @return the number of rows deleted.
     */
    @Delete
    int delete(Business Business);

    @Query("SELECT * FROM businesses WHERE id = :id")
    Business getSpecificBusiness(int id);

    @Query("SELECT * FROM businesses")
    List<Business> getAllBusinesses();

    @Query("DELETE FROM businesses")
    void emptyTable();
}
