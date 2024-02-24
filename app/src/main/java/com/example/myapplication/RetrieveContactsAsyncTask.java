package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RetrieveContactsAsyncTask extends AsyncTask<Long, Void, List<Contact>> {
    private Context context;
    private int sortOp;
    private LinearLayout contactContainer;
    private UserContacts userContacts;
    private List<String> selectedDetails;
    private Activity activity;

    private long uid;

    // Constructor to receive the context, sortOp, and UI elements
    public RetrieveContactsAsyncTask(Activity activity,Context context, int sortOp,LinearLayout contactContainer,List<String> selectedDetails,long uid) {
        this.context = context;
        this.sortOp = sortOp;
        this.contactContainer = contactContainer;
        this.selectedDetails = selectedDetails;
        this.uid = uid;
        this.activity = activity;
    }


    @Override
    protected List<Contact> doInBackground(Long... params) {
        long userId = params[0];
        // Access the database and retrieve contacts
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        ContactDao contactDao = appDatabase.contactDao();
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

        contactContainer.removeAllViews(); // Clear existing views

        for (Contact c : userContacts) {
            appendContact(this.contactContainer, c, selectedDetails);
        }
    }



    private void showViewContactDialog(Contact contact) {
            // Create a custom dialog
            Dialog dialog = new Dialog(activity);
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

            // Show the dialog only if the activity is still running
        dialog.show();
    }



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


    public class DeleteContactAsyncTask extends AsyncTask<Long, Void, Void> {
        private Context context;

        // Constructor to receive the context
        public DeleteContactAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Long... params) {
            long contactId = params[0];
            AppDatabase database = AppDatabase.getInstance(context);
            database.contactDao().deleteContact(contactId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            new RetrieveContactsAsyncTask( activity,context, sortOp, contactContainer, selectedDetails, uid).execute(uid);

        }
    }

    private void appendContact(ViewGroup contactContainer, Contact c, List<String> selectedDetails) {

        // Inflate the contact item view
        View contactItemView = LayoutInflater.from(context).inflate(R.layout.contact_item, null);

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
                // Log.d("UserContacts", "inside contact image click listener");
                toggleIconsVisibility(contactItemView);
            }
        });

        // Set click listeners for edit and delete icons
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditContact.class);
                intent.putExtra("contact", c);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete click
                long contactId = (long) contactItemView.getTag();
                new DeleteContactAsyncTask(context).execute(contactId);
            }
        });

        // Set click listeners for phone, SMS, and view icons
        phoneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialerIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + c.getPhoneNumber()));
                dialerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialerIntent);
            }
        });

        smsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + c.getPhoneNumber()));
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
            }
        });

        viewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog with detailed contact information
                Toast.makeText(context,"pressed View icon",Toast.LENGTH_SHORT).show();
                showViewContactDialog(c);
            }
        });

        contactContainer.addView(contactItemView);


    }


}
