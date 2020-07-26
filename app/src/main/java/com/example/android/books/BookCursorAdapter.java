package com.example.android.books;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.books.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameText=(TextView) view.findViewById(R.id.nameText);
        TextView breedText=(TextView) view.findViewById(R.id.descText);
        TextView subjectText=(TextView) view.findViewById(R.id.subjectText);
        TextView priceText=(TextView) view.findViewById(R.id.priceText);

        String name=cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME));
        String breed=cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_DESC));
        String subject=cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_GENRE));
        String bookprice =cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RATE));

        int bookint = Integer.parseInt(bookprice);
        int deliveryCharger = 50;


        nameText.setText(name);
        if(TextUtils.isEmpty(breed))
            breed=context.getString(R.string.unknown_desc);
        breedText.setText(breed);
        subjectText.setText(subject);
        priceText.setText(bookprice + " + Delivery charges Rs50  Total Amount: " + (bookint + deliveryCharger));
    }
}
