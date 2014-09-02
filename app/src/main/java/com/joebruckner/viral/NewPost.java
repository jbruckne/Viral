package com.joebruckner.viral;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class NewPost extends Activity {

    static final int REQUEST_TAKE_PHOTO = 1;

    Button test;
    ImageView imageView;

    byte[] byteArray;
    ParseFile imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        imageView = (ImageView) findViewById(R.id.imageView);
        test      = (Button) findViewById(R.id.test);

        // Send user to camera app to take a pic
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            // Get the image data from the camera intent
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            // Set the image as the full layout
            ViewGroup layout = (ViewGroup) test.getParent();
            if(layout != null) layout.removeView(test);
            imageView.setImageBitmap(bitmap);

            // Convert the image into savable data for Parse
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byteArray = stream.toByteArray();

            // Save the image data to parse
            imageFile = new ParseFile("post.png", byteArray);
            imageFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        Log.v("Parse", "Done uploading image");
                        recordPost();
                    } else {
                        Log.e("Parse", e.toString());
                    }
                }
            });
        }
    }

    public void recordPost() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseObject post = new ParseObject("Post");
        post.put("user", user.getObjectId());
        post.put("name", user.getUsername());
        post.put("image", imageFile);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.v("Parse", "Done recording the post");
                } else {
                    Log.e("Parse", e.toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_post, menu);
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
