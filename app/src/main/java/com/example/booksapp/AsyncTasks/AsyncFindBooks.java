package com.example.booksapp.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.booksapp.Activities.SearchBookActivity;
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
    private SearchBookActivity.MyListAdapter myListAdapter;

    public AsyncFindBooks(String apiKey, SearchBookActivity.MyListAdapter myListAdapter) {
        this.apiKey = apiKey;
        this.myListAdapter = myListAdapter;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        URL url;
        // Search terms in api are separated by +
        String query = strings[0].replace(' ', '+');
        try {
            url = new URL(urlBasis + "?q=" + query + "&printType=books" + "&maxResults=40" + "&key=" + apiKey);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            Log.i("teo", urlConnection.toString());
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
                Log.i("teo", jsonObject.toString());
                JSONArray books = jsonObject.getJSONArray("items");

                int arrayLength = books.length();

                // add each book
                for (int i = 0; i < arrayLength; i++) {
                    JSONObject currBook = books.getJSONObject(i);
                    BookEntity newBook = createBook(currBook);
                    Log.i("teo", currBook.toString());

                    // Add thumbnail
                    String coverUrl=null;
                    if(currBook.getJSONObject("volumeInfo").has("imageLinks"))
                    {
                        coverUrl = currBook.getJSONObject("volumeInfo")
                                .getJSONObject("imageLinks").getString("thumbnail");
                        Log.i("teo", coverUrl);
                    }

                    myListAdapter.add(newBook, coverUrl);
                }

                // Notify list adapter that all book objects have been created
                myListAdapter.notifyDataSetChanged();
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
        if(volumeInfo.has("authors")) {book.setAuthor(volumeInfo.getJSONArray("authors").getString(0));}
            else {book.setAuthor("Author Unknown");}
        book.setResume(getIfExists(volumeInfo, "description", "No summary available."));
        book.setTitle(getIfExists(volumeInfo, "title", "No Title available."));
        book.setPageCount(Integer.parseInt(getIfExists(volumeInfo, "pagecount", "0")));
        book.setPublishDate(getIfExists(volumeInfo, "publishedDate", "Date unknown"));
        return book;
    }

    /**
     * Get a book's parameter if its JSON info exists
     * @param jsonObject book's Json info
     * @param key Parameter we are looking for
     * @param fallback Value to save instead if parameter does not exist
     * @return Parameter string/Default String
     * @throws JSONException Parsing error
     */
    private String getIfExists(JSONObject jsonObject, String key, String fallback) throws JSONException {
        if(jsonObject.has(key)) {
            return jsonObject.getString(key);
        }
        return fallback;
    }

    /**
     * Actions on result
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