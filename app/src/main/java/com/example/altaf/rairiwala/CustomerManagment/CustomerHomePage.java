package com.example.altaf.rairiwala.CustomerManagment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.AccountManagment.CheckInterNet;
import com.example.altaf.rairiwala.AccountManagment.ConnectToInternet;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;
import com.example.altaf.rairiwala.DeliverPersonManagement.DeliveryPersonAssignedOrders;
import com.example.altaf.rairiwala.DeliverPersonManagement.DeliveryPersonHomePage;
import com.example.altaf.rairiwala.Models.Category;
import com.example.altaf.rairiwala.Models.Notifications;
import com.example.altaf.rairiwala.PerformanceMonitering.Rating_Stars_Activity;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.CategoryListView;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.NotificationFragment;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomePage extends AppCompatActivity {
    List<Category> category_List;
    List<Category> sqliteDb;
    ProgressDialog progressDialog;
    GridView androidListView;
    TextView message;
    TextView txtViewCount;
    DatabaseHandling databaseHandling;
    List<Notifications> notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!new CheckInterNet(CustomerHomePage.this).isNetworkAvailable()) {
            startActivity(new Intent(CustomerHomePage.this, ConnectToInternet.class));
            this.finish();
        }
        txtViewCount = findViewById(R.id.customer_notifictaion_count);
        // get the reference of Button
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        /*FirebaseMessaging.getInstance().subscribeToTopic("rairiwala");
        FirebaseInstanceId.getInstance().getToken();*/
        Fragment fragment = new CategoryListFragment();
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("customerReciever"));

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_menus, menu);
        final View notificaitons = menu.findItem(R.id.actionNotifications).getActionView();

        txtViewCount = (TextView) notificaitons.findViewById(R.id.customer_notifictaion_count);
        final DatabaseHandling databaseHandling = new DatabaseHandling(CustomerHomePage.this);
        int count = 0;
        count = SharedPrefManager.getInstance(this).getCustomer().getCustomer_id();
        if (count != 0) {
            notificationsList = databaseHandling.getAllNotes(count);
            txtViewCount.setText(Integer.toString(notificationsList.size()));
        }
        if (txtViewCount.getText().equals("0")) {
            txtViewCount.setVisibility(View.GONE);
        }
        txtViewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        notificaitons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(txtViewCount.getText().toString());
                if (count > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("rule", "customer");
                    Fragment fragment = new NotificationFragment();
                    fragment.setArguments(bundle);
                    FragmentManager fm = getFragmentManager();
                    // create a FragmentTransaction to begin the transaction and replace the Fragment
                    android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    // replace the FrameLayout with new Fragment
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit();
                    txtViewCount.setText("0");
                    txtViewCount.setVisibility(View.GONE);
                } else {
                    Toast.makeText(CustomerHomePage.this, "No New Order", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPrefManager.getInstance(CustomerHomePage.this).logOut();
            startActivity(new Intent(this, UserLogin.class));
            this.finish();
            return true;
        } else if (id == R.id.customer_orders) {
            startActivity(new Intent(CustomerHomePage.this, CustomerOrderList.class));
        } else if (id == R.id.rate) {
            startActivity(new Intent(CustomerHomePage.this, Rating_Stars_Activity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null) {
                    int count = Integer.parseInt(txtViewCount.getText().toString());
                    count = count + 1;
                    txtViewCount.setText(Integer.toString(count));
                    txtViewCount.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        SharedPrefManagerFirebase.getInstance(this).saveActivityStateCustomerHomePage(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPrefManagerFirebase.getInstance(this).saveActivityStateCustomerHomePage(false);
    }
}
