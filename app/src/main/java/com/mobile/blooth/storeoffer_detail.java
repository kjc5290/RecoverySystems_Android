package com.mobile.blooth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KevinCostello on 6/18/15.
 */
public class storeoffer_detail extends AppCompatActivity {

    ParseObject offer;
    Integer offerCost;
    ParseUser user;
    ProgressDialog pDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storeoffer_detail);
        setToolbar();
        pDialog = new ProgressDialog(storeoffer_detail.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));



        // get the objectidiD for object form intent
        Bundle bundle=this.getIntent().getExtras();
        //use the stirng to query parse
        final String detailObjectId=bundle.getString("objectId");
       final String descriptionText = bundle.getString("descriptionText");
        final String title = bundle.getString("title");
        String cost = bundle.getString("cost");
        Integer cost1;
        //
        // getSupportActionBar().setTitle(cost1);
        ParseQuery<ParseUser> userTest = ParseUser.getQuery();
        userTest.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                user = object;
            }
        });

        //description
        TextView description = (TextView)findViewById(R.id.descriptionText);
        description.setText(descriptionText);
        description.setMovementMethod(new ScrollingMovementMethod());

        // Add the title view
        TextView titleTextView = (TextView)findViewById(R.id.title);
        titleTextView.setText(title);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");
        query.whereEqualTo("objectId", detailObjectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> eventList, ParseException e) {
                ParseObject object = eventList.get(0);
                offer = object;
                offerCost = object.getInt("cost");
                //image
                ParseImageView image = (ParseImageView) findViewById(R.id.thumbnail);
                ParseFile imageFile = object.getParseFile("thumbnail");
                if (imageFile != null) {
                    image.setParseFile(imageFile);
                    image.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                pDialog.dismiss();
                            }else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }

        });

        final Button button = (Button) findViewById(R.id.redeemButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new AlertDialog.Builder(storeoffer_detail.this)
                        .setTitle("Are you sure you want to Buy this offer?")
                        .setMessage("You can only get this offer if you earned enough points!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                ParseUser user = ParseUser.getCurrentUser();

                                if(user.getInt("points") >= offerCost) {
                                    ArrayList<String> offerArray = (ArrayList<String>) user.get("offersArray");
                                    offerArray.add(detailObjectId);
                                    user.saveInBackground();

                                    user.increment("points", -offerCost);
                                    user.saveInBackground();
                                    Toast.makeText(getApplicationContext(), "Just added" + " " + title, Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(getApplicationContext(),"You do not have enough points to get this offer!", Toast.LENGTH_SHORT).show();
                                }


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle("Boothevent");
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
