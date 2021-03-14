package com.example.booksapp.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.example.booksapp.AsyncTasks.AsyncFindBooks;
import com.example.booksapp.R;
import com.example.booksapp.Singletons.MySingleton;
import com.example.booksapp.database.BookEntity;

import java.util.Vector;

public class SearchBookActivity extends AppCompatActivity {
    private MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.search_header);

        String bookQuery = null;
        // Gets the search query from intent
        Intent searchIntent = getIntent();
        if (searchIntent.getAction().equals(Intent.ACTION_SEARCH)) {
            bookQuery = searchIntent.getStringExtra(SearchManager.QUERY);
        }

        myListAdapter = new MyListAdapter(this);
        AsyncFindBooks findBooks = new AsyncFindBooks(getResources().getString(R.string.CONSUMER_KEY), myListAdapter);
        findBooks.execute(bookQuery);
        ListView listView = (ListView) findViewById(R.id.found_list);
        listView.setAdapter(myListAdapter);
    }

    public class MyListAdapter extends BaseAdapter{
        private final Context context;
        private Vector<Pair<BookEntity,String>> vector;

        public MyListAdapter(Context context) {
            this.context = context;
            this.vector = new Vector<>();
        }

        @Override
        public int getCount() {
            return vector.size();
        }

        @Override
        public Object getItem(int position) {
            return vector.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void add(BookEntity book, String url) {
            Pair<BookEntity,String> newPair = new Pair<>(book,url);
            vector.add(newPair);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BookEntity bookInfo = vector.get(position).first;
            String urlCover = vector.get(position).second;

            //gets the request queue from our singleton
            RequestQueue queue = MySingleton.getInstance(context).getRequestQueue();

            if(convertView==null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.found_book_layout,parent,false);
            }

            //puts the imageview in the response listener
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.bitmap_small_cover);
            if(urlCover!=null)
            {
                Response.Listener<Bitmap> rep_listener = response -> {
                    imageView.setImageBitmap(response);
                };
                //Constructs the image request and adds it to the queue
                ImageRequest imageRequest = new ImageRequest(urlCover, rep_listener, 1000, 1000, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, null);
                queue.add(imageRequest);
            } else {
                imageView.setImageResource(R.drawable.default_cover);
            }

            TextView titleView = (TextView) convertView.findViewById(R.id.book_title);
            TextView authorView = (TextView) convertView.findViewById(R.id.book_author);

            titleView.setText(bookInfo.getTitle());
            authorView.setText(bookInfo.getAuthor());
            return convertView;
        }
    }

    /**
     * Function to go back to the previous activity
     * @param item Menu Item
     * @return Boolean of success
     */
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
