package com.joebruckner.viral.startup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.joebruckner.viral.Homepage;
import com.joebruckner.viral.R;
import com.joebruckner.viral.object.User;
import com.joebruckner.viral.serverTask.StartSessionRequest;


public class SplashScreen extends Activity {

    private static final int SPLASH_TIME = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        User currentUser = new User(this);
        if(currentUser.getUserId() != null) {
            StartSessionRequest request = new StartSessionRequest(this, Homepage.class);
            request.execute();
        } else {
            Intent loginIntent = new Intent(this, Login.class);
            startActivity(loginIntent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen, menu);
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
