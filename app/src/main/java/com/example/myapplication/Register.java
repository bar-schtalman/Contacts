package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {
    Button register;
    EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.button);
        editTextUsername = findViewById(R.id.usernameEditText);
        editTextPassword = findViewById(R.id.passwordEditText);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assuming you have retrieved username and password from your UI
                final String username = editTextUsername.getText().toString();
                final String password = editTextPassword.getText().toString();

                // Execute AsyncTask to check if the username already exists in the background
                new CheckUsernameAsyncTask().execute(username, password);
            }
        });
    }

    private class CheckUsernameAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // Inside your SignupActivity
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            UserDao userDao = db.userDao();

            // Check if the username already exists
            String username = params[0];

            User existingUser = userDao.getUserByUsername(username);

            // Return true if the username does not exist, false otherwise
            return existingUser == null;
        }

        @Override
        protected void onPostExecute(Boolean isUsernameUnique) {
            // Handle the result, e.g., show a Toast message
            if (isUsernameUnique) {
                // Username is unique, proceed with user registration
                User newUser = new User();
                newUser.setUsername(editTextUsername.getText().toString());
                newUser.setPassword(editTextPassword.getText().toString());

                // Execute AsyncTask to insert user in the background
                new InsertUserAsyncTask().execute(newUser);
            } else {
                // Username already exists, show an error message
                Toast.makeText(Register.this, "Username already exists. Please choose a different username.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class InsertUserAsyncTask extends AsyncTask<User, Void, Long> {
        @Override
        protected Long doInBackground(User... users) {
            // Inside your SignupActivity
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            UserDao userDao = db.userDao();

            // Assuming you have retrieved username and password from your UI
            return userDao.insertUser(users[0]);
        }

        @Override
        protected void onPostExecute(Long userId) {
            // Handle the result, e.g., show a Toast message
            if (userId > 0) {
                Toast.makeText(Register.this, "User successfully registered " + userId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, UserContacts.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            } else {
                Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
