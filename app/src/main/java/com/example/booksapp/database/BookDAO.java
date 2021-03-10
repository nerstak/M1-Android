package com.example.booksapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDAO {
    @Query("SELECT * FROM bookentity")
    List<BookEntity> getAll();

    @Query("SELECT * FROM bookentity WHERE title LIKE :title")
    BookEntity findByName(String title);

    @Query("SELECT * FROM bookentity WHERE id = :id")
    BookEntity findByID(String id);

    @Insert
    void insertAll(BookEntity... books);

    @Delete
    void delete(BookEntity book);

    @Query("DELETE FROM bookentity WHERE id = :idBook")
    void delete(String idBook);

    @Update
    void update(BookEntity bookEntity);
}
