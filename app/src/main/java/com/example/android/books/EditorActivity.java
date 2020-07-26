/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.books;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.books.data.BookContract;
import com.example.android.books.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mNameEditText;

    private EditText mDescEditText;

    private EditText mPriceEditText;

    private Spinner mGenreSpinner;

    private int mGenre = 0;
    private Uri uri;

    private boolean isBookChanged =false;

    private View.OnTouchListener mTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(!isBookChanged)
                isBookChanged =true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        uri=getIntent().getData();
        if(uri==null)
            setTitle("Sell your book");
        else {
            setTitle(R.string.editor_activity_title_edit_book);
            getLoaderManager().initLoader(2,null,this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mDescEditText = (EditText) findViewById(R.id.edit_book_desc);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mGenreSpinner = (Spinner) findViewById(R.id.spinner_genre);

        setupSpinner();

        mNameEditText.setOnTouchListener(mTouchListener);
        mDescEditText.setOnTouchListener(mTouchListener);
        mGenreSpinner.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection=new String[]{BookContract.BookEntry._ID,BookContract.BookEntry.COLUMN_NAME,BookEntry.COLUMN_DESC,BookContract.BookEntry.COLUMN_GENRE,BookEntry.COLUMN_RATE};
        return new CursorLoader(this,uri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() == 0)
            return;
        cursor.moveToNext();
        mNameEditText.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME)));
        mDescEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_DESC)));
        mPriceEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_RATE)));

        switch (cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_GENRE))) {
            case "Physics":
                mGenreSpinner.setSelection(1);
                break;
            case "Chemistry":
                mGenreSpinner.setSelection(2);
                break;
            case "CS":
                mGenreSpinner.setSelection(3);
                break;
            case "English":
                mGenreSpinner.setSelection(4);
                break;
            default:
                mGenreSpinner.setSelection(0);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDescEditText.setText("");
        mPriceEditText.setText("");
        mGenreSpinner.setSelection(0);

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGenreSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_phy))) {
                        mGenre = 1; // Phy
                    }
                    else if (selection.equals(getString(R.string.genre_chem))) {
                        mGenre = 2; // Chem
                    }
                    else if (selection.equals(getString(R.string.genre_comp))) {
                        mGenre = 3; // Chem
                    }
                    else if (selection.equals(getString(R.string.genre_eng))) {
                        mGenre = 4; // Chem
                    }
                    else {
                        mGenre = 0; // Maths
                    }

                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = 0; // Unknown
            }
        });
    }


    private void savePet(){
        String name=mNameEditText.getText().toString().trim();
        String description = mDescEditText.getText().toString().trim();
        String genre = mGenreSpinner.getSelectedItem().toString();
        String price = mPriceEditText.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Name field is empty",Toast.LENGTH_SHORT).show();
        }


        else {
            if(TextUtils.isEmpty(price))
                price ="0";
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_NAME, name);
            values.put(BookContract.BookEntry.COLUMN_DESC, description);
            values.put(BookEntry.COLUMN_GENRE, genre);
            values.put(BookContract.BookEntry.COLUMN_RATE, price);

            if (uri == null) {
                long id = ContentUris.parseId(getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values));
                if (id == -1) {
                    Toast.makeText(this, "error in entering data", Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowUpdated = getContentResolver().update(uri, values, null, null);

            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.action_save:
                savePet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(!isBookChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardListener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChanges(discardListener);

    }

    private void showUnsavedChanges(DialogInterface.OnClickListener discardListener){

        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.dialog_message);
        alertBuilder.setPositiveButton(R.string.discard_changes_button,discardListener);
        alertBuilder.setNegativeButton(R.string.keep_editing_button,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface!=null){
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alert=alertBuilder.create();
        alert.show();
    }
}