package com.joebruckner.viral.serverTasks;

import android.content.Context;
import android.content.Intent;
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
    Class className;

    public LogInRequest(Context context, Class className) {
        this.context = context;
        this.className = className;
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

        return Server.makeRequest("log_in", args);
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
                // Continue to the app
                Intent intent = new Intent(context, className);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
            editor.putString("userId", response.getString("userId"));
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
