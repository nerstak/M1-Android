package com.example.booksapp.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;
import com.example.booksapp.database.DatabaseUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Async task to Load list of books
 */
public class AsyncFindBooks extends AsyncTask<String, Void, JSONObject> {
    private final static String urlBasis = "https://www.googleapis.com/books/v1/volumes";
    private final String apiKey;

    public AsyncFindBooks(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        URL url = null;
        String query = strings[0].replace(' ', '+');
        try {
            url = new URL(urlBasis + "?q" + query + "&key=" + apiKey);
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
        if (jsonObject != null) {
            try {
                JSONArray books = jsonObject.getJSONArray("items");

                int arrayLength = books.length();

                BookEntity newBook;
                // add each book
                for (int i = 0; i < arrayLength; i++) {
                    newBook = createBook(books.getJSONObject(i));
                    //myAdapter.add(mewBook);
                }

                //myAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Creates a book given its JSON info
     * @param jsonObject book's Json info
     * @return Created BookEntity
     * @throws JSONException Parsing error
     */
    private BookEntity createBook(JSONObject jsonObject) throws JSONException {
        BookEntity book = new BookEntity(jsonObject.getString("id"));
        JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
        book.setAuthor(volumeInfo.getJSONArray("authors").getString(0));
        book.setResume(volumeInfo.getString("description"));
        book.setTitle(volumeInfo.getString("title"));
        book.setPageCount(Integer.parseInt(volumeInfo.getString("pageCount")));
        book.setPublishDate(volumeInfo.getString("publishedDate"));
        return book;
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