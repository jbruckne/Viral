package com.joebruckner.viral.serverTask;

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
public class StartSessionRequest extends AsyncTask<String, Integer, String> {
    Context context;
    Class className;
    String token;
    String tokenId;
    String timestamp;
    String userId;

    public StartSessionRequest(Context context, Class className) {
        this.context = context;
        this.className = className;
    }

    @Override
    protected String doInBackground(String... params) {
        setSessionData();

        Log.d("Session Data", "token: " + token + ". tokenId: " + tokenId + ". timestamp: " + timestamp + ". userId: " + userId);

        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("token", token));
        args.add(new BasicNameValuePair("tokenId", tokenId));
        args.add(new BasicNameValuePair("timestamp", timestamp));
        args.add(new BasicNameValuePair("userId", userId));

        String response = Server.makeRequest("new_session", args);

        Log.d("Response", response);

        return response;
    }

    private void setSessionData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        token = sp.getString("token", null);
        tokenId = sp.getString("tokenId", null);
        timestamp = sp.getString("timestamp", null);
        userId = sp.getString("userId", null);
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
                // Continue to the app
                Intent intent = new Intent(context, className);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Log.e("Response Error" , e);
            }
        } catch(JSONException e) {
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
