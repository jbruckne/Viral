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
public class SignUpRequest extends AsyncTask<String, Integer, String> {
    Context context;

    public SignUpRequest(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("username", username));
        args.add(new BasicNameValuePair("password", password));

        String response = Server.makeRequest("sign_up", args);

        return response;

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
