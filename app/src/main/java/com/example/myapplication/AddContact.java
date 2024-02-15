package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContact extends AppCompatActivity {
    EditText firstName_txt, lastName_txt, phone_txt, company_txt, email_txt, address_txt;
    String contactName,firstName, lastName, phone, company, email, address,gender;
    public long uid;
    Button addContact;
    String API = "https://api.genderize.io/?name=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        firstName_txt = findViewById(R.id.first_name);
        lastName_txt = findViewById(R.id.last_name);
        phone_txt = findViewById(R.id.phone_number);
        company_txt = findViewById(R.id.company);
        email_txt = findViewById(R.id.email);
        address_txt = findViewById(R.id.address);
        addContact = findViewById(R.id.add);
        Intent intent = getIntent();
        if (intent != null) {
            uid = intent.getLongExtra("id", 0);
            Toast.makeText(AddContact.this, "uid is " + uid, Toast.LENGTH_SHORT).show();
        }

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstName_txt.getText().toString();
                lastName = lastName_txt.getText().toString();

                // Create and execute the AsyncTask to fetch gender
                GenderizeAsyncTask genderizeAsyncTask = new GenderizeAsyncTask(new GenderizeAsyncTask.GenderizeCallback() {
                    @Override
                    public void onGenderFetched(String fetchedGender) {
                        // Now you have the gender, use it as needed
                        gender = fetchedGender;
                        Toast.makeText(AddContact.this,"it is a"+gender,Toast.LENGTH_SHORT).show();
                        Contact newContact = new Contact(firstName, lastName, phone, company,email, address, gender, uid);

                        // Insert the contact using AsyncTask
                        new InsertContactAsyncTask().execute(newContact);
                    }
                });

                genderizeAsyncTask.execute(firstName);

                phone = phone_txt.getText().toString();
                company = company_txt.getText().toString();
                email = email_txt.getText().toString();
                address = address_txt.getText().toString();
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
            // Handle the result, e.g., show a Toast message
            if (contactId > 0) {
                Toast.makeText(AddContact.this, "Contact successfully added with id " + contactId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddContact.this,UserContacts.class);
                intent.putExtra("id",uid);
                startActivity(intent);
                // Add any other UI updates or navigation code if needed
            } else {
                Toast.makeText(AddContact.this, "Failed to add contact", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
