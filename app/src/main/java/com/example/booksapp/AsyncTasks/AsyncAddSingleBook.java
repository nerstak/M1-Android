package com.example.booksapp.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// TODO: Check if we have to keep this class at the end of the project
/**
 * Async task to download one book at the time
 */
public class AsyncAddSingleBook extends AsyncTask<Void, Void, JSONObject> {
    private final WeakReference<Context> contextWeakReference;
    private final String idBook;
    private final static String urlBasis = "https://www.googleapis.com/books/v1/volumes/";
    private final String apiKey;

    public AsyncAddSingleBook(WeakReference<Context> contextWeakReference, String idBook, String apiKey) {
        this.contextWeakReference = contextWeakReference;
        this.idBook = idBook;
        this.apiKey = apiKey;
    }



    // TODO: Check if we can remove strings as input
    @Override
    protected JSONObject doInBackground(Void... voids) {
        URL url = null;
        try {
            url = new URL(urlBasis + idBook + "?key=" + apiKey);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                return handleResult(new BufferedInputStream(urlConnection.getInputStream()));
            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        AsyncBitmapDownloader asyncBitmapDownloader = new AsyncBitmapDownloader(contextWeakReference, idBook);
        if (contextWeakReference != null) {
            Context context = contextWeakReference.get();

            if (context != null) {
                BookDatabase db = Room.databaseBuilder(context,
                        BookDatabase.class, "BookDatabase")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();

                // TODO: Remove this one line
                db.bookDAO().delete(idBook);
                if (jsonObject != null) {
                    try {
                        BookEntity book = new BookEntity(idBook);

                        JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
                        book.setAuthor(volumeInfo.getJSONArray("authors").getString(0));
                        book.setResume(volumeInfo.getString("description"));
                        book.setTitle(volumeInfo.getString("title"));
                        book.setPageCount(Integer.parseInt(volumeInfo.getString("pageCount")));

                        asyncBitmapDownloader.execute(idBook);

                        db.bookDAO().insertAll(book);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                db.close();
            }
        }
    }

    /**
     * Actions on result
     *
     * @param bufferedInputStream Input stream
     * @return JSON Object retrieved
     */
    protected JSONObject handleResult(BufferedInputStream bufferedInputStream) {
        String s = readStream(bufferedInputStream);

        // Json and display
        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read stream and convert it to string
     *
     * @param is Stream
     * @return String
     */
    protected String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
