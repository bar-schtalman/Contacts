package com.example.myapplication;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserContacts extends AppCompatActivity {
    private Button add, logout, sort, change_details;
    private long uid;
    private int sortOp = 1;
    private ContactDao contactDao;
    private List<String> selectedDetails;
    private LinearLayout contactContainer;  // Add this member variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_contacts);



        // Initialize UI elements
        add = findViewById(R.id.add);
        sort = findViewById(R.id.sort);
        selectedDetails = getDefaultSelectedDetails();
        change_details = findViewById(R.id.changeDetails);
        contactContainer = findViewById(R.id.contactContainer);  // Initialize contactContainer

        // Get user ID from the intent
        Intent intent = getIntent();
        if (intent != null) {
            uid = intent.getLongExtra("id", 0);
        }

        // Initialize database and retrieve contacts
        AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        contactDao = appDatabase.contactDao();
        new RetrieveContactsAsyncTask(UserContacts.this,getApplicationContext(), sortOp, contactContainer,selectedDetails,uid).execute(uid);
        Toast.makeText(UserContacts.this,"uid is "+uid,Toast.LENGTH_SHORT).show();

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
                popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle menu item click with if statements
                        if (item.getItemId() == R.id.sortByFirstName) {
                            sortOp = 1;
                            new RetrieveContactsAsyncTask(UserContacts.this,getApplicationContext(), sortOp, contactContainer,selectedDetails,uid).execute(uid);
                            return true;
                        }

                        if (item.getItemId() == R.id.sortByLastName) {
                            sortOp = 2;
                            new RetrieveContactsAsyncTask(UserContacts.this,getApplicationContext(), sortOp, contactContainer,selectedDetails,uid).execute(uid);
                            return true;
                        }

                        if (item.getItemId() == R.id.sortByPhoneNumber) {
                            sortOp = 3;
                            new RetrieveContactsAsyncTask(UserContacts.this,getApplicationContext(), sortOp, contactContainer,selectedDetails,uid).execute(uid);
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

        Button btnApply = dialogView.findViewById(R.id.apply);

        // Set the initial state of checkboxes based on selectedDetails
        Map<CheckBox, String> checkBoxMap = new HashMap<>();

        checkBoxMap.put(checkBoxFullName, "first_name");
        checkBoxMap.put(checkBoxPhoneNumber, "phone_number");
        checkBoxMap.put(checkBoxEmail, "email");
        checkBoxMap.put(checkBoxCompany, "company_name");
        checkBoxMap.put(checkBoxAddress, "address");
        checkBoxMap.put(checkGender, "gender");

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
                for (Map.Entry<CheckBox, String> entry : checkBoxMap.entrySet()) {
                    if (entry.getKey().isChecked()) {
                        selectedDetails.add(entry.getValue());
                    }
                }

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
        new RetrieveContactsAsyncTask(UserContacts.this,getApplicationContext(), sortOp, contactContainer,selectedDetails,uid).execute(uid);
    }
    // Method to retrieve default selected details
    private List<String> getDefaultSelectedDetails() {
        List<String> defaultDetails = new ArrayList<>();
        defaultDetails.add("first_name");
        defaultDetails.add("last_name");
        defaultDetails.add("phone_number");
        return defaultDetails;
    }
}
