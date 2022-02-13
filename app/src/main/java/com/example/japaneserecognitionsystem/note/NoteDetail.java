package com.example.japaneserecognitionsystem.note;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.japaneserecognitionsystem.MainActivity;
import com.example.japaneserecognitionsystem.R;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NoteDetail extends AppCompatActivity {

    Intent data;


    FirebaseFirestore db;
    FirebaseAuth mauth;
    String userID;
    DocumentReference documentReference;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Authentication and Firestore initialization
        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mauth.getCurrentUser().getUid();

        //Get data from firestore
        data = getIntent();

        TextView detailTitle = (TextView) findViewById(R.id.noteDetailTitle);
        TextView detailContent = (TextView) findViewById(R.id.noteDetailContent);
        detailContent.setMovementMethod(new ScrollingMovementMethod());

        detailContent.setText(data.getStringExtra("content"));
        detailTitle.setText(data.getStringExtra("title"));
        //detailContent.setBackgroundColor(getResources().getColor(data.getIntExtra("code", 0), null));

        //Edit note
        ImageButton detailEdit = (ImageButton) findViewById(R.id.noteDetailEditButton);
        detailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), EditNote.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteID", data.getStringExtra("noteID"));
                startActivity(intent);
            }
        });

        //Delete note
        ImageButton detailDelete = (ImageButton) findViewById(R.id.noteDetailDeleteButton);
        detailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference = db.collection("Notes").document(userID)
                        .collection("myNotes").document(data.getStringExtra("noteID"));
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NoteDetail.this, "Note Deleted.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NoteDetail.this, "Failed to Delete.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        ImageButton detailClose = (ImageButton) findViewById(R.id.noteDetailCloseButton);
        detailClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

}