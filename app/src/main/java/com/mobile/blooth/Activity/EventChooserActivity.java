package com.mobile.blooth.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobile.blooth.BloothLocationServices;
import com.mobile.blooth.DispatchActivity;
import com.mobile.blooth.Adapter.MyListAdapter;
import com.mobile.blooth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class EventChooserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_chooser);
        final ListView listview = (ListView) findViewById((R.id.listView));
        final SharedPreferences prefs = this.getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE);;
        setToolbar();
        // use a default value using new Date()
        final SharedPreferences.Editor editor = prefs.edit();

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    //Set custom MyListAdapter using list and custom xml
                    MyListAdapter adapter = new MyListAdapter(getApplicationContext(), R.layout.list_item, eventList);
                    listview.setAdapter(adapter);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }

                //Set click listener for each item in the listview
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                        Toast.makeText(getApplicationContext(), eventList.get(position).getString("EventName"), Toast.LENGTH_SHORT).show();
                        editor.putString("objectId", eventList.get(position).getObjectId());
                        editor.putString("currentEvent", eventList.get(position).getString("EventName"));
                        editor.commit();
                        ParseUser.getCurrentUser().put("eventId", eventList.get(position).getObjectId());
                        ParseUser.getCurrentUser().saveInBackground();
                        Intent i= new Intent(EventChooserActivity.this, BloothLocationServices.class);
                        i.putExtra("User_Info", ParseUser.getCurrentUser().getObjectId());
                        i.putExtra("Event_Id", eventList.get(position).getObjectId());
                        startService(i);
                        Intent intent = new Intent(EventChooserActivity.this, EventScheduleActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                ParseUser.logOut();
                // Start and intent for the dispatch activity
                Intent intent = new Intent(EventChooserActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle("Choose Event");
            setSupportActionBar(toolbar);
        }
    }

}
