package com.example.myapplication;
import android.content.Context;
import android.os.AsyncTask;

public class DeleteContactAsyncTask extends AsyncTask<Long, Void, Void> {

    private Context context;

    // Constructor to receive the context
    public DeleteContactAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Long... params) {
        long contactId = params[0];
        // Access the database and delete the contact
        AppDatabase database = AppDatabase.getInstance(context);
        database.contactDao().deleteContact(contactId);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {


    }
}
