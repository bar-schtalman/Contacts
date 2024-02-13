package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class UserContacts extends AppCompatActivity {
    Button add, logout;
    long uid;
    private ContactDao contactDao;
    //TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_contacts);
        add = findViewById(R.id.add);
        //textView = findViewById(R.id.contactView);

        Intent intent = getIntent();
        if (intent != null) {
            uid = intent.getLongExtra("id", 0);
            Toast.makeText(UserContacts.this, "uid is " + uid, Toast.LENGTH_SHORT).show();
        }
        AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        contactDao = appDatabase.contactDao();
        new RetrieveContactsAsyncTask().execute(uid);

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserContacts.this, Login.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserContacts.this, AddContact.class);
                intent.putExtra("id", uid);
                startActivity(intent);
            }
        });
    }

    private class RetrieveContactsAsyncTask extends AsyncTask<Long, Void, List<Contact>> {
        @Override
        protected List<Contact> doInBackground(Long... params) {
            long userId = params[0];
            return contactDao.getContactsForUserByFirstName(userId);
        }

        @Override
        protected void onPostExecute(List<Contact> userContacts) {
            // Now, you have the list of contacts for the specified user ID (userContacts)
            // Append each contact's information to the contactView with increased text size

            LinearLayout contactContainer = findViewById(R.id.contactContainer);
            contactContainer.removeAllViews(); // Clear existing views

            for (Contact c : userContacts) {
                View contactItemView = getLayoutInflater().inflate(R.layout.contact_item, null);
                TextView contactInfo = contactItemView.findViewById(R.id.contactInfo);
                ImageView editIcon = contactItemView.findViewById(R.id.editIcon);
                ImageView deleteIcon = contactItemView.findViewById(R.id.deleteIcon);
                ImageView viewIcon = contactItemView.findViewById(R.id.viewicon);

                // Set contact information
                contactInfo.setText(getFormattedContactText(c));

                // Set click listeners for edit and delete icons
                editIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle edit click
                        // You can open an edit activity or perform any other action
                        // based on the clicked contact
                    }
                });

                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle delete click
                        // You can show a confirmation dialog and delete the contact
                        // based on the clicked contact
                    }
                });

                viewIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                // Add the contact item view to the contact container
                contactContainer.addView(contactItemView);
            }
        }


        private String getFormattedContactText(Contact contact) {
            // Format the contact information
            return String.format("%s %s %s %s", contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber(),contact.getGender());
        }


    }}
