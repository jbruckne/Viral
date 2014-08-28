package com.joebruckner.viral;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class viewRequest extends Activity {

    private TextView contactNameView;
    private String   contactName;
    private String   contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        // Set Views
        contactNameView = (TextView) findViewById(R.id.contact_name);

        // Get the requester's contact info
        contactName = getIntent().getStringExtra("CONTACT_NAME");
        contactId   = getIntent().getStringExtra("CONTACT_ID");
        contactNameView.setText(contactName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_request, menu);
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
