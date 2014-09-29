package com.joebruckner.viral.startup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.joebruckner.viral.Homepage;
import com.joebruckner.viral.R;
import com.joebruckner.viral.serverTask.SignUpRequest;

public class SignUp extends Activity {

    private EditText newUsername;
    private EditText newPassword;
    private EditText confirmPass;
    private Button   signUp;
    private Intent   intent;

    final int MIN = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set views
        newUsername = (EditText) findViewById(R.id.new_username);
        newPassword = (EditText) findViewById(R.id.new_password);
        confirmPass = (EditText) findViewById(R.id.confirm_password);
        signUp      = (Button)   findViewById(R.id.sign_up);

        // Sign up the user and log them in
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = newUsername.getText().toString();
                String password = newPassword.getText().toString();
                String confirm  = confirmPass.getText().toString();

                if(username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    showError(0);
                    return;
                } else if(username.length() < MIN || password.length() < MIN) {
                    showError(1);
                    return;
                } else if(!password.equals(confirm)) {
                    showError(2);
                    return;
                }

                SignUpRequest request = new SignUpRequest(getApplicationContext(), Homepage.class);
                request.execute(username, password);
            }
        });
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
            case 2:
                errorMessage = "Passwords don't match";
            default:
                errorMessage = "Error";
        }

        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
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
