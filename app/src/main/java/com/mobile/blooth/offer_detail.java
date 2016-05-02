package com.mobile.blooth;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
public class offer_detail extends AppCompatActivity {

    ProgressDialog pDialog;
    private Toolbar toolbar;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myoffers_detail);

        ActionBar actionBar = getActionBar();
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(offer_detail.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        setToolbar();

        // get the objectidiD for object form intent
        Bundle bundle=this.getIntent().getExtras();
        //use the stirng to query parse
        final String detailObjectId=bundle.getString("objectId");
        String descriptionText = bundle.getString("descriptionText");
        title= bundle.getString("title");

//        getSupportActionBar().setTitle(title);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));

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
                new AlertDialog.Builder(offer_detail.this)
                        .setTitle("Are you sure you want to redeem this offer?")
                        .setMessage("Once you redeem this offer you will not be able to access it again!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ParseUser user = ParseUser.getCurrentUser();
                                ParseQuery<ParseUser> userTest = ParseUser.getQuery();
                                userTest.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
                                    public void done(ParseUser object, ParseException e) {
                                        ArrayList<String> offerArray = (ArrayList<String>) object.get("offersArray");
                                        offerArray.remove(detailObjectId);
                                        object.put("offersArray", offerArray);
                                        object.saveInBackground();
                                        // call the redeem view
                                        Intent redeemOffer = new Intent(offer_detail.this, offer_redeem.class);
                                        redeemOffer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Log.i("detailObject is", detailObjectId);
                                        redeemOffer.putExtra("objectId", detailObjectId);
                                        startActivity(redeemOffer);
                                    }
                                });


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
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }

    }

}
