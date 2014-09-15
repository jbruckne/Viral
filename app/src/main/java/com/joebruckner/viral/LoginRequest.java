package com.joebruckner.viral;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joebruckner on 9/15/14.
 */
public class LogInRequest extends AsyncTask<String, Integer, String> {
    Context context;

    public LogInRequest(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        // Get login info
        String username = params[0];
        String password = params[1];

        // Set login arguments
        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("username", username));
        args.add(new BasicNameValuePair("password", password));

        // Send the request to the server
        String response = Server.makeRequest("log_in", args);

        return response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject response = new JSONObject(s);
            String e = response.getString("error");

            if(e.equals("null")) {
                saveSession(response);
            } else {
                Log.e("Response Error", e);
            }
        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
        }
    }

    private void saveSession(JSONObject response) {
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("token", response.getString("token"));
            editor.putString("tokenId", response.getString("tokenId"));
            editor.putString("timestamp", response.getString("timestamp"));
            editor.putString("userId", response.getString("userID"));
            editor.commit();
        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
