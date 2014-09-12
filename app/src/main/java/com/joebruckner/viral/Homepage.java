package com.joebruckner.viral;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Homepage extends Activity {

    private final int CONTACTS = 0;
    private final int POSTS    = 1;
    private final int REQUESTS = 2;

    private ListView          listView;
    private PostsAdapter      adapter;
    private List<ParseObject> items;
    private List<ParseObject> contacts;
    private Dialog            dialog;
    private EditText          friendUsername;
    private Button            sendRequest;
    private Button            cancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        updateItems();
    }

    private void updateItems() {

        ParseCloud.callFunctionInBackground("getItems", new HashMap<String, Object>(), new FunctionCallback<ArrayList<ArrayList<ParseObject>>>() {
            @Override
            public void done(ArrayList<ArrayList<ParseObject>> o, ParseException e) {
                if (e == null) {
                    // Make a new list of the updated items
                    items = new ArrayList<ParseObject>();
                    items.addAll(o.get(POSTS));
                    items.addAll(o.get(REQUESTS));
                    setListView();
                } else {
                    Log.e("Parse getItems", e.toString());
                }
            }
        });
    }

    private void setListView() {

        // Set adapters to the List
        adapter = new PostsAdapter(this, items);
        adapter.setAcceptListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = ((PostsAdapter.ButtonInfo) v.getTag()).pos;
                answerRequest(pos, "acceptRequest");
            }
        });
        adapter.setDeclineListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = ((PostsAdapter.ButtonInfo) v.getTag()).pos;
                answerRequest(pos, "declineRequest");
            }
        });

        listView = (ListView) findViewById(R.id.recents);
        listView.setAdapter(adapter);

        // Open the contact request or image post
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject object = items.get(position);
                if (object.getClassName().equals("Post")) {
                    openPost(object);
                } else {
                    Log.e("List", "ParseObject is neither Request or Post");
                }
            }
        });
    }

    // Add the user's to each others contacts or just delete the request
    public void answerRequest(final int pos, final String function) {
        String requestId = items.get(pos).getObjectId();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("requestId", requestId);
        ParseCloud.callFunctionInBackground(function, params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if (e == null) {
                    Log.v("Parse " + function, o.toString());

                    //items.remove(pos);
                    adapter.removeItem(pos);
                } else {
                    Log.e("Parse " + function, e.toString());
                }
            }
        });
    }

    // Open the post into full screen
    public void openPost(ParseObject post) {
        ParseFile file = post.getParseFile("image");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getApplicationContext(), viewPost.class);
                    intent.putExtra("BYTES", bytes);
                    startActivity(intent);
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });
    }

    // Open a dialog to create and send a request
    public void requestDialog() {

        // Custom dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.friend_request_dialog);
        dialog.setTitle("Add Friend");

        // Set dialog components
        friendUsername = (EditText) dialog.findViewById(R.id.friend_username);
        sendRequest    = (Button)   dialog.findViewById(R.id.send_request);
        cancelRequest  = (Button)   dialog.findViewById(R.id.cancel);

        // Send the request to the friend
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("to", friendUsername.getText().toString());
                ParseCloud.callFunctionInBackground("sendRequest", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object o, ParseException e) {
                        if(e == null) {
                            Toast.makeText(getApplicationContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Parse sendRequest", e.toString());
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        // Cancel the request
        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_new:
                Intent newIntent = new Intent(this, NewPost.class);
                startActivity(newIntent);
                break;
            case R.id.action_add_friend:
                requestDialog();
                break;
            case R.id.action_logout:
                ParseUser.logOut();
                Intent intent = new Intent(this, Login.class);
                finish();
                startActivity(intent);
                break;
            case R.id.action_refresh:
                break;
            case R.id.action_test:
                test();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void test() {
        ParseUser user = ParseUser.getCurrentUser();
        Log.v("User", "Logged in as " + user.getUsername());

        ServerTask task = new ServerTask();
        task.execute();
    }
}
