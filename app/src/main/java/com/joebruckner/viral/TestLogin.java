package com.joebruckner.viral;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class TestLogin extends Activity {

    EditText usernameText;
    EditText passwordText;
    Button login;
    Button signup;

    String username;
    String password;
    String token;
    String tokenId;
    String timestamp;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);

        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        login  = (Button) findViewById(R.id.login_button);
        signup = (Button) findViewById(R.id.signup_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for current user logged in or login with info given
                if(checkCurrentUser()) {
                    getSessionToken();
                    ServerTask task = new ServerTask(getApplicationContext(), "start_session");
                    // TODO
                } else {
                    username = usernameText.getText().toString();
                    password = passwordText.getText().toString();
                }

                // Check for login errors
                if(username.isEmpty() || password.isEmpty()) {
                    showError(0);
                    return;
                }

                // Log the user in
                ServerTask task = new ServerTask(getApplicationContext(), "log_in");
                task.execute(username, password);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Sign in user with info given
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();

                // Check for signup errors
                if(username.isEmpty() || password.isEmpty()) {
                    showError(0);
                    return;
                } else if(username.length() < 6 || password.length() < 6) {
                    showError(1);
                    return;
                }

                // Sign the user in
                ServerTask task = new ServerTask(getApplicationContext(), "sign_up");
                task.execute(username, password);
            }
        });

    }

    private boolean checkCurrentUser() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean("currentUserSet", false);
    }

    private void getSessionToken() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        token = sp.getString("token", null);
        tokenId = sp.getString("tokenId", null);
        timestamp = sp.getString("timestamp", null);
        userId = sp.getString("userId", null);
    }

    /*
     * Display login/signup errors
     * 0: Didn't fill in all prompts
     * 1: New username or password is too small
     */
    public void showError(int e) {
        String errorMessage;
        switch(e) {
            case 0:
                errorMessage = "Must fill in all prompts.";
                break;
            case 1:
                errorMessage = "Username/Password must be at least 6 characters long";
                break;
            default:
                errorMessage = "Error";
        }

        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_login, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
