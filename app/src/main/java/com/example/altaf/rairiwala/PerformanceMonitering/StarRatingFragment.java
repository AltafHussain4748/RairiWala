package com.example.altaf.rairiwala.PerformanceMonitering;

/**
 * Created by AltafHussain on 3/25/2018.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.altaf.rairiwala.R;

public class StarRatingFragment extends Fragment {
    public static StarRatingFragment newInstance() {
        StarRatingFragment fragment = new StarRatingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.star_rating_fragment, container, false);
    }
}