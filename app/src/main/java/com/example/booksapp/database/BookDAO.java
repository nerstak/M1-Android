package com.example.booksapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookDAO {
    @Query("SELECT * FROM bookentity")
    List<BookEntity> getAll();

    @Query("SELECT * FROM bookentity WHERE title LIKE :title")
    BookEntity findByName(String title);

    @Insert
    void insertAll(BookEntity... books);

    @Delete
    void delete(BookEntity book);

    @Query("DELETE FROM bookentity WHERE id = :idBook")
    void delete(String idBook);
}
