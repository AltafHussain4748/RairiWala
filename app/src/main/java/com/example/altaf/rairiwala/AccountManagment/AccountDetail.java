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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.Models.Customer;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                        if (newName.getText().length() >= 4) {
                            Toast.makeText(AccountDetail.this, "" + newName.getText(), Toast.LENGTH_SHORT).show();
                            if (SharedPrefManager.getInstance(AccountDetail.this).getPersonId() != 0) {
                                changeData(SharedPrefManager.getInstance(AccountDetail.this).getPersonId(), 0, newName.getText().toString(), "name");
                            }
                        } else {
                            Toast.makeText(AccountDetail.this, "More than 4 characters..", Toast.LENGTH_SHORT).show();
                        }

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
                final EditText newPin = (EditText) viewInflated.findViewById(R.id.newPin);
                final EditText confirmPin = (EditText) viewInflated.findViewById(R.id.confirmPin);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

// Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (newPin.getText().toString().equals(confirmPin.getText().toString())) {
                            Customer customer = SharedPrefManager.getInstance(AccountDetail.this).getCustomer();
                            DeliveryPerson deliveryPerson = SharedPrefManager.getInstance(AccountDetail.this).getDeliveryPerson();
                            if (customer != null) {
                                if (customer.getPin().toString().equals(oldPin.getText().toString())) {
                                    changeData(SharedPrefManager.getInstance(AccountDetail.this).getPersonId(), Integer.parseInt(confirmPin.getText().toString()), newPin.getText().toString(), "pin");

                                } else {
                                    Toast.makeText(AccountDetail.this, "Old pin incorrect", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (deliveryPerson != null) {
                                if (deliveryPerson.getPin().toString().equals(oldPin.getText().toString())) {
                                    changeData(SharedPrefManager.getInstance(AccountDetail.this).getPersonId(), Integer.parseInt(confirmPin.getText().toString()), newPin.getText().toString(), "pin");

                                } else {
                                    Toast.makeText(AccountDetail.this, "Old pin incorrect", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            Toast.makeText(AccountDetail.this, "Pin does not matched", Toast.LENGTH_SHORT).show();
                        }
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

    private void changeData(final int person_id1, final int pin1, final String name1, final String type) {
        Toast.makeText(this, "Updating", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.ChangeAccount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false) {
                                Customer customer = SharedPrefManager.getInstance(AccountDetail.this).getCustomer();
                                DeliveryPerson deliveryPerson = SharedPrefManager.getInstance(AccountDetail.this).getDeliveryPerson();
                                Toast.makeText(AccountDetail.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if (customer != null) {
                                    if (type.equals("pin")) {
                                        customer.setPin(String.valueOf(pin1));
                                        pin.setText(String.valueOf(pin1));
                                        SharedPrefManager.getInstance(AccountDetail.this).addCustomerToPref(customer);
                                    } else if (type.equals("name")) {
                                        customer.setName(name1);
                                        name.setText(name1);
                                        SharedPrefManager.getInstance(AccountDetail.this).addCustomerToPref(customer);
                                    }
                                } else if (deliveryPerson != null) {
                                    if (type.equals("pin")) {
                                        deliveryPerson.setPin(String.valueOf(pin1));
                                        pin.setText(String.valueOf(pin1));
                                        SharedPrefManager.getInstance(AccountDetail.this).addDeliveryPersonToPref(deliveryPerson);
                                    } else if (type.equals("name")) {
                                        deliveryPerson.setName(name1);
                                        name.setText(name1);
                                        SharedPrefManager.getInstance(AccountDetail.this).addDeliveryPersonToPref(deliveryPerson);
                                    }
                                }

                            } else {
                                Toast.makeText(AccountDetail.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Toast.makeText(getApplicationContext(), "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("person_id", String.valueOf(person_id1));
                params.put("name", String.valueOf(name1));
                params.put("pin", String.valueOf(pin1));
                params.put("type", String.valueOf(type));


                return params;
            }
        };
        RequestHandler.getInstance(AccountDetail.this).addToRequestQueue(stringRequest);
    }
}
