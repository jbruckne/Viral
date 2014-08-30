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

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Homepage extends Activity {

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

        items = new ArrayList<ParseObject>();
        getRequests();
    }

    // ---INITIAL SETUP METHODS---

    private void getRequests() {
        // Get all the friend requests for the user
        ParseQuery<ParseObject> requestQuery = ParseQuery.getQuery("Request");
        requestQuery.whereEqualTo("to", ParseUser.getCurrentUser().getObjectId());
        requestQuery.include("from");
        requestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null) {
                    items.addAll(parseObjects);
                    getContacts();
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });
    }

    private void getContacts() {
        // Get all of the user's contacts
        ParseRelation<ParseObject> contactsRelation = ParseUser.getCurrentUser().getRelation("contacts");
        ParseQuery<ParseObject> contactQuery = contactsRelation.getQuery();
        contactQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null) {
                    contacts = parseObjects;
                    getPosts();
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });
        
    }

    private void getPosts() {
        ArrayList<String> contactIds = new ArrayList<String>();
        for(ParseObject contact : contacts) {
            contactIds.add(contact.getString("contactId"));
        }

        // Get all the posts from the users friends
        ParseQuery<ParseObject> postQuery = ParseQuery.getQuery("Post");
        postQuery.whereContainedIn("User", contactIds);
        postQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    items.addAll(parseObjects);
                    setListView();
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });
    }

    private void setListView() {

        // Set adapter to the List
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

    // ---END OF INITIAL SETUP METHODS---

    // Add the user's to each others contacts or just delete the request
    public void answerRequest(final int pos, String function) {
        String requestId = items.get(pos).getObjectId();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("requestId", requestId);
        ParseCloud.callFunctionInBackground(function, params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if(e == null) {
                    Log.v("Parse Cloud", o.toString());

                    //items.remove(pos);
                    adapter.removeItem(pos);
                } else {
                    Log.e("Parse Cloud", e.toString());
                }
            }
        });
    }

    // Open the post into full screen
    public void openPost(ParseObject post) {
        ParseFile file = post.getParseFile("Image");
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
                sendRequest();
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

    // Save the request
    public void sendRequest() {
        // create the request
        final ParseObject object = new ParseObject("Request");
        object.put("from", ParseUser.getCurrentUser());

        // Find the id of the friend the user is requesting
        ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
        friendQuery.whereEqualTo("username", friendUsername.getText().toString());
        friendQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null) {
                    object.put("to", parseUsers.get(0).getObjectId());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Log.d("Parse", object.toString());
                            } else {
                                Log.e("Parse", e.toString());
                            }
                        }
                    });
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_new) {
            Intent intent = new Intent(this, NewPost.class);
            startActivity(intent);
        } else if(id == R.id.action_add_friend) {
            requestDialog();
        } else if(id == R.id.action_logout) {
            // Log the user out
            ParseUser.logOut();
            Intent intent = new Intent(this, Login.class);
            finish();
            startActivity(intent);
        } else if(id == R.id.action_test) {
            test();
        }
        return super.onOptionsItemSelected(item);
    }

    public void test() {
        ParseUser user = ParseUser.getCurrentUser();
        Log.v("User", "Logged in as " + user.getUsername());
    }
}
