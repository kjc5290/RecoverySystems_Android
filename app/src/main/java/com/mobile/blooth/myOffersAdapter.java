package com.mobile.blooth;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by KevinCostello on 6/16/15.
 */
public class myOffersAdapter extends ParseQueryAdapter<ParseObject> {




    public myOffersAdapter(Context context, final String eventId) {
        // Use the QueryFactory to construct a PQA that will only show
        // Todos marked as high-pri
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {

            ArrayList<String> offerIds = new ArrayList<String>();

            public ParseQuery create() {


                ParseUser user = ParseUser.getCurrentUser();

                Log.i("user is", user.get("username").toString());
                ParseQuery<ParseUser> userTest = ParseUser.getQuery();
                userTest.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
                    public void done(ParseUser object, ParseException e) {
                        Log.i("offerId2", object.get("offersArray").toString());
                        // This method is to fix a bug - it causes the offersArray on the user object to update faster

                    }
                });

                offerIds = (ArrayList<String>) user.get("offersArray");
                Log.i("offerId", offerIds.toString());
                ParseQuery query = new ParseQuery("Offers");
                query.whereEqualTo("eventID", eventId);
                query.whereContainedIn("objectId", offerIds);
                //query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);

                return query;
            }
        });
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.event_schedle, null);
        }

        super.getItemView(object, v, parent);

        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.icon);
        ParseFile imageFile = object.getParseFile("thumbnail");
        if (imageFile != null) {
            todoImage.setParseFile(imageFile);
            todoImage.loadInBackground();
        }

        // Add the title view
        TextView titleTextView = (TextView) v.findViewById(R.id.TalkTitle);
        titleTextView.setText(object.getString("title"));

        ImageButton uneeded = (ImageButton) v.findViewById(R.id.favBtn);
        uneeded.setVisibility(View.GONE);


        return v;
    }


}
