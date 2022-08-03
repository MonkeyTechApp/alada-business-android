package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.Role;

import java.util.List;

@Dao
public interface RoleDao {
    @Insert
    Long insert(Role role);

    @Insert
    List<Long> insertAll(Role... roles);

    @Update
    int update(Role role);

    @Delete
    int delete(Role role);

    @Query("SELECT * FROM roles WHERE id = :id")
    Role getSpecificRole(int id);


    @Query("SELECT * FROM roles")
    List<Role> getAllRoles();

}
