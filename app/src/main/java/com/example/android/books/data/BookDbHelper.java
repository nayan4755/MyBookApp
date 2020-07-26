package com.example.android.books.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import com.example.android.books.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shelter.db";
    public static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_PET_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME
                + "(" + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_DESC + " TEXT NOT NULL, "
                + BookEntry.COLUMN_GENRE + " INTEGER, "
                + BookEntry.COLUMN_RATE + " INTEGER NOT NULL DEFAULT 0);";

        sqLiteDatabase.execSQL(CREATE_PET_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE "+ DATABASE_NAME );
    }
    
}
