package com.example.android.findyourbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class BookImageActivity extends AppCompatActivity {

    private static final String LOG_TAG = BookImageActivity.class.getName();
    public ImageView mImage;
    public URL urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_image);

        mImage = (ImageView) findViewById(R.id.book_image);

        TextView title = (TextView) findViewById(R.id.book_name);
        TextView author = (TextView) findViewById(R.id.author_name);
        TextView publishedBy = (TextView) findViewById(R.id.published_by);
        TextView publishedOn = (TextView) findViewById(R.id.published_on);

        setTitle("Book Details");

        Intent intent = getIntent();
        String url = intent.getStringExtra("imageUrl");
        String bookTitle = intent.getStringExtra("bookTitle");
        String bookAuthor = intent.getStringExtra("bookAuthor");
        String bookPublisher = intent.getStringExtra("bookPublisher");
        String bookPublishDate = intent.getStringExtra("bookPublishDate");

        title.setText(bookTitle);
        author.setText(bookAuthor);
        publishedBy.setText(bookPublisher);
        publishedOn.setText(bookPublishDate);

        try {
            urlImage = new URL(url);
        } catch (MalformedURLException i) {
        }

        ImageAsyncTask task = new ImageAsyncTask();
        task.execute(urlImage);
    }

    public class ImageAsyncTask extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... urls) {
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
                return bitmap;
            } catch (IOException i) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImage.setImageBitmap(bitmap);
        }
    }
}
