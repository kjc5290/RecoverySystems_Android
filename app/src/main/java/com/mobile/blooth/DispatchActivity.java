package com.mobile.blooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mobile.blooth.Activity.EventChooserActivity;
import com.mobile.blooth.Activity.EventScheduleActivity;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

//import com.bloothevents.kevincostello.bloothlocationservices.BloothLocationServices;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {
    private String userObj;

    public DispatchActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if there is current user info
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "vSG1sUHBHr6u4yV98nhLpRhBd99TymLG7R9Pj7zK", "myMfTfqnMoboNwpU5vGWA2lD2pM6c5nShfZIGFzF");
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);

        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            userObj = ParseUser.getCurrentUser().getObjectId();

            if(prefs.getString("currentEvent", "No Event Selected").equalsIgnoreCase("No Event Selected"))
            {
                Intent intent = new Intent(DispatchActivity.this, EventChooserActivity.class);
                startActivity(intent);
                finish();
            }

            ParseInstallation.getCurrentInstallation().put("user",ParseUser.getCurrentUser());
            ParseInstallation.getCurrentInstallation().put("eventId",prefs.getString("objectId", "No Event Selected"));
            ParseInstallation.getCurrentInstallation().saveInBackground();

            Intent i= new Intent(this, BloothLocationServices.class);
            i.putExtra("User_Info",userObj);
            i.putExtra("Event_Id",prefs.getString("objectId", "No Event Selected"));
            startService(i);

            startActivity(new Intent(this, EventScheduleActivity.class)); //change this to schedule view
            //do anyother things before load such as start Blooth Services
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, SignInActivity.class));
        }
    }



}