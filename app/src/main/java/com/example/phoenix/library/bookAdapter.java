package com.example.phoenix.library;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class bookAdapter extends ArrayAdapter<book> {
    //private int colorResourceID;
    private boolean ok;
    public bookAdapter(Activity context, ArrayList<book> books, boolean ok) {
        super(context, 0, books);
        //this.colorResourceID=colorResourceID;
        this.ok = ok;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        book currentBook = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.book_title);
        titleTextView.setText(currentBook.getTitle());

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.book_author);
        authorTextView.setText(currentBook.getAuthor());

        TextView themeTextView = (TextView) listItemView.findViewById(R.id.book_theme);
        themeTextView.setText(currentBook.getTheme());

        if (ok){
            TextView countTextView = listItemView.findViewById(R.id.book_count);
            String bookCount = "left: "+currentBook.getBookCount();
            countTextView.setText(bookCount);
        }

        return listItemView;
    }
}
