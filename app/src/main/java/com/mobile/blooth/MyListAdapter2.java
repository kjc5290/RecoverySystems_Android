package com.mobile.blooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Tim on 3/4/2015.
 *
 * This is only an example - it loads the beacons for each event!
 */

public class MyListAdapter2 extends BaseAdapter {

    private Context mContext;
    private int mLayout;
    private List<ParseObject> mArr;
    private LayoutInflater mInflater;

    //Constructor
    public MyListAdapter2(Context context, int layout, List<ParseObject> arr) {
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

        //Set the text view
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        name.setText(mArr.get(position).getString("commonName"));
        TextView desc = (TextView) convertView.findViewById(R.id.textView2);
        desc.setText("UUID: " + mArr.get(position).getString("UUID"));

        //Return the view
        return convertView;
    }
}