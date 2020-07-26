package com.example.android.books;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.books.data.BookContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DescActivity extends AppCompatActivity {

    private Button Buy;
    private Uri uri;
    private TextView Description;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);

        Buy = (Button)findViewById(R.id.buybtn);
        Description = (TextView)findViewById(R.id.textView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        uri=getIntent().getData();

        Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DescActivity.this, BuyingActivity.class));
                deletePet();
                finish();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                Description.setText("Hello " + userProfile.getUserName() + "! Your book will be delivered to the address: " + userProfile.getUserAddress() + ". Please press the button below to buy your book.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DescActivity.this,databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void deletePet() {
        // TODO: Implement this method
        int rowDeleted=getContentResolver().delete(uri,null,null);
    }


}
