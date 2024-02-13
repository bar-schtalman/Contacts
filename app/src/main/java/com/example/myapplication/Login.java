package com.example.myapplication;

import com.facebook.stetho.Stetho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    Button register_btn,login_btn;
    TextView editTextEmail, editTextPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register_btn = findViewById(R.id.register_btn);
        login_btn = findViewById(R.id.login_btn);
        editTextEmail = findViewById(R.id.emailEditBox);
        Stetho.initializeWithDefaults(this);

        editTextPassword = findViewById(R.id.passwordEditBox);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                new LoginUserAsyncTask().execute(username, password);            }
        });

    }
    private class LoginUserAsyncTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... credentials) {
            // Inside your LoginActivity
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            UserDao userDao = db.userDao();

            // Retrieve user by username and password
            return userDao.getUser(credentials[0], credentials[1]);
        }
        protected void onPostExecute(User user) {
            // Handle the result, e.g., show a Toast message
            if (user != null) {
                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                // Redirect to another activity or perform necessary actions
                // For example:
                Intent intent = new Intent(Login.this, UserContacts.class);
                long uid = user.getId();
                intent.putExtra("id",uid);
                startActivity(intent);
            } else {
                Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        }
}}