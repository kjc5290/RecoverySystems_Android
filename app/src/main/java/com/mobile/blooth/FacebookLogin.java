package com.mobile.blooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mobile.blooth.Activity.EventChooserActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tim on 2/18/2015.
 */
public class FacebookLogin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseFacebookUtils.initialize(this);
        System.out.println("Before pfu");
        List<String> permissions = Arrays.asList("public_profile");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {

            @Override
            public void done(ParseUser parseUser, ParseException e) {

                if (e != null) {
                    // Show the error message
                    Toast.makeText(FacebookLogin.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    System.out.println(parseUser.getUsername());
                    // Start an intent for the dispatch activity
                    //add the empty array of offers
                    ArrayList<String> emptyArray = new ArrayList<String>();
                    parseUser.put("offersArray", emptyArray);
                    parseUser.saveInBackground();
                    Toast.makeText(FacebookLogin.this, "Login Done", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FacebookLogin.this, EventChooserActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
}

