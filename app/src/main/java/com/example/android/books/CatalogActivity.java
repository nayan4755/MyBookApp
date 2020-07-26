package com.example.android.books;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.books.data.BookContract;
import com.example.android.books.data.BookContract.BookEntry;
import com.google.firebase.auth.FirebaseAuth;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    BookCursorAdapter cursorAdapter;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        firebaseAuth = FirebaseAuth.getInstance();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        View emptyView =findViewById(R.id.empty_view);
        ListView listView=(ListView) findViewById(R.id.listView);
        listView.setEmptyView(emptyView);

        cursorAdapter=new BookCursorAdapter(this,null);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long id) {
                Intent editIntent=new Intent();
                editIntent.setClass(getApplicationContext(),DescActivity.class);//i have taken it to Desc page..its a new change
                editIntent.setData(ContentUris.withAppendedId(BookEntry.CONTENT_URI,id));
                startActivity(editIntent);
            }
        });
        getLoaderManager().initLoader(1,null,this);
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(CatalogActivity.this, LoginActivity.class));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_NAME, BookEntry.COLUMN_DESC, BookEntry.COLUMN_GENRE, BookEntry.COLUMN_RATE};
        return new CursorLoader(this,BookContract.BookEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logout:{
                Logout();
                break;
            }
            case R.id.profileMenu:
                startActivity(new Intent(CatalogActivity.this, ProfileActivity.class));
                break;
            case R.id.searchMenu:
                startActivity(new Intent(CatalogActivity.this, SearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_all_books_title);
        builder.setMessage(R.string.delete_all_books_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        int rowDeleted=getContentResolver().delete(BookEntry.CONTENT_URI,null,null);
        if(rowDeleted<=0){

            Toast.makeText(this,R.string.editor_delete_book_failed,Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this,R.string.editor_delete_book_successful,Toast.LENGTH_LONG).show();
    }


}
