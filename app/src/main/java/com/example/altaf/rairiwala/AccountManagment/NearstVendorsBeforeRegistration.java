package com.example.altaf.rairiwala.AccountManagment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.altaf.rairiwala.CustomerManagment.CategoryListFragment;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;

public class NearstVendorsBeforeRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearst_vendors_before_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Nearst Vendors");
        }
        Bundle bundle = new Bundle();
        bundle.putString("VALUE", "getNearstVendors");
        Fragment fragment = new CategoryListFragment();
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
            DatabaseHandling databaseHandling=new DatabaseHandling(this);
            databaseHandling.deleteAllCategories();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        DatabaseHandling databaseHandling=new DatabaseHandling(this);
        databaseHandling.deleteAllCategories();
    }
}
