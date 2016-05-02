package com.mobile.blooth;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by KevinCostello on 6/15/15.
 */
public class event_Detail extends AppCompatActivity {
    ProgressDialog pDialog;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail1);
        pDialog = new ProgressDialog(event_Detail.this);
        setToolbar();
        // Set progressbar title
        // Set progressbar message
        //pDialog.setTitle("Activity Feed");
        pDialog.setMessage("Loading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbarit
        pDialog.show();

        // get the objectidiD for object form intent
        Bundle bundle=this.getIntent().getExtras();
        //use the stirng to query parse
        String detailObject=bundle.getString("objectId");


        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Talks");
        query.whereEqualTo("objectId", detailObject);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> eventList, ParseException e) {
                ParseObject object = eventList.get(0);
                //image
                ParseImageView image = (ParseImageView)findViewById(R.id.imageView2);
                ParseFile imageFile = object.getParseFile("picture");
                if (imageFile != null) {
                    image.setParseFile(imageFile);
                    image.loadInBackground(new GetDataCallback() {
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

                // Add the title view
                TextView titleTextView = (TextView)findViewById(R.id.textView);
                titleTextView.setText(object.getString("title"));

                // add startime view formatted mm//dd//yy
                TextView timestampView = (TextView)findViewById(R.id.startTime);
                Date date = object.getDate("startTime");

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");


                // Using DateFormat format method we can create a string
                // representation of a date with the defined format.
                String reportDate = df.format(date);
                timestampView.setText(reportDate);

                //add presenter view
                TextView author = (TextView)findViewById(R.id.presenterName);
                author.setText("By: "+ object.getString("presenterName"));

                //description
                TextView description = (TextView)findViewById(R.id.descriptionText);
                description.setText(object.getString("descriptionText"));
                description.setMovementMethod(new ScrollingMovementMethod());

                //location string
                TextView locaton = (TextView)findViewById(R.id.location);
                locaton.setText(object.getString("Location"));
            }
         });
    }


    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        if (toolbar != null) {
            toolbar.setTitle(prefs.getString("currentEvent", "No Event Selected") + " " + "Schedule");
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}