package com.example.mdpproject.model;

import com.example.mdpproject.R;

import java.util.ArrayList;


public class PlacesData {
    private String[] places = {"Swimming pool", "Sport centre", "Garden and Park"};
    private int[] images = {R.drawable.ic_baseline_pool_24,
            R.drawable.ic_baseline_sports_soccer_24,
            R.drawable.ic_baseline_directions_run_24};

    private ArrayList<Places> mList = new ArrayList<Places>();

    public PlacesData() {
        // Build data array list
        for (int i = 0; i < places.length; ++i) {
            Places place = new Places( images[i], places[i] );
            mList.add(place);
        }
    }

    public ArrayList<Places> getPlacesDataList() {
        return mList;
    }
}
