package com.example.booksapp.database;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BookEntity {
    @PrimaryKey @NonNull
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
    public String status;

    @ColumnInfo(name = "resume")
    public String resume;

    public BookEntity(String id, String title, String author, int pageCount, int pageRead, String status, String resume) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.pageRead = pageRead;
        this.status = status;
        this.resume = resume;
    }
}
