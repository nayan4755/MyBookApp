package com.example.android.books.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.example.android.books.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    private BookDbHelper mDbHelper;
    private static final int BOOK_CODE =100;
    private static final int BOOKID_CODE =101;

    private static UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY,BookContract.PATH_BOOK, BOOK_CODE);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY,BookContract.PATH_BOOK +"/#", BOOKID_CODE);
    }



    @Override
    public boolean onCreate() {
        mDbHelper=new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        Cursor cursor;
        int match= uriMatcher.match(uri);

        SQLiteDatabase db=mDbHelper.getReadableDatabase();

        switch (match){

            case BOOK_CODE:
                cursor=db.query(BookEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;

            case BOOKID_CODE:

                s=BookContract.BookEntry._ID+"=?";
                strings1=new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor=db.query(BookContract.BookEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI::" + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(),BookEntry.CONTENT_URI);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOK_CODE:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOKID_CODE:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match=uriMatcher.match(uri);

        switch(match){
            case BOOK_CODE:
                uri= insertBook(uri,contentValues);
                break;

            default:
                throw new IllegalArgumentException("Cannot insert through Uri::"+uri);
        }


        return uri;
    }

    private Uri insertBook(Uri uri, ContentValues values){

        if(values.getAsString(BookContract.BookEntry.COLUMN_NAME)==null)
            throw new IllegalArgumentException("Book requires a name");

        if(values.getAsString(BookContract.BookEntry.COLUMN_GENRE)==null)
            throw new IllegalArgumentException("Book requires a genre attribute");

        if(values.getAsString(BookContract.BookEntry.COLUMN_RATE)==null || Integer.parseInt(values.getAsString(BookContract.BookEntry.COLUMN_RATE))<0)
            throw new IllegalArgumentException("Invalid price");

        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        long id=db.insert(BookContract.BookEntry.TABLE_NAME,null,values);
        if(id>0)
        {
            getContext().getContentResolver().notifyChange(BookContract.BookEntry.CONTENT_URI,null);
        }
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowDeleted=0;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOK_CODE:
                // Delete all rows that match the selection and selection args
                rowDeleted=database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKID_CODE:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowDeleted=database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowDeleted>0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        if(values.getAsString(BookEntry.COLUMN_NAME)==null)
            throw new IllegalArgumentException("Book requires a name");

        if(values.getAsString(BookContract.BookEntry.COLUMN_GENRE)==null)
            throw new IllegalArgumentException("Book requires a genre attribute");

        if(values.getAsString(BookContract.BookEntry.COLUMN_RATE)==null || Integer.parseInt(values.getAsString(BookContract.BookEntry.COLUMN_RATE))<0)
            throw new IllegalArgumentException("Invalid Price");

        final int match = uriMatcher.match(uri);
        int rowUpdated=0;
        switch (match) {
            case BOOK_CODE:
                rowUpdated=updatePet(uri, values, selection, selectionArgs);
                break;
            case BOOKID_CODE:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowUpdated=updatePet(uri, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (rowUpdated>0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowUpdated;
    }

    private int updatePet(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        if (values.containsKey(BookContract.BookEntry.COLUMN_NAME)) {
            String name = values.getAsString(BookContract.BookEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        // If the {@link BookEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(BookEntry.COLUMN_GENRE)) {
            String genre = values.getAsString(BookContract.BookEntry.COLUMN_GENRE);
            if (genre == null) {
                throw new IllegalArgumentException("Book requires valid genre");
            }
        }

        // If the {@link BookEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(BookContract.BookEntry.COLUMN_RATE)) {
            // Check that the price is greater than or equal to 0 kg
            Integer price = Integer.parseInt(values.getAsString(BookContract.BookEntry.COLUMN_RATE));
            if (price < 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        return db.update(BookContract.BookEntry.TABLE_NAME,values,selection,selectionArgs);
    }
}
