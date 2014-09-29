package com.joebruckner.viral.startup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.joebruckner.viral.Homepage;
import com.joebruckner.viral.R;
import com.joebruckner.viral.serverTask.LogInRequest;

public class Login extends Activity {

    private EditText usernameText;
    private EditText passwordText;
    private Button   loginButton;
    private Button   signUpButton;
    private Intent   intentLogin;
    private Intent   intentSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set Views
        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        loginButton  = (Button)   findViewById(R.id.login);
        signUpButton = (Button)   findViewById(R.id.start_sign_up);

        // Login user
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                Log.v("username", username);
                Log.v("password", password);

                LogInRequest request = new LogInRequest(getApplicationContext(), Homepage.class);
                request.execute(username, password);
            }
        });

        // Send user to the sign up page
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSignUp = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intentSignUp);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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
