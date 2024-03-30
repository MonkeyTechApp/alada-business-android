package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.Variation;

import java.util.List;

@Dao
public interface VariationDao {
    @Insert
    Long insert(Variation variation);

    @Insert
    List<Long> insertAll(Variation... variations);

    @Update
    int update(Variation variation);

    @Delete
    int delete(Variation variation);

    @Query("SELECT * FROM variations WHERE id = :id")
    Variation getSpecificVariation(int id);

    @Query("SELECT * FROM variations")
    List<Variation> getAllVariations();

    @Query("SELECT * FROM variations WHERE product_id = :id")
    List<Variation> getSpecificProductItems(int id);

    @Query("DELETE FROM variations")
    void emptyTable();
}
