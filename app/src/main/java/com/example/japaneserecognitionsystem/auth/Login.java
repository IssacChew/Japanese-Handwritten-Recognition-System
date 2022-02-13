package com.example.japaneserecognitionsystem.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.japaneserecognitionsystem.MainActivity;
import com.example.japaneserecognitionsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText loginEmailAddress = findViewById(R.id.editTextEmailAddress);
        EditText loginPassword = findViewById(R.id.editTextLoginPassword);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Login button (custom login)
        Button loginBtn = (Button) findViewById(R.id.buttonlogin);

        View.OnClickListener OCLLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = loginEmailAddress.getText().toString();
                String password = loginPassword.getText().toString();

                //Empty check
                if(emailAddress.isEmpty()){
                    Toast.makeText(Login.this, "Please enter your email address.", Toast.LENGTH_SHORT).show();

                }else if(password.isEmpty()){
                    Toast.makeText(Login.this, "Please enter your password.", Toast.LENGTH_SHORT).show();

                }else{
                    //Sign in with Email and Password
                    mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(Login.this, "Sign In Successful.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(Login.this, "Sign In Failed.", Toast.LENGTH_SHORT).show();
                            };
                        };
                    });
                }
            }
        };

        loginBtn.setOnClickListener(OCLLogin);

        //Register using email button (custom register as new user)
        Button registerUsingEmail = (Button) findViewById(R.id.registerUsingEmail);

        View.OnClickListener OCLRegisterUsingEmail = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        };

        registerUsingEmail.setOnClickListener(OCLRegisterUsingEmail);

        //Forget Password
        TextView forgetPasswordBtn = (TextView) findViewById(R.id.LoginForgetPasswordBtn);

        View.OnClickListener OCLForgetPassword = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ChangePassword.class));
                finish();
            }
        };

        forgetPasswordBtn.setOnClickListener(OCLForgetPassword);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

    }

}