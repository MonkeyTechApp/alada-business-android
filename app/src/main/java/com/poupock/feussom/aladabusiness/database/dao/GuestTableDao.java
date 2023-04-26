package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.GuestTable;

import java.util.List;

@Dao
public interface GuestTableDao {
    @Insert
    Long insert(GuestTable guest_table);

    @Insert
    List<Long> insertAll(GuestTable... guest_tables);

    @Update
    int update(GuestTable guest_table);

    @Delete
    int delete(GuestTable guest_table);

    @Query("SELECT * FROM guest_tables WHERE id = :id")
    GuestTable getSpecificGuestTable(int id);

    @Query("SELECT * FROM guest_tables WHERE title = :name")
    GuestTable getSpecificGuestTable(String name);


    @Query("SELECT * FROM guest_tables")
    List<GuestTable> getAllGuestTables();

    @Query("DELETE FROM guest_tables")
    void emptyTable();
}
