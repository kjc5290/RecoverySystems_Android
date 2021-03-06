package com.mobile.blooth;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 * Created by KevinCostello on 6/16/15.
 */
public class storeOfferAdapter extends ParseQueryAdapter<ParseObject> {




    public storeOfferAdapter(Context context, final String eventId) {
        // Use the QueryFactory to construct a PQA that will only show
        // Todos marked as high-pri
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {



            public ParseQuery create() {

                ParseQuery query = new ParseQuery("Offers");
                query.whereEqualTo("eventID", eventId);
                Boolean offerTrue = true;
                query.whereEqualTo("storeOffer", offerTrue);

                return query;
            }
        });
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.store_offer, null);
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

        //set the cost
        TextView costTextView = (TextView) v.findViewById(R.id.cost);
        Number cost = object.getInt("cost");
        costTextView.setText(cost.toString());

        return v;
    }


}
