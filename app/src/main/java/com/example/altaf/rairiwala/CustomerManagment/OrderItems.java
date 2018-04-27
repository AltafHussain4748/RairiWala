package com.example.altaf.rairiwala.CustomerManagment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SaveToken;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderItems extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    ArrayList<Product> products;
    RecyclerView recyclerView;
    Button order;
    CheckOutAdapter checkOutAdapter;
    RelativeLayout relativeLayout;
    TextView totalprice;
    int price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_order_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        final String jsonString = bundle.getString("CART");
        Gson gson = new Gson();
        Type listOfproductType = new TypeToken<List<Product>>() {
        }.getType();
        order = findViewById(R.id.order);
        totalprice = findViewById(R.id.totalprice);
        relativeLayout = findViewById(R.id.cordinator_layout);
        products = gson.fromJson(jsonString, listOfproductType);
        recyclerView = findViewById(R.id.product_checkout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkOutAdapter = new CheckOutAdapter(this, products, order);
        recyclerView.setAdapter(checkOutAdapter);
        for (Product pr : products) {
            price = price + (pr.getProductDetails().getQuantity() * pr.getProductDetails().getPrice());
        }
        totalprice.setText("Total Rs:" + price);
        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, OrderItems.this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        // refreshing recycler view
        checkOutAdapter.notifyDataSetChanged();
        saveToken();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_cometo_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        } else if (d == R.id.home_return) {
            startActivity(new Intent(OrderItems.this, CustomerHomePage.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CheckOutAdapter.ProductViewHolder) {
            // get the removed item name to display it in snack bar
            String name = products.get(viewHolder.getAdapterPosition()).getProduct_name();
            final Product product;
            product = products.get(position);
            // backup of removed item for undo purpose
            final Product deletedItem = products.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            checkOutAdapter.removeItem(viewHolder.getAdapterPosition());
            price = price - product.getProductDetails().getPrice() * product.getProductDetails().getQuantity();
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    price = price + product.getProductDetails().getPrice() * product.getProductDetails().getQuantity();
                    // undo is selected, restore the deleted item
                    checkOutAdapter.restoreItem(deletedItem, deletedIndex);
                    totalprice.setText("Total Rs:" + price);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            totalprice.setText("Total Rs:" + price);
        }
    }

    public void saveToken() {
        if (SharedPrefManager.getInstance(OrderItems.this).getPersonId() != 0) {
            if (!SharedPrefManagerFirebase.getInstance(OrderItems.this).getToken().equals(SharedPrefManagerFirebase.getInstance(OrderItems.this).getTokenUpdated()) && SharedPrefManagerFirebase.getInstance(OrderItems.this).getToken() != "no") {
                new SaveToken(OrderItems.this).saveToken();
            }
        }
    }
}
