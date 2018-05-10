package com.example.altaf.rairiwala.PerformanceMonitering;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.altaf.rairiwala.PerformanceMonitering.StarRatingFragment;
import com.example.altaf.rairiwala.R;

public class Rating_Stars_Activity extends AppCompatActivity {
    int vendor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating__stars_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("Rate It");
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        Bundle bundle = getIntent().getExtras();
        vendor_id = bundle.getInt("vendor_id");
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        Bundle bundle = new Bundle();
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = new StarRatingFragment();

                                bundle.putString("rule", "QUALITY");
                                bundle.putInt("vendor_id", vendor_id);
                                selectedFragment.setArguments(bundle);

                                break;
                            case R.id.action_item2:
                                selectedFragment = new StarRatingFragment();
                                bundle.putString("rule", "QUANTITY");
                                bundle.putInt("vendor_id", vendor_id);
                                selectedFragment.setArguments(bundle);
                                break;
                            case R.id.action_item3:
                                selectedFragment = new StarRatingFragment();
                                bundle.putString("rule", "PRICE");
                                bundle.putInt("vendor_id", vendor_id);
                                selectedFragment.setArguments(bundle);
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        Fragment selectedFragment = new StarRatingFragment();
        bundle.putString("rule", "QUALITY");
        bundle.putInt("vendor_id", vendor_id);
        selectedFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
