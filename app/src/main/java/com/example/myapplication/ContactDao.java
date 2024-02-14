package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    Long insert(Contact contact);

    @Query("SELECT * FROM contacts WHERE user_id = :userId")
    List<Contact> getContactsForUser(long userId);

    @Query("SELECT * FROM contacts WHERE user_id = :userId ORDER BY first_name")
    List<Contact> getContactsForUserByFirstName(long userId);

    @Query("SELECT * FROM contacts WHERE user_id = :userId ORDER BY last_name")
    List<Contact> getContactsForUserByLastName(long userId);

    @Query("SELECT * FROM contacts WHERE user_id = :userId ORDER BY phone_number ")
    List<Contact> getContactsForUserByPhoneNumber(long userId);

    @Query("DELETE FROM contacts WHERE id = :id")
    void deleteContact(long id);
    @Update
    void editContact(Contact contact);





    // other queries...
}
