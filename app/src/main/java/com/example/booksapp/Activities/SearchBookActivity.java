package com.example.booksapp.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.example.booksapp.AsyncTasks.AsyncBitmapDownloader;
import com.example.booksapp.AsyncTasks.AsyncFindBooks;
import com.example.booksapp.ContentProviders.MySuggestionProvider;
import com.example.booksapp.R;
import com.example.booksapp.Singletons.MySingleton;
import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;
import com.example.booksapp.database.DatabaseUtilities;

import java.lang.ref.WeakReference;
import java.util.Vector;

public class SearchBookActivity extends AppCompatActivity {
    private MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        // Add back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.search_header);

        String bookQuery = null;
        // Gets the search query from intent
        Intent searchIntent = getIntent();
        if (searchIntent.getAction().equals(Intent.ACTION_SEARCH)) {
            bookQuery = searchIntent.getStringExtra(SearchManager.QUERY);

            //Adds query to search history
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(bookQuery, null);
        }

        // Set up list adapter
        myListAdapter = new MyListAdapter(this);
        AsyncFindBooks findBooks = new AsyncFindBooks(getResources().getString(R.string.CONSUMER_KEY), myListAdapter);
        findBooks.execute(bookQuery);
        ListView listView = (ListView) findViewById(R.id.found_list);
        listView.setAdapter(myListAdapter);

        //Add book when tapped
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BookEntity clicked = (BookEntity) myListAdapter.getItem(position);

                BookDatabase db = DatabaseUtilities.getBookDatabase(getApplicationContext());

                //Update if in library, otherwise add
                String message;
                if (db.bookDAO().findByID(clicked.getId()) != null) {
                    db.bookDAO().update(clicked);
                    message = "Book is already in Library";
                } else {
                    db.bookDAO().insertAll(clicked);
                    message = "Book added to Library!";
                    // We download the cover
                    AsyncBitmapDownloader downloader = new AsyncBitmapDownloader(new WeakReference<>(getApplicationContext()), clicked.getId());
                    downloader.execute();
                }
                Toast toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
                toast.show();
            }
        });
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
            return vector.get(position).first;
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
                Response.Listener<Bitmap> rep_listener = imageView::setImageBitmap;
                //Constructs the image request and adds it to the queue
                ImageRequest imageRequest = new ImageRequest(urlCover, rep_listener, 1000, 1000, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, null);
                queue.add(imageRequest);
            } else {
                imageView.setImageResource(R.drawable.default_cover);
            }

            TextView titleView = (TextView) convertView.findViewById(R.id.book_title);
            TextView authorView = (TextView) convertView.findViewById(R.id.book_author);

            titleView.setText(bookInfo.getTitle());
            authorView.setText(bookInfo.getAuthor());

            // Programatically rescale image since Volley does not let you do this in XML
            imageView.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics()) ));

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
