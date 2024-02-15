package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GenderizeAsyncTask extends AsyncTask<String, Void, String> {

    private static final String API_URL = "https://api.genderize.io/?name=";

    // Interface to communicate the result back to the calling activity or fragment
    public interface GenderizeCallback {
        void onGenderFetched(String gender);
    }

    private final GenderizeCallback callback;

    public GenderizeAsyncTask(GenderizeCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String name = params[0];
        String apiUrl = API_URL + name;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e("GenderizeAsyncTask", "Error fetching gender", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                // Parse the JSON response
                JSONObject jsonObject = new JSONObject(result);
                String gender = jsonObject.optString("gender");
                callback.onGenderFetched(gender);
            } catch (JSONException e) {
                Log.e("GenderizeAsyncTask", "Error parsing JSON", e);
            }
        }
    }
}
