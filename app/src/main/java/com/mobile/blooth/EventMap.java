package com.mobile.blooth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


import com.mobile.blooth.Activity.EventChooserActivity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by KevinCostello on 7/30/15.
 */
public class EventMap extends AppCompatActivity {

    ProgressDialog pDialog;
    ParseFile mapfile;
    ParseImageView imageView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_map);
        imageView = (ParseImageView) findViewById(R.id.eventMap);
        setToolbar();
        pDialog = new ProgressDialog(EventMap.this);
        // Set progressbar title
        // Set progressbar message
        //pDialog.setTitle("Activity Feed");
        pDialog.setMessage("Loading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        final SharedPreferences prefs = this.getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE);

        //set the title
//        getSupportActionBar().setTitle("Indoor Map");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));


        //check if event id is set
        String kEventId = prefs.getString("objectId", "No Event Selected");
        Log.i("EventID", kEventId);

        //check if the user has selected a event
        if (prefs.getString("currentEvent", "No Event Selected").equalsIgnoreCase("No Event Selected") || ParseUser.getCurrentUser().get("eventId") == null) {
            Intent intent = new Intent(EventMap.this, EventChooserActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), prefs.getString("currentEvent", "No Event Selected"), Toast.LENGTH_SHORT).show();
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereEqualTo("objectId", kEventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> regionList, ParseException e) {
                mapfile = regionList.get(0).getParseFile("eventMap");
                if (mapfile != null) {
                    imageView.setParseFile(mapfile);
                    imageView.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e==null) {
                                pDialog.dismiss();
                            }else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            Intent chooser = new Intent(EventMap.this, MapsActivity.class);
            chooser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(chooser);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        if (toolbar != null) {
            toolbar.setTitle("Indoor Map");
            setSupportActionBar(toolbar);
        }

    }


}