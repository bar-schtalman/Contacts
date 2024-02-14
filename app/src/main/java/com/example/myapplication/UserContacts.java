package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class UserContacts extends AppCompatActivity {
    Button add, logout,sort;
    long uid;
    int sortOp = 1;
    private ContactDao contactDao;
    //TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_contacts);
        add = findViewById(R.id.add);
        sort = findViewById(R.id.sort);
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

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                PopupMenu popupMenu = new PopupMenu(context, sort);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.sort_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle menu item click with if statements
                        if (item.getItemId() == R.id.sortByFirstName) {
                            sortOp = 1;
                            new RetrieveContactsAsyncTask().execute(uid);
                            return true;
                        }

                        if (item.getItemId() == R.id.sortByLastName) {
                            sortOp = 2;
                            new RetrieveContactsAsyncTask().execute(uid);
                            return true;
                        }

                        if (item.getItemId() == R.id.sortByPhoneNumber) {
                            sortOp = 3;
                            new RetrieveContactsAsyncTask().execute(uid);
                            return true;
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }


    private class RetrieveContactsAsyncTask extends AsyncTask<Long, Void, List<Contact>> {
        @Override
        protected List<Contact> doInBackground(Long... params) {
            long userId = params[0];
            if(sortOp == 1 ) {
                return contactDao.getContactsForUserByFirstName(userId);
            }
            else if(sortOp == 2){
                return contactDao.getContactsForUserByLastName(userId);
                }
            else{
                return contactDao.getContactsForUserByPhoneNumber(userId);
            }
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
                ImageView phoneIcon = contactItemView.findViewById(R.id.phoneIcon);
                ImageView smsIcon = contactItemView.findViewById(R.id.smsIcon);

                // Set contact information
                contactInfo.setText(getFormattedContactText(c));

                contactItemView.setTag(c.getId());

                // Add click listener to the contact name (contactInfo)
                contactInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toggle visibility of icons when contact name is clicked
                        toggleIconsVisibility(contactItemView);
                    }
                });
                // Set click listeners for edit and delete icons
                editIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle edit click
                        // You can open an edit activity or perform any other action
                        // based on the clicked contact
                        Intent intent = new Intent(UserContacts.this,EditContact.class);
                        intent.putExtra("contact",c);
                        startActivity(intent);
                    }
                });

                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long contactId = (long) contactItemView.getTag();
                        new DeleteContactAsyncTask().execute(contactId);                    }
                });

                viewIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showViewContactDialog(c);
                    }
                });

                phoneIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent dialerIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + c.phoneNumber));
                        startActivity(dialerIntent);
                    }
                });

                smsIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + c.phoneNumber));
                        startActivity(smsIntent);
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


    }



    private void showViewContactDialog(Contact contact) {
        // Create a custom dialog
        Dialog dialog = new Dialog(UserContacts.this);
        dialog.setContentView(R.layout.dialog_view_contact);

        // Initialize UI elements in the dialog
        TextView firstNameTextView = dialog.findViewById(R.id.dialogFirstName);
        TextView lastNameTextView = dialog.findViewById(R.id.dialogLastName);
        TextView phoneNumberTextView = dialog.findViewById(R.id.dialogPhoneNumber);
        TextView emailTextView = dialog.findViewById(R.id.dialogEmail);
        TextView companyTextView = dialog.findViewById(R.id.dialogCompany);
        TextView addressTextView = dialog.findViewById(R.id.dialogAddres);
        TextView genderTextView = dialog.findViewById(R.id.dialogGender);

        // Set values for UI elements based on the contact object
        firstNameTextView.setText("First Name: " + contact.getFirstName());
        lastNameTextView.setText("Last Name: " + contact.getLastName());
        phoneNumberTextView.setText("Phone Number: " + contact.getPhoneNumber());
        emailTextView.setText("Email: " + contact.getEmail());
        companyTextView.setText("Company: " + contact.getCompanyName());
        addressTextView.setText("Address: " + contact.getAddress());
        genderTextView.setText("Gender: " + contact.getGender());

        // Show the dialog
        dialog.show();
    }



    private void toggleIconsVisibility(View clickedContactItem) {
        // Find the icons in the clicked contact item
        ImageView editIcon = clickedContactItem.findViewById(R.id.editIcon);
        ImageView deleteIcon = clickedContactItem.findViewById(R.id.deleteIcon);
        ImageView viewIcon = clickedContactItem.findViewById(R.id.viewicon);
        ImageView phoneIcon = clickedContactItem.findViewById(R.id.phoneIcon);
        ImageView smsIcon = clickedContactItem.findViewById(R.id.smsIcon);

        // Toggle visibility of icons
        int visibility = (editIcon.getVisibility() == View.VISIBLE) ? View.INVISIBLE : View.VISIBLE;
        editIcon.setVisibility(visibility);
        deleteIcon.setVisibility(visibility);
        viewIcon.setVisibility(visibility);
        phoneIcon.setVisibility(visibility);
        smsIcon.setVisibility(visibility);
    }

    private class DeleteContactAsyncTask extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... params) {
            long contactId = params[0];
            // Access the database and delete the contact
            AppDatabase database = AppDatabase.getInstance(getApplicationContext());
            database.contactDao().deleteContact(contactId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Handle any post-delete operations
            new RetrieveContactsAsyncTask().execute(uid);
        }
    }


}
