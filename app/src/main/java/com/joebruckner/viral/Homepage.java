package com.joebruckner.viral;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.joebruckner.viral.object.User;
import com.joebruckner.viral.startup.Login;


public class Homepage extends Activity {

    private ListView          listView;
    private PostsAdapter      adapter;
    private Dialog            dialog;
    private EditText          friendUsername;
    private Button            sendRequest;
    private Button            cancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

    }

    private void updateItems() {

    }

    private void setListView() {

    }

    // Add the user's to each others contacts or just delete the request
    public void answerRequest(final int pos, final String function) {

    }

    // Open the post into full screen
    public void openPost() {

    }

    // Open a dialog to create and send a request
    public void requestDialog() {


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
                User currentUser = new User(this);
                currentUser.logout(Login.class);
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

    }
}
