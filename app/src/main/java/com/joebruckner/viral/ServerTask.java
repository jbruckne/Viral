package com.joebruckner.viral;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joebruckner on 9/5/14.
 */
public class ServerTask extends AsyncTask {

    final String url = "http://web.ics.purdue.edu/~jbruckne/";

    public ServerTask() {

    }

    @Override
    protected Object doInBackground(Object[] params) {

        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("user", "joe"));

        String response = makeRequest("sign_in", args);

        Log.d("Response", response);

        return null;
    }

    private String makeRequest(String request, List<NameValuePair> args) {

        try {
            String requestUrl = url + request + ".php";

            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(requestUrl);
            post.setEntity(new UrlEncodedFormEntity(args));

            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();

            BufferedInputStream inputBuffer = new BufferedInputStream(stream);
            ByteArrayBuffer byteBuffer = new ByteArrayBuffer(20);

            int current = 0;
            while ((current = inputBuffer.read()) != -1) {
                byteBuffer.append(current);
            }

            String jsonText = new String(byteBuffer.toByteArray());
            return jsonText;

        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncodingException", e.toString());
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Object o) {
        super.onCancelled(o);
    }
}
