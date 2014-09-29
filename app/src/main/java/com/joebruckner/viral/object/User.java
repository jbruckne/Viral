package com.joebruckner.viral.object;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by joebruckner on 9/29/14.
 */
public class User {
    Context context;

    public User(Context context) {
        this.context = context;
    }

    public String getUsername() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("username", null);
    }

    public String getUserId() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("userId", null);
    }

    public void updateData() {
        // TODO
    }

    public void logout(Class className) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("token");
        editor.remove("tokenId");
        editor.remove("timestamp");
        editor.remove("userId");
        editor.apply();

        Intent loginIntent = new Intent(context, className);
        context.startActivity(loginIntent);
    }

}
