package com.joebruckner.viral;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joebruckner on 9/15/14.
 */
public class StartSessionRequest extends AsyncTask<String, Integer, String> {
    Context context;

    public StartSessionRequest(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String token = params[0];
        String tokenId = params[1];
        String timestamp = params[2];
        String userId = params[3];

        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("token", token));
        args.add(new BasicNameValuePair("tokenId", tokenId));
        args.add(new BasicNameValuePair("timestamp", timestamp));
        args.add(new BasicNameValuePair("userId", userId));

        String response = Server.makeRequest("start_session", args);

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
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
