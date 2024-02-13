package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Details extends AppCompatActivity {
    TextView text;
    String uid;
    //Button logout;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        text = findViewById(R.id.uid_text);

        //logout = findViewById(R.id.logout_btn);
        appDatabase = AppDatabase.getInstance(this);
        Intent intent = getIntent();
        if(intent != null){
            long uid = intent.getLongExtra("id",0);
            Toast.makeText(Details.this,"uid is "+uid,Toast.LENGTH_SHORT).show();

        }


    }

    private void displayAllUsers() {
        // Use lifecycleScope.launch to perform the database operation asynchronously
            // Retrieve all users from the Room database
            UserDao userDao = appDatabase.userDao();
            List<User> userList = userDao.getAllUsers();

            if (!userList.isEmpty()) {
                // Display user details in the UI, e.g., concatenate usernames into a single TextView
                TextView usernameTextView = findViewById(R.id.uid_text);

                StringBuilder allUsernames = new StringBuilder("Usernames:\n");

                for (User user : userList) {
                    allUsernames.append(user.getUsername()).append("\n");
                }

                usernameTextView.setText(allUsernames.toString());
            } else {
                // Handle the case where there are no users
                TextView usernameTextView = findViewById(R.id.uid_text);
                usernameTextView.setText("No users found");
            }
        }
    }
