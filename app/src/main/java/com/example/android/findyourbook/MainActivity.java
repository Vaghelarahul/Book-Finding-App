package com.example.android.findyourbook;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.findyourbook.data.Books;
import com.example.android.findyourbook.data.QueryUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Books>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    private BookAdapter mAdapter;
    private TextView mEmptyText;
    private ProgressBar mLoadingSpinner;
    private TextView mInstructionText;
    private static final int BOOK_LOADER_ID = 1;

    private String GOOGLE_BOOK_URL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView bookListView = (ListView) findViewById(R.id.book_list);
        mLoadingSpinner = (ProgressBar) findViewById(R.id.progress);
        mInstructionText = (TextView) findViewById(R.id.instruction_TextView);

        mEmptyText = (TextView) findViewById(R.id.emptyTextView);
        bookListView.setEmptyView(mEmptyText);

        mLoadingSpinner.setVisibility(View.INVISIBLE);

        mAdapter = new BookAdapter(this, new ArrayList<Books>());
        bookListView.setAdapter(mAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Books currentWord = mAdapter.getItem(position);

                String bookTitle = currentWord.getBookTitle();
                String bookAuthor = currentWord.getBookAuthor();
                String bookPublisher = currentWord.getBookPublisher();
                String bookPublishDate = currentWord.getPublishDate();
                String imageUrl = currentWord.getBookUrl();

                Intent bookIntent = new Intent(MainActivity.this, BookImageActivity.class);
                bookIntent.putExtra("imageUrl", imageUrl);
                bookIntent.putExtra("bookTitle", bookTitle);
                bookIntent.putExtra("bookAuthor", bookAuthor);
                bookIntent.putExtra("bookPublisher", bookPublisher);
                bookIntent.putExtra("bookPublishDate", bookPublishDate);
                startActivity(bookIntent);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        String query = null;
        if (intent.getAction().equals(intent.ACTION_SEARCH)){
            query = intent.getStringExtra(SearchManager.QUERY);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (query == null) {
                Toast.makeText(MainActivity.this, R.string.alert_message, Toast.LENGTH_LONG).show();
            } else {
                GOOGLE_BOOK_URL = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=30";
                LoaderManager loaderManager = getLoaderManager();
                Loader<List<Books>> bookLoader = loaderManager.getLoader(BOOK_LOADER_ID);
                if (bookLoader == null) {
                    loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
                } else {
                    loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                }
            }
        } else {
            mLoadingSpinner.setVisibility(View.GONE);
            mInstructionText.setVisibility(View.INVISIBLE);
            mEmptyText.setText(R.string.no_network);
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<List<Books>> onCreateLoader(int id, Bundle bundle) {

        return new  AsyncTaskLoader<List<Books>>(this) {
            List<Books> mJsonRawBookData;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mJsonRawBookData != null) {
                    deliverResult(mJsonRawBookData);
                } else {
                mLoadingSpinner.setVisibility(View.VISIBLE);
                forceLoad();
                }
            }

            @Override
            public List<Books> loadInBackground() {
                if (GOOGLE_BOOK_URL == null) {
                    return null;
                }
                List<Books> booksData = QueryUtils.fetchBooksData(GOOGLE_BOOK_URL);
                return booksData;
            }

            @Override
            public void deliverResult(List<Books> data) {
                mJsonRawBookData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
        public void onLoadFinished(Loader<List<Books>> loader, List<Books> data) {
            mLoadingSpinner.setVisibility(View.GONE);
            mInstructionText.setVisibility(View.GONE);
            mEmptyText.setText(R.string.no_data_found);
            mAdapter.clear();
            if (data != null) {
                mAdapter.addAll(data);
    }
    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_action).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}
