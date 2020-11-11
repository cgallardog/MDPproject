package com.example.mdpproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mdpproject.R;
import com.example.mdpproject.model.Places;

import java.util.ArrayList;



public class PlacesArrayAdapter extends ArrayAdapter<Places> {
    private ArrayList<Places> items;
    private Context mContext;

    public PlacesArrayAdapter(Context context, ArrayList<Places> places) {
        super( context, 0, places );
        items = places;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ) {

        View newView = convertView;

        // This approach can be improved for performance using a ViewHolder
        if ( newView == null ) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            newView = inflater.inflate(R.layout.country_list_item, parent, false);
        }
        //-----

        TextView textView = (TextView) newView.findViewById(R.id.textCountry);
        ImageView imageView = (ImageView) newView.findViewById(R.id.imgCountry);

        Places place = items.get(position);

        textView.setText(place.getName());
        imageView.setImageResource(place.getImageResource());

        return newView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
