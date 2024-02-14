package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditContact extends AppCompatActivity {
    EditText firstName_txt, lastName_txt, phone_txt, company_txt, email_txt, address_txt, gender_txt;
    Button saveBtn;
    ContactDao contactDao;
    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        firstName_txt = findViewById(R.id.first_name);
        lastName_txt = findViewById(R.id.last_name);
        phone_txt = findViewById(R.id.phone_number);
        company_txt = findViewById(R.id.company);
        email_txt = findViewById(R.id.email);
        address_txt = findViewById(R.id.address);
        gender_txt = findViewById(R.id.gender);
        saveBtn = findViewById(R.id.saveBTN);

        contactDao = AppDatabase.getInstance(getApplicationContext()).contactDao(); // Assuming AppDatabase is the database instance

        Intent intent = getIntent();
        if (intent != null) {
            contact = (Contact) intent.getSerializableExtra("contact");
            if (contact != null) {
                firstName_txt.setText(contact.getFirstName());
                lastName_txt.setText(contact.getLastName());
                phone_txt.setText(contact.getPhoneNumber());
                company_txt.setText(contact.getCompanyName());
                email_txt.setText(contact.getEmail());
                address_txt.setText(contact.getAddress());
                gender_txt.setText(contact.getGender());
            }
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact.editContact(firstName_txt.getText().toString(),lastName_txt.getText().toString(),
                        phone_txt.getText().toString(), address_txt.getText().toString(),company_txt.getText().toString(),
                        email_txt.getText().toString(),gender_txt.getText().toString());

                new EditContactAsyncTask(contactDao).execute(contact);
                Intent intent = new Intent(EditContact.this,UserContacts.class);
                intent.putExtra("id",contact.userId);
                startActivity(intent);
            }
        });
    }

    private static class EditContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDao contactDao;

        EditContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            try {
                contactDao.editContact(contacts[0]);
            } catch (Exception e) {
                // Log any exceptions that occur during the database operation
                e.printStackTrace();
            }
            return null;
        }
    }
}
