package com.example.altaf.rairiwala.AccountManagment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altaf.rairiwala.Models.Customer;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

public class AccountDetail extends AppCompatActivity {
    TextView name, pin, phoneNumber;
    ImageButton editPin, editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        name = findViewById(R.id.name);
        pin = findViewById(R.id.pinNumber);
        phoneNumber = findViewById(R.id.phoneNumber);
        editName = findViewById(R.id.editName);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountDetail.this);
                builder.setTitle("Change UserName");

// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(AccountDetail.this).inflate(R.layout.layout, null);
// Set up the input
                final EditText newName = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

// Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(AccountDetail.this, "" + newName.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        editPin = findViewById(R.id.editPin);
        editPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountDetail.this);
                builder.setTitle("Change Pin");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(AccountDetail.this).inflate(R.layout.chasngepin, null);
// Set up the input
                final EditText oldPin = (EditText) viewInflated.findViewById(R.id.oldPin);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

// Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(AccountDetail.this, "" + oldPin.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        Customer customer = SharedPrefManager.getInstance(this).getCustomer();
        DeliveryPerson deliveryPerson = SharedPrefManager.getInstance(this).getDeliveryPerson();
        if (customer != null) {
            name.setText(customer.getName());
            pin.setText(customer.getPin());
            phoneNumber.setText(customer.getPerson_phone_number());
        }
        if (deliveryPerson != null) {
            name.setText(deliveryPerson.getName());
            pin.setText(deliveryPerson.getPin());
            phoneNumber.setText(deliveryPerson.getPerson_phone_number());
        }

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
