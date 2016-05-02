package com.mobile.blooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by KevinCostello on 6/30/15.
 */
public class photoContestAdapter extends BaseAdapter {

    private Context mContext;
    private int mLayout;
    private List<ParseObject> mArr;
    private LayoutInflater mInflater;

    // Constructor
    public photoContestAdapter(Context c, List<ParseObject> arr) {
        mContext = c;
        mArr = arr;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        com.parse.ParseImageView imageView;


        if (convertView == null) {
            imageView = new com.parse.ParseImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (com.parse.ParseImageView) convertView;
        }
        imageView.setParseFile(mArr.get(position).getParseFile("image"));
        imageView.loadInBackground();
        return imageView;
    }


}
