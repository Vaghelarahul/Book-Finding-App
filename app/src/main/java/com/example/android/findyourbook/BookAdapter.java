package com.example.android.findyourbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.findyourbook.data.Books;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Books> {

    public BookAdapter(Context context, List<Books> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_adapter, parent, false);

        }

        Books currentWord = getItem(position);

        TextView titleView = (TextView) listItemView.findViewById(R.id.book_title);
        titleView.setText(currentWord.getBookTitle());

        TextView authorView = (TextView) listItemView.findViewById(R.id.book_author);
        authorView.setText(currentWord.getBookAuthor());

        return listItemView;
    }
}
