package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUser(String username, String password);

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(long userId);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT id FROM users WHERE username = :username")
    Long getIdByUsername(String username);
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);


}
