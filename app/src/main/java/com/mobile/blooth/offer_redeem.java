package com.mobile.blooth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by KevinCostello on 6/18/15.
 */
public class offer_redeem extends AppCompatActivity {

    ProgressDialog pDialog;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem_offer);
        setToolbar();
//        getSupportActionBar().setTitle("Show this to redeem your offer!");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));

        // get the objectidiD for object form intent
        pDialog = new ProgressDialog(offer_redeem.this);
        pDialog.setMessage("Redeeming...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();


        String offerId = getIntent().getExtras().getString("objectId");;
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");
        query.whereEqualTo("objectId", offerId);
        Log.i("offerID",offerId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> offerList, ParseException e) {
                ParseObject object = offerList.get(0);
                //image
                ParseImageView image = (ParseImageView) findViewById(R.id.offerImage);
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

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            Intent chooser = new Intent(offer_redeem.this, MyOffers.class);
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
            toolbar.setTitle("Show this to redeem your offer!");
            setSupportActionBar(toolbar);
        }
    }

}
