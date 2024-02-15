package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class UserContacts extends AppCompatActivity {
    // UI elements
    private Button add, logout, sort, change_details;
    private long uid;
    private int sortOp = 1;
    private ContactDao contactDao;

    private List<String> selectedDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_contacts);

        // Initialize UI elements
        add = findViewById(R.id.add);
        sort = findViewById(R.id.sort);
        selectedDetails = getDefaultSelectedDetails();
        change_details = findViewById(R.id.changeDetails);

        // Get user ID from the intent
        Intent intent = getIntent();
        if (intent != null) {
            uid = intent.getLongExtra("id", 0);
        }

        // Initialize database and retrieve contacts
        AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        contactDao = appDatabase.contactDao();
        new RetrieveContactsAsyncTask().execute(uid);

        // Set click listeners for buttons
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the login activity on logout
                Intent intent = new Intent(UserContacts.this, Login.class);
                startActivity(intent);
            }
        });

        change_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for changing details
                onChangeDetailsButtonClick(view);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the add contact activity
                Intent intent = new Intent(UserContacts.this, AddContact.class);
                intent.putExtra("id", uid);
                startActivity(intent);
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a popup menu for sorting options
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

    // Method to handle the click event for changing details
    private void onChangeDetailsButtonClick(View view) {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.change_details_dialog, null);

        // Find checkboxes and button in the dialog
        CheckBox checkBoxFullName = dialogView.findViewById(R.id.checkBoxName);
        CheckBox checkBoxPhoneNumber = dialogView.findViewById(R.id.checkBoxPhoneNumber);
        CheckBox checkBoxCompany = dialogView.findViewById(R.id.checkBoxCompany);
        CheckBox checkBoxAddress = dialogView.findViewById(R.id.checkBoxAddress);
        CheckBox checkBoxEmail = dialogView.findViewById(R.id.checkBoxEmail);
        CheckBox checkGender = dialogView.findViewById(R.id.checkBoxGender);

        // Find other checkboxes...
        Button btnApply = dialogView.findViewById(R.id.apply);

        // Set the initial state of checkboxes based on selectedDetails
        checkBoxFullName.setChecked(selectedDetails.contains("first_name"));
        checkBoxPhoneNumber.setChecked(selectedDetails.contains("phone_number"));
        checkBoxCompany.setChecked(selectedDetails.contains("company_name"));
        checkBoxEmail.setChecked(selectedDetails.contains("email"));
        checkBoxAddress.setChecked(selectedDetails.contains("address"));
        checkGender.setChecked(selectedDetails.contains("gender"));

        // Set other checkboxes...

        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set onClickListener for the Apply button
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update selectedDetails based on the checkboxes
                selectedDetails.clear();
                if (checkBoxFullName.isChecked()) {
                    selectedDetails.add("first_name");
                }
                if (checkBoxPhoneNumber.isChecked()) {
                    selectedDetails.add("phone_number");
                }
                if (checkBoxEmail.isChecked()) {
                    selectedDetails.add("email");
                }
                if (checkBoxCompany.isChecked()) {
                    selectedDetails.add("company_name");
                }
                if (checkBoxAddress.isChecked()) {
                    selectedDetails.add("address");
                }
                if (checkGender.isChecked()) {
                    selectedDetails.add("gender");
                }
                // Add other details...

                // Update the UI with selected details
                updateUIWithSelectedDetails();

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    // Method to update the UI with selected details
    private void updateUIWithSelectedDetails() {
        // Get the contacts and update the UI
        new RetrieveContactsAsyncTask().execute(uid);
    }

    // Method to retrieve default selected details
    private List<String> getDefaultSelectedDetails() {
        List<String> defaultDetails = new ArrayList<>();
        defaultDetails.add("first_name");
        defaultDetails.add("last_name");
        defaultDetails.add("phone_number");
        return defaultDetails;
    }

    // AsyncTask to retrieve contacts from the database
    private class RetrieveContactsAsyncTask extends AsyncTask<Long, Void, List<Contact>> {
        @Override
        protected List<Contact> doInBackground(Long... params) {
            long userId = params[0];
            if (sortOp == 1) {
                return contactDao.getContactsForUserByFirstName(userId);
            } else if (sortOp == 2) {
                return contactDao.getContactsForUserByLastName(userId);
            } else {
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
                // Inflate the contact item view
                View contactItemView = getLayoutInflater().inflate(R.layout.contact_item, null);

                // Find UI elements in the contact item view
                TextView contactName = contactItemView.findViewById(R.id.textName);
                TextView contactEmail = contactItemView.findViewById(R.id.textEmail);
                TextView contactAddress = contactItemView.findViewById(R.id.textAddress);
                TextView contactGender = contactItemView.findViewById(R.id.textGender);
                TextView contactCompany = contactItemView.findViewById(R.id.textCompany);
                TextView contactPhoneNumber = contactItemView.findViewById(R.id.textNumber);
                ImageView editIcon = contactItemView.findViewById(R.id.editIcon);
                ImageView deleteIcon = contactItemView.findViewById(R.id.deleteIcon);
                ImageView smsIcon = contactItemView.findViewById(R.id.smsIcon);
                ImageView phoneIcon = contactItemView.findViewById(R.id.phoneIcon);
                ImageView contactImage = contactItemView.findViewById(R.id.imageContact);
                ImageView viewIcon = contactItemView.findViewById(R.id.viewIcon);

                // Set contact information
                String name = c.getFirstName() + " " + c.getLastName();
                contactName.setText(name);
                contactEmail.setText(c.getEmail());
                contactAddress.setText(c.getAddress());
                contactGender.setText(c.getGender());
                contactCompany.setText(c.getCompanyName());
                contactPhoneNumber.setText(c.getPhoneNumber());

                // Set visibility based on selected details
                contactName.setVisibility(selectedDetails.contains("first_name") ? View.VISIBLE : View.GONE);
                contactEmail.setVisibility(selectedDetails.contains("email") ? View.VISIBLE : View.GONE);
                contactAddress.setVisibility(selectedDetails.contains("address") ? View.VISIBLE : View.GONE);
                contactGender.setVisibility(selectedDetails.contains("gender") ? View.VISIBLE : View.GONE);
                contactCompany.setVisibility(selectedDetails.contains("company_name") ? View.VISIBLE : View.GONE);
                contactPhoneNumber.setVisibility(selectedDetails.contains("phone_number") ? View.VISIBLE : View.GONE);

                // Set the tag for the contact item view
                contactItemView.setTag(c.getId());

                // Add click listener to the contact image
                contactImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("UserContacts", "inside contact image click listener");
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
                        Intent intent = new Intent(UserContacts.this, EditContact.class);
                        intent.putExtra("contact", c);
                        startActivity(intent);
                    }
                });

                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle delete click
                        long contactId = (long) contactItemView.getTag();
                        new DeleteContactAsyncTask().execute(contactId);
                    }
                });

                // Set click listeners for phone, SMS, and view icons
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

                viewIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Show a dialog with detailed contact information
                        showViewContactDialog(c);
                    }
                });

                // Add the contact item view to the contact container
                contactContainer.addView(contactItemView);
            }
        }
    }

    // Method to show a dialog with detailed contact information
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
        TextView addressTextView = dialog.findViewById(R.id.dialogAddress);
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

    // Method to toggle the visibility of icons in a contact item view
    private void toggleIconsVisibility(View clickedContactItem) {
        // Find the icons in the clicked contact item
        ImageView editIcon = clickedContactItem.findViewById(R.id.editIcon);
        ImageView deleteIcon = clickedContactItem.findViewById(R.id.deleteIcon);
        ImageView phoneIcon = clickedContactItem.findViewById(R.id.phoneIcon);
        ImageView smsIcon = clickedContactItem.findViewById(R.id.smsIcon);
        ImageView viewIcon = clickedContactItem.findViewById(R.id.viewIcon);

        // Toggle visibility of icons
        int visibility = (editIcon.getVisibility() == View.VISIBLE) ? View.INVISIBLE : View.VISIBLE;
        editIcon.setVisibility(visibility);
        deleteIcon.setVisibility(visibility);
        phoneIcon.setVisibility(visibility);
        smsIcon.setVisibility(visibility);
        viewIcon.setVisibility(visibility);
    }

    // AsyncTask to delete a contact from the database
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
