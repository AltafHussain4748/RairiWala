package com.example.altaf.rairiwala.AccountManagment;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.Models.Customer;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class AccountDetail extends AppCompatActivity {
    TextView name, pin, phoneNumber, accountDeactivation;
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
        accountDeactivation = findViewById(R.id.deActivation);

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
                            Vendor vendor = SharedPrefManager.getInstance(AccountDetail.this).getSeller();
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
                            if (vendor != null) {
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
        final Customer customer = SharedPrefManager.getInstance(this).getCustomer();
        DeliveryPerson deliveryPerson = SharedPrefManager.getInstance(this).getDeliveryPerson();
        final Vendor vendor = SharedPrefManager.getInstance(AccountDetail.this).getSeller();
        accountDeactivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customer != null) {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(AccountDetail.this);
                    dialogBuilder
                            .withTitle("DeActivate Account")                                  //.withTitle(null)  no title
                            .withTitleColor("#FFFFFF")                                  //def
                            .withDividerColor("#11000000")                              //def
                            .withMessage("Do you want to DeActivate Account?")                     //.withMessage(null)  no Msg
                            .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                            .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
                            .withIcon(getResources().getDrawable(R.drawable.delete))
                            .withDuration(700)                                          //def
                            .withEffect(RotateBottom)                                         //def Effectstype.Slidetop
                            .withButton1Text("No")                                      //def gone
                            .withButton2Text("yes")                                  //def gone
                            .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                            //.setCustomView(View or ResId,context)
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogBuilder.dismiss();
                                }
                            })
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deactivateAccount(SharedPrefManager.getInstance(AccountDetail.this).getPersonId(), "customer");
                                }
                            })
                            .show();

                }
                if (vendor != null) {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(AccountDetail.this);
                    dialogBuilder
                            .withTitle("DeActivate Account")                                  //.withTitle(null)  no title
                            .withTitleColor("#FFFFFF")                                  //def
                            .withDividerColor("#11000000")                              //def
                            .withMessage("Do you want to DeActivate Account?")                     //.withMessage(null)  no Msg
                            .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                            .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
                            .withIcon(getResources().getDrawable(R.drawable.delete))
                            .withDuration(700)                                          //def
                            .withEffect(RotateBottom)                                         //def Effectstype.Slidetop
                            .withButton1Text("No")                                      //def gone
                            .withButton2Text("yes")                                  //def gone
                            .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                            //.setCustomView(View or ResId,context)
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogBuilder.dismiss();
                                }
                            })
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deactivateAccount(SharedPrefManager.getInstance(AccountDetail.this).getPersonId(), "vendor");
                                }
                            })
                            .show();
                }
            }
        });

        if (customer != null) {
            name.setText(customer.getName());
            pin.setText(customer.getPin());
            phoneNumber.setText(customer.getPerson_phone_number());
        }
        if (deliveryPerson != null) {
            accountDeactivation.setVisibility(View.GONE);
            name.setText(deliveryPerson.getName());
            pin.setText(deliveryPerson.getPin());
            phoneNumber.setText(deliveryPerson.getPerson_phone_number());
        }
        if (vendor != null) {
            name.setText(vendor.getName());
            pin.setText(vendor.getPin());
            phoneNumber.setText(vendor.getPerson_phone_number());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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
                                Vendor vendor = SharedPrefManager.getInstance(AccountDetail.this).getSeller();
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
                                } else if (vendor != null) {
                                    if (type.equals("pin")) {
                                        vendor.setPin(String.valueOf(pin1));
                                        pin.setText(String.valueOf(pin1));
                                        SharedPrefManager.getInstance(AccountDetail.this).addSellerToPref(vendor);
                                    } else if (type.equals("name")) {
                                        vendor.setName(name1);
                                        name.setText(name1);
                                        SharedPrefManager.getInstance(AccountDetail.this).addSellerToPref(vendor);
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

    public void deactivateAccount(final int id, final String type) {
        if (type != null && id > 0) {
            ///start string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.DeActivateAccount,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.getBoolean("error") == false) {

                                    SharedPrefManager.getInstance(AccountDetail.this).logOut();
                                    DatabaseHandling handling = new DatabaseHandling(AccountDetail.this);
                                    handling.deleteAllCategories();
                                    startActivity(new Intent(AccountDetail.this, AppStartUpPage.class));
                                    finishAffinity();
                                    AccountDetail.this.finish();


                                    Toast.makeText(AccountDetail.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AccountDetail.this, "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("person_id", String.valueOf(id));
                    params.put("type", type);


                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
            // end string request
        }

    }
}
