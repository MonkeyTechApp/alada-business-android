package com.poupock.feussom.aladabusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.poupock.feussom.aladabusiness.util.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    Long insert(User user);

    @Insert
    List<Long> insertAll(User... users);

    @Update
    int update(User user);

    @Delete
    int delete(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    User getSpecificUser(int id);


    @Query("SELECT * FROM users WHERE role_id = :id")
    User getUsersWithRole(int id);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("DELETE FROM users")
    void emptyTable();
}
