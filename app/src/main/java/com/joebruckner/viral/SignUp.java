package com.joebruckner.viral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends Activity {

    private EditText newUsername;
    private EditText newPassword;
    private EditText confirm;
    private Button   signUp;

    private Intent   intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set views
        newUsername = (EditText) findViewById(R.id.new_username);
        newPassword = (EditText) findViewById(R.id.new_password);
        confirm     = (EditText) findViewById(R.id.confirm_password);
        signUp      = (Button)   findViewById(R.id.sign_up);

        // Sign up the user and log them in
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = newUsername.getText().toString();
                String password = newPassword.getText().toString();

                if(password.equals(confirm.getText().toString())) {
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                intent = new Intent(getApplicationContext(), Homepage.class);
                                startActivity(intent);
                            } else {
                                Log.e("Parse", e.toString());
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),
                            "passwords must match", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
