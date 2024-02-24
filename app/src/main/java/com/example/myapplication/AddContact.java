package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddContact extends AppCompatActivity {
    private EditText firstNameTxt, lastNameTxt, phoneTxt, companyTxt, emailTxt, addressTxt;
    private String firstName, lastName, phone, company, email, address, gender;
    private long uid;
    private Button addContact;
    private String API = "https://api.genderize.io/?name=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialize UI elements
        firstNameTxt = findViewById(R.id.first_name);
        lastNameTxt = findViewById(R.id.last_name);
        phoneTxt = findViewById(R.id.phone_number);
        companyTxt = findViewById(R.id.company);
        emailTxt = findViewById(R.id.email);
        addressTxt = findViewById(R.id.address);
        addContact = findViewById(R.id.add);

        // Get user ID from the intent
        Intent intent = getIntent();
        if (intent != null) {
            uid = intent.getLongExtra("id", 0);
        }

        // Set click listener for the "Add Contact" button
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input values
                firstName = firstNameTxt.getText().toString();
                lastName = lastNameTxt.getText().toString();
                phone = phoneTxt.getText().toString();
                company = companyTxt.getText().toString();
                email = emailTxt.getText().toString();
                address = addressTxt.getText().toString();

                // Check for internet connectivity before executing the AsyncTask
                if (isNetworkAvailable()) {
                    // Create and execute the AsyncTask to fetch gender
                    GenderizeAsyncTask genderizeAsyncTask = new GenderizeAsyncTask(new GenderizeAsyncTask.GenderizeCallback() {
                        @Override
                        public void onGenderFetched(String fetchedGender) {
                            gender = fetchedGender;
                            // Create a new contact object
                            Contact newContact = new Contact(firstName, lastName, phone, company, email, address, gender, uid);

                            // Insert the contact using AsyncTask
                            new InsertContactAsyncTask().execute(newContact);
                        }
                    });

                    // Execute gender AsyncTask
                    genderizeAsyncTask.execute(firstName);
                } else {
                    // No internet connectivity, create the contact without setting gender
                    Contact newContact = new Contact(firstName, lastName, phone, company, email, address, null, uid);

                    // Insert the contact using AsyncTask
                    new InsertContactAsyncTask().execute(newContact);
                }
            }
        });
    }

    // AsyncTask for inserting the contact
    private class InsertContactAsyncTask extends AsyncTask<Contact, Void, Long> {
        @Override
        protected Long doInBackground(Contact... contacts) {
            // Access the database and perform the insert operation
            AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            return database.contactDao().insert(contacts[0]);
        }

        @Override
        protected void onPostExecute(Long contactId) {
            if (contactId > 0) {
                Intent intent = new Intent(AddContact.this, UserContacts.class);
                Toast.makeText(AddContact.this,"uid before move is "+uid,Toast.LENGTH_SHORT).show();
                intent.putExtra("id", uid);
                startActivity(intent);
                // Add any other UI updates or navigation code if needed
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }
}
