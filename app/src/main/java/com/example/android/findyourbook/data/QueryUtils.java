package com.example.android.findyourbook.data;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Books> fetchBooksData(String requestUrl) {

        String jsonResponse = null;
        URL urls = createUrl(requestUrl);

        try {
            jsonResponse = makeHttpRequest(urls);

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Books> books = parseJsonResponse(jsonResponse);
        return books;
    }

    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error with connection" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "problem retrieving data");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromInputStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        try {
            String line = bufferedReader.readLine();
            output.append(line);
            while (line != null) {
                line = bufferedReader.readLine();
                output.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    private static List<Books> parseJsonResponse(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Books> booksList = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray arrayElement = root.getJSONArray("items");

            for (int i = 0; i < arrayElement.length(); i++) {

                JSONObject objectElement = arrayElement.getJSONObject(i);
                JSONObject volumeInfo = objectElement.getJSONObject("volumeInfo");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                JSONObject imageInfo = volumeInfo.getJSONObject("imageLinks");


                String bookTitle = volumeInfo.getString("title");
                String bookAuthor = authors.getString(0);
                String publisher = volumeInfo.getString("publisher");
                String publishedDate = volumeInfo.getString("publishedDate");
                String imageUri = imageInfo.getString("smallThumbnail");

                Books data = new Books(bookTitle, bookAuthor, publisher, publishedDate, imageUri);
                booksList.add(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return booksList;
    }

}
