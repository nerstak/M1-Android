package com.example.booksapp.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.File;

@Entity
public class BookEntity {
    @PrimaryKey @NonNull
    private String id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "page_count")
    private int pageCount;

    @ColumnInfo(name = "page_read")
    private int pageRead;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "resume")
    private String resume;

    @ColumnInfo(name = "publish_date")
    private String publishDate;

    @ColumnInfo(name = "rating")
    private String rating;

    public BookEntity(@NonNull String id, String title, String author, int pageCount, int pageRead, String status, String resume, String publishDate, String rating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.pageRead = pageRead;
        this.status = status;
        this.resume = resume;
        this.publishDate = publishDate;
        this.rating = rating;
    }

    @Ignore
    public BookEntity(@NonNull String id) {
        this.id = id;
        this.pageRead = 0;
        this.status = StatusBook.Unknown.toString();
        this.rating = "Unrated";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        if(pageCount > 0) {
            this.pageCount = pageCount;
        }
    }

    public int getPageRead() {
        return pageRead;
    }

    public boolean setPageRead(int pageRead) {
        if(pageRead >= 0 &&  pageRead <= this.pageCount) {
            this.pageRead = pageRead;
            return true;
        }
        return false;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    /**
     * Load image of book
     * @param context Context
     * @return Bitmap or null if file is missing
     */
    public Bitmap loadImage(Context context) {
        // TODO: Load custom image first, if not load cache
        File file = new File(context.getCacheDir(), id);
        if (file.exists()) {
            Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());

            // Upscaling image
            return Bitmap.createScaledBitmap(b, 400, 600, true);
        }
        return null;
    }
}
