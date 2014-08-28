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

import com.parse.DeleteCallback;
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

    private void getRequests() {
        // Get all the friend requests for the user
        ParseQuery<ParseObject> requestQuery = ParseQuery.getQuery("Request");
        requestQuery.whereEqualTo("idTo", ParseUser.getCurrentUser().getObjectId());
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
            contactIds.add(contact.getString("id"));
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
                acceptRequest(pos);
                deleteRequest(pos);
            }
        });
        adapter.setDeclineListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = ((PostsAdapter.ButtonInfo) v.getTag()).pos;
                deleteRequest(pos);
            }
        });
        listView = (ListView) findViewById(R.id.recents);
        listView.setAdapter(adapter);

        // Open the contact request or image post
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject object = items.get(position);
                if (object.getClassName().equals("Request")) {
                    openRequest(object);
                } else if (object.getClassName().equals("Post")) {
                    openPost(object);
                } else {
                    Log.e("List", "ParseObject is neither Request or Post");
                }
            }
        });
    }

    public void openRequest(ParseObject request) {
        Intent intent = new Intent(getApplicationContext(), viewRequest.class);
        intent.putExtra("CONTACT_NAME", request.getString("nameFrom"));
        intent.putExtra("CONTACT_ID", request.getString("idFrom"));
        startActivity(intent);
    }

    public void acceptRequest(int pos) {
        final ParseObject request = items.get(pos);
        final ParseObject contact = new ParseObject("Contact");
        contact.put("id", request.getString("idFrom"));
        contact.put("name", request.getString("nameFrom"));
        contact.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.v("Parse", "Saved contact object");
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseRelation<ParseObject> contactsRelation = user.getRelation("contacts");
                    contactsRelation.add(contact);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                query.whereEqualTo("objectId", request.getString("idFrom"));
                                query.findInBackground(new FindCallback<ParseUser>() {
                                    @Override
                                    public void done(List<ParseUser> parseUsers, ParseException e) {
                                        if(e == null) {

                                        } else {
                                            Log.e("Parse", e.toString());
                                        }
                                    }
                                });

                                Log.v("Parse", "Successfully added new contact");
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

    public void deleteRequest(int pos) {
        final int position = pos;
        ParseObject request = items.get(position);
        request.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    items.remove(position);
                    adapter.removeItem(position);
                    Log.v("Parse", "Successfully removed request");
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });
    }

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

    public void sendRequest() {
        // Set up the request
        ParseUser user = ParseUser.getCurrentUser();
        final ParseObject object = new ParseObject("Request");
        object.put("idFrom", (String) user.getObjectId());
        object.put("nameFrom", (String) user.getUsername());

        // Find the id of the friend the user is requesting
        ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
        friendQuery.whereEqualTo("username", friendUsername.getText().toString());
        friendQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null) {
                    object.put("idTo", (String) parseUsers.get(0).getObjectId());
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
        /*ParseObject friendRequest = new ParseObject("testRequest");
        friendRequest.put("from", ParseUser.getCurrentUser());
        friendRequest.put("to", "QeTlmFIobV");
        friendRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.v("Parse", "Done saving");
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });*/

        /*ParseQuery<ParseObject> query = ParseQuery.getQuery("testRequest");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null) {
                    String requestId = parseObjects.get(0).getObjectId();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("test", requestId);
                    ParseCloud.callFunctionInBackground("acceptRequest", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String result, ParseException e) {
                            if (e == null) {
                                Log.v("Parse", result);
                            } else {
                                Log.e("Parse", e.toString());
                            }
                        }
                    });
                }
            }
        });*/

        HashMap<String, Object> params = new HashMap<String, Object>();
        ParseCloud.callFunctionInBackground("hello", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException e) {

                if(e == null) {
                    Log.v("Parse", result);
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });

    }
}
