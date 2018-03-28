package com.example.altaf.rairiwala.DeliverPersonManagement;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altaf.rairiwala.AccountManagment.CheckInterNet;
import com.example.altaf.rairiwala.AccountManagment.ConnectToInternet;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;

import com.example.altaf.rairiwala.Models.Notifications;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;

import com.example.altaf.rairiwala.Singelton.NotificationFragment;
import com.example.altaf.rairiwala.Singelton.SaveToken;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;

import java.util.List;


public class DeliveryPersonHomePage extends AppCompatActivity {
    TextView txtViewCount;
    int count = 0;
    List<Order> ordersList;
    RecyclerView recyclerView;
    AssignedOrderAdapter assignedOrderAdapter;
    ProgressBar progressBar;
    TextView message;
    List<Notifications> notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_person_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!new CheckInterNet(DeliveryPersonHomePage.this).isNetworkAvailable()) {
            startActivity(new Intent(DeliveryPersonHomePage.this, ConnectToInternet.class));
            this.finish();
        }
        getSupportActionBar().setTitle("Home");
        txtViewCount = findViewById(R.id.txtCount);
        Fragment fragment = new DeliveryPersonAssignedOrders();
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
        saveToken();
        //broadcast reciever
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("newOrderAssign"));
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
        getMenuInflater().inflate(R.menu.delivery_person, menu);
        final View notificaitons = menu.findItem(R.id.actionNotifications).getActionView();
        txtViewCount = (TextView) notificaitons.findViewById(R.id.txtCount);
        final DatabaseHandling databaseHandling = new DatabaseHandling(DeliveryPersonHomePage.this);
        int count = 0;
        count = SharedPrefManager.getInstance(this).getDeliveryPerson().getDelivery_person_id();
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
                    bundle.putString("rule", "dp");
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
                    Toast.makeText(DeliveryPersonHomePage.this, "No New Order", Toast.LENGTH_SHORT).show();
                }
                //    TODO


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
            SharedPrefManager.getInstance(DeliveryPersonHomePage.this).logOut();
            startActivity(new Intent(this, UserLogin.class));
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveToken() {
        if (SharedPrefManager.getInstance(DeliveryPersonHomePage.this).getPersonId() != 0) {
            if (!SharedPrefManagerFirebase.getInstance(DeliveryPersonHomePage.this).getToken().equals(SharedPrefManagerFirebase.getInstance(DeliveryPersonHomePage.this).getTokenUpdated()) && SharedPrefManagerFirebase.getInstance(DeliveryPersonHomePage.this).getToken() != "no") {
                new SaveToken(DeliveryPersonHomePage.this).saveToken();
            }
        } else {
            Toast.makeText(this, "Some error", Toast.LENGTH_SHORT).show();
        }
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
        SharedPrefManagerFirebase.getInstance(this).saveActivityStateDeliveryPersonHomePage(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPrefManagerFirebase.getInstance(this).saveActivityStateDeliveryPersonHomePage(false);
    }


}
