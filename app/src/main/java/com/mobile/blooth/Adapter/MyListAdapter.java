package com.mobile.blooth.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.blooth.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.util.List;

/**
 * Created by Tim on 3/4/2015.
 *
 * This is for the eventChooser view to set the objectId of the the event selected by the user
 */

public class MyListAdapter extends BaseAdapter {

    private Context mContext;
    private int mLayout;
    private List<ParseObject> mArr;
    private LayoutInflater mInflater;

    //Constructor
    public MyListAdapter(Context context, int layout, List<ParseObject> arr) {
        mContext = context;
        mLayout = layout;
        mArr = arr;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mArr.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(mLayout, parent, false);
        }

        ParseFile fileObject = (ParseFile) mArr.get(position).get("thumbnail");
        final View finalConvertView = convertView;
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
                    ImageView image = (ImageView) finalConvertView.findViewById(R.id.imageView1);

                    // Set the Bitmap into the
                    // ImageView
                    image.setImageBitmap(bmp);

                } else {
                    Log.d("test",
                            "There was a problem downloading the data.");
                }
            }
        });

        //Set the ImageView
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageView1);
        //mArr.get(position).get("")

        //Set the text view
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        name.setText(mArr.get(position).getString("EventName"));
        TextView desc = (TextView) convertView.findViewById(R.id.textView2);
        desc.setText(mArr.get(position).getString("descriptionText"));

        //Return the view
        return convertView;
    }
}