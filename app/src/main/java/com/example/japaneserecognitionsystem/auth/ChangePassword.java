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

import com.example.japaneserecognitionsystem.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    FirebaseAuth mauth;
    FirebaseUser firebaseUser;
    String userID;

    TextView newPassword, confirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        newPassword = (TextView) findViewById(R.id.changePassowordEditNewPassword);
        confirmNewPassword = (TextView) findViewById(R.id.changePasswordEditConfirmNewPassword);

        //Initialise auth and firestore
        mauth = FirebaseAuth.getInstance();
        firebaseUser = mauth.getCurrentUser();

        //Cancel and close
        ImageButton closeBtn = (ImageButton) findViewById(R.id.changePasswordCloseButton);

        View.OnClickListener OCLCloseBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePassword.this, Login.class));
                finish();
            }
        };
        closeBtn.setOnClickListener(OCLCloseBtn);

        Button doneBtn = (Button) findViewById(R.id.changePasswordDoneBtn);

        View.OnClickListener OCLDoneBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changeNewPassword = newPassword.getText().toString().trim();
                String confirmChangeNewPassword = confirmNewPassword.getText().toString().trim();

                //Empty check
                if(changeNewPassword.isEmpty()){
                    Toast.makeText(ChangePassword.this, "Please enter your new password.", Toast.LENGTH_SHORT).show();

                }else if(confirmChangeNewPassword.isEmpty()){
                    Toast.makeText(ChangePassword.this, "Please enter again your new password.", Toast.LENGTH_SHORT).show();

                }else if(changeNewPassword.equals(confirmChangeNewPassword)){
                    firebaseUser.updatePassword(changeNewPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ChangePassword.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePassword.this, "Password failed to update.", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        };
        doneBtn.setOnClickListener(OCLDoneBtn);


    }
}