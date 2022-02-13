package com.example.japaneserecognitionsystem.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.japaneserecognitionsystem.MainActivity;
import com.example.japaneserecognitionsystem.R;
import com.example.japaneserecognitionsystem.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    FirebaseAuth mauth;
    FirebaseFirestore db;
    DocumentReference documentReference;
    FirebaseUser firebaseUser;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView registerUserName = (TextView) findViewById(R.id.editRegisterUserName);
        TextView registerEmailAddress = (TextView) findViewById(R.id.editTextRegisterEmailAddress);
        TextView registerPassword = (TextView) findViewById(R.id.editRegisterPassword);

        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(mauth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        Button registerBtn = (Button) findViewById(R.id.buttonRegister);

        View.OnClickListener OCLRegister = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = registerUserName.getText().toString().trim();
                String emailAddress = registerEmailAddress.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();

                //check empty
               if(userName.isEmpty()) {
                   Toast.makeText(Register.this, "Please enter your user name.", Toast.LENGTH_SHORT).show();

               }else if(emailAddress.isEmpty()){
                    Toast.makeText(Register.this, "Please enter your email address.", Toast.LENGTH_SHORT).show();

                }else if(password.isEmpty()){
                    Toast.makeText(Register.this, "Please enter your password.", Toast.LENGTH_SHORT).show();

                }else{
                   mauth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               firebaseUser = mauth.getCurrentUser();
                               userID = firebaseUser.getUid();
                               documentReference = db.collection("Notes").document(userID)
                                       .collection("Users").document(userID);
                               User user = new User(userName, emailAddress);

                               documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {
                                       Toast.makeText(Register.this, "User Register Sucessfully", Toast.LENGTH_SHORT).show();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(Register.this, "User Register Failed", Toast.LENGTH_SHORT).show();
                                   }
                               });
                               startActivity(new Intent(getApplicationContext(), MainActivity.class));
                           }else{
                               Toast.makeText(Register.this, "Unable to Register User", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
               }
            }
        };
        registerBtn.setOnClickListener(OCLRegister);

        //Cancel registration
        ImageButton backToLoginBtn = (ImageButton) findViewById(R.id.registerCloseButton);

        View.OnClickListener OCLBackToLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        };

        backToLoginBtn.setOnClickListener(OCLBackToLogin);



    }
}