package com.example.booksapp.database;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BookEntity {
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "page_count")
    public int pageCount;

    @ColumnInfo(name = "page_read")
    public int pageRead;

    @ColumnInfo(name = "status")
    public StatusBook status;

    @ColumnInfo(name = "cover")
    public Bitmap cover;

    public BookEntity(String id, String title, String author, int pageCount, int pageRead, StatusBook status, Bitmap cover) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.pageRead = pageRead;
        this.status = status;
        this.cover = cover;
    }
}
