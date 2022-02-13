package com.example.japaneserecognitionsystem.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.japaneserecognitionsystem.MainActivity;
import com.example.japaneserecognitionsystem.R;
import com.example.japaneserecognitionsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    FirebaseAuth mauth;
    FirebaseFirestore db;
    DocumentReference documentReference;
    String userID;

    CircleImageView ProfilePicture;
    TextView userName, emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfilePicture = (CircleImageView) findViewById(R.id.profilePicture);
        ImageButton closeBtn = (ImageButton) findViewById(R.id.profileCloseButton);
        ImageButton editBtn = (ImageButton) findViewById(R.id.profileEditButton);

        userName = (TextView) findViewById(R.id.profileUserName);
        emailAddress = (TextView) findViewById(R.id.profileEmailAddress);

        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mauth.getCurrentUser().getUid();
        documentReference = db.collection("Notes").document(userID)
                .collection("Users").document(userID);

        View.OnClickListener OCLEditProfile = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, EditProfile.class));
            }
        };

        editBtn.setOnClickListener(OCLEditProfile);

        View.OnClickListener OCLCloseBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        };
        closeBtn.setOnClickListener(OCLCloseBtn);

        getUserInfo();
    }

    //Get user name and profile picture from database
    private void getUserInfo() {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String name = user.getUserName();
                String email = user.getEmailAddress();
                String imageURL = user.getImageURL();

                Picasso.get().load(imageURL).into(ProfilePicture);

                userName.setText(name);
                emailAddress.setText(email);
            }
        });
    }
}