package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    Long insert(Product product);

    @Insert
    List<Long> insertAll(Product... products);

    @Update
    int update(Product product);

    @Delete
    int delete(Product product);

    @Query("SELECT * FROM products WHERE id = :id")
    Product getSpecificProduct(int id);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("DELETE FROM products")
    void emptyTable();
}
