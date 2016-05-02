package com.mobile.blooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by KevinCostello on 7/30/15.
 */
public class favScheduleAdapter extends ParseQueryAdapter<ParseObject> {

    private static SharedPreferences prefs;
    private static ArrayList<String> favList = new ArrayList<String>();



    public favScheduleAdapter(final Context context, final String eventId) {
        // Use the QueryFactory to construct a PQA that will only show
        // Todos marked as high-pri

        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {



            public ParseQuery create() {

                final SharedPreferences prefs = context.getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE);

                if (prefs.getStringSet("userScheduleFavs", null) != null){
                    favList = new ArrayList<String>(prefs.getStringSet("userScheduleFavs", null));
                }
                ParseQuery query = new ParseQuery("Talks");
                query.whereEqualTo("eventId", eventId);
                query.whereContainedIn("objectId",favList);
                return query;
            }
        });
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.event_schedle, null);
        }

        super.getItemView(object, v, parent);

        final SharedPreferences prefs = getContext().getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE);

        if (prefs.getStringSet("userScheduleFavs", null) != null){
            favList = new ArrayList<String>(prefs.getStringSet("userScheduleFavs", null));
        }


        final SharedPreferences.Editor editor = getContext().getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE).edit();

        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.icon);
        ParseFile imageFile = object.getParseFile("picture");
        if (imageFile != null) {
            todoImage.setParseFile(imageFile);
            todoImage.loadInBackground();
        }

        // Add the title view
        TextView titleTextView = (TextView) v.findViewById(R.id.TalkTitle);
        titleTextView.setText(object.getString("title"));

        // add startime view
        TextView timestampView = (TextView) v.findViewById(R.id.startTime);
        Date date = object.getDate("startTime");

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        //SharedPreferences prefs = this.getApplicationContext().getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE);


// Using DateFormat format method we can create a string
// representation of a date with the defined format.
        String reportDate = df.format(date);
        timestampView.setText(reportDate);

        //add presenter view
        TextView author = (TextView) v.findViewById(R.id.author);
        author.setText(object.getString("presenterName"));

        final ImageView btn = (ImageView) v.findViewById(R.id.favBtn);
        if(favList.contains(object.getObjectId())){
            btn.setImageResource(R.drawable.hearticon);
            btn.setSelected(true);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                 if (btn.isSelected() == true) {
                    btn.setImageResource(R.drawable.hearticonunselected);
                    btn.setSelected(false);
                    favList.remove(object.getObjectId());
                    Set<String> set = new HashSet<String>();
                    set.addAll(favList);
                    editor.putStringSet("userScheduleFavs", set);
                    editor.commit();
                    loadObjects();
                }


            }
        });


        return v;
    }


}
