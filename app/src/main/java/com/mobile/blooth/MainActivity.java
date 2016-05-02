package com.mobile.blooth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.mobile.blooth.Activity.EventChooserActivity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

//this is only a sample Activity
public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView listview2 = (ListView) findViewById((R.id.listView2));
        setToolbar();
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        // use a default value using new Date()
//        getSupportActionBar().setTitle(prefs.getString("currentEvent", "No Event Selected"));
        //if the current event is not set start event chooser
        if(prefs.getString("currentEvent", "No Event Selected").equalsIgnoreCase("No Event Selected"))
        {
            Intent intent = new Intent(MainActivity.this, EventChooserActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), prefs.getString("currentEvent", "No Event Selected"), Toast.LENGTH_SHORT).show();
        }

        //show choosen event
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereEqualTo("objectId", prefs.getString("objectId", "No Event Selected"));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> eventList, ParseException e) {
                if (e == null && eventList.size()!=0) {
                    //Set custom MyListAdapter using list and custom xml

                    ParseFile fileObject = (ParseFile) eventList.get(0).get("thumbnail");

                    fileObject.getDataInBackground(new GetDataCallback() {

                        public void done(byte[] data,
                                         ParseException e) {
                            if (e == null) {
                                //Log.d("test",
                                //        "We've got data in data.");
                                // Decode the Byte[] into
                                // Bitmap
                                Bitmap bmp = BitmapFactory
                                        .decodeByteArray(
                                                data, 0,
                                                data.length);

                                // Get the ImageView from
                                // main.xml
                               // ImageView image = (ImageView) findViewById(R.id.imageView2);

                                // Set the Bitmap into the
                                // ImageView
                                //image.setImageBitmap(bmp);

                            } else {
                                Log.d("test",
                                        "There was a problem downloading the data.");
                            }
                        }
                    });

                } else {
                    //Log.d("score", "Error: " + e.getMessage());
                }
            }
        });


    }

    //this is stuff for the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                ParseUser.logOut();
                // Start and intent for the dispatch activity
                Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.event_choose:
                Intent i = new Intent(MainActivity.this, EventChooserActivity.class);
                startActivity(i);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        if (toolbar != null) {
            toolbar.setTitle("My Profile");
            setSupportActionBar(toolbar);
        }
//        toolbar.setNavigationIcon(R.drawable.ic_back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i=new Intent(my_profile.this,DispatchActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });
    }

}
