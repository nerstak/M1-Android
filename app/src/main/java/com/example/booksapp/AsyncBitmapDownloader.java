package com.example.booksapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Download one image
 */
public class AsyncBitmapDownloader extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<Context> contextViewWeakReference;
    private final String idBook;

    public AsyncBitmapDownloader(WeakReference<Context> contextViewWeakReference, String idBook) {
        this.contextViewWeakReference = contextViewWeakReference;
        this.idBook = idBook;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            Bitmap bm = null;
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                bm = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.i("karsto", e.toString());
            } finally {
                urlConnection.disconnect();
            }

            return bm;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(contextViewWeakReference != null && bitmap != null) {
            Context context = contextViewWeakReference.get();

            if(context != null) {
                try {
                    File f = new File(context.getCacheDir(), idBook);
                    if(!f.exists()) {
                        FileOutputStream fos = new FileOutputStream(f);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
