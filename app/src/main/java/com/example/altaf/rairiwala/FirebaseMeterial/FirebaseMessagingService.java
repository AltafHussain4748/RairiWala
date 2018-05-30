package com.example.altaf.rairiwala.FirebaseMeterial;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.CustomerManagment.CustomerOrderList;
import com.example.altaf.rairiwala.DeliverPersonManagement.DeliveryPersonHomePage;
import com.example.altaf.rairiwala.Models.Customer;
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.PerformanceMonitering.SystemVendorResponseTime;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerHomePage;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerNewOrderList;
import com.example.altaf.rairiwala.Singelton.NotificationTags;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;


/**
 * Created by filipp on 5/23/2016.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String message1 = "";
    JSONObject orderObject = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Toast.makeText(this, ""+remoteMessage, Toast.LENGTH_SHORT).show();
        // showNotification(remoteMessage.getData().get("message"));
        // Check if message contains a data payload.
        try {
            JSONObject json = new JSONObject(remoteMessage.getData().toString());
            showNotification(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void showNotification(JSONObject message) {

        try {
            if (message.getBoolean("error") == false) {
                if (message.getString("type").equals("customer_order_confirmed")) {
                    Customer customer = SharedPrefManager.getInstance(this).getCustomer();
                    if (customer != null) {
                        String orderString = message.getString("message");
                        //  orderObject = new JSONObject(orderString);
                        Intent i = new Intent(this, CustomerOrderList.class);
                        i.putExtra("order", orderString);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                .setAutoCancel(true)
                                .setContentTitle("New Order")
                                .setContentText("Has been confirmed")
                                .setSmallIcon(R.drawable.addproduct)
                                .setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND);

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        manager.notify(0, builder.build());
                        if (SharedPrefManagerFirebase.getInstance(this).getActivityStateCustomerHomePage()) {
                            Intent intent = new Intent("customerReciever");
                            sendCustomerBroadCast(intent);
                            DatabaseHandling handling = new DatabaseHandling(FirebaseMessagingService.this);
                            handling.insert(NotificationTags.CONFIRMORDER, "New Order", "Order has been confirmed", message.getInt("reciever_id"));
                        } else {

                            DatabaseHandling handling = new DatabaseHandling(FirebaseMessagingService.this);
                            handling.insert(NotificationTags.CONFIRMORDER, "New Order", "Order hass been confirmed ", message.getInt("reciever_id"));
                        }
                    }
                    //    message1 = orderString;
                } else if (message.getString("type").equals("dp")) {
                    DeliveryPerson dp = SharedPrefManager.getInstance(this).getDeliveryPerson();
                    if (dp != null) {
                        Intent i = new Intent(this, DeliveryPersonHomePage.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                .setAutoCancel(true)
                                .setContentTitle("New Order")
                                .setContentText("New Order is assigned")
                                .setSmallIcon(R.drawable.addproduct)
                                .setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND);

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        manager.notify(0, builder.build());
                        if (SharedPrefManagerFirebase.getInstance(this).getActivityStateDeliveryPersonHomePage()) {
                            Intent intent = new Intent("newOrderAssign");
                            sendAssignOrderBroadcast(intent);
                            DatabaseHandling handling = new DatabaseHandling(FirebaseMessagingService.this);
                            handling.insert(NotificationTags.ORDERASSIGNED, "Order Assigned", "Order has been assigned", message.getInt("reciever_id"));
                        } else {

                            DatabaseHandling handling = new DatabaseHandling(FirebaseMessagingService.this);
                            handling.insert(NotificationTags.ORDERASSIGNED, "Order Assigned", "Order has been assigned", message.getInt("reciever_id"));
                        }

                    }

                } else if (message.getString("type").equals("ordersent")) {
                    int vendor_id = SharedPrefManager.getInstance(this).getSeller().getVendor_id();
                    if (vendor_id != 0) {
                        String orderString = message.getString("message");
                        Intent i = new Intent(this, SellerNewOrderList.class);
                        //   i.putExtra("order", orderString);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                .setAutoCancel(true)
                                .setContentTitle("New Order")
                                .setContentText("New Ordedr has arrived")
                                .setSmallIcon(R.drawable.addproduct)
                                .setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND);

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        manager.notify(0, builder.build());
                        message1 = orderString;
                        JSONObject orderObject = new JSONObject(orderString);
                        CustomerAddress customerAddress;
                        Gson gson = new Gson();
                        Type listOfproductType = new TypeToken<CustomerAddress>() {
                        }.getType();
                        customerAddress = gson.fromJson(orderObject.getString("customerAddress"), listOfproductType);

                        //new code of saving order placement time
                        int order_id = 0;
                        order_id = orderObject.getInt("order_id");

                        if (vendor_id != 0 && order_id != 0) {
                            //function call to save data
                            new SystemVendorResponseTime(this).addNewOrderPlacementTime(vendor_id, order_id, "placement");
                        }
                        //practicer
                        if (SharedPrefManagerFirebase.getInstance(this).getActivityStateSellerHomePage() || SharedPrefManagerFirebase.getInstance(this).getStateActivityNewOrderListSeller()) {
                            Intent intent = new Intent("speedExceeded");
                            sendLocationBroadcast(intent);
                            DatabaseHandling handling = new DatabaseHandling(FirebaseMessagingService.this);
                            handling.insert(NotificationTags.NEWORDER, "New Order", "New Order has arrived from " + customerAddress.getName(), message.getInt("reciever_id"));
                        } else {

                            DatabaseHandling handling = new DatabaseHandling(FirebaseMessagingService.this);
                            handling.insert(NotificationTags.NEWORDER, "New Order", "New Order has arrived from " + customerAddress.getName(), message.getInt("reciever_id"));
                        }

                    }
                } else if (message.getString("type").equals("delivered")) {
                    //if the message is aorder delivered
                    int vendor_id = 0;
                    Customer customer = SharedPrefManager.getInstance(FirebaseMessagingService.this).getCustomer();
                    Vendor vendor = SharedPrefManager.getInstance(FirebaseMessagingService.this).getSeller();
                    if (vendor != null) {
                        vendor_id = vendor.getVendor_id();
                    }
                    if (customer != null) {
                        Intent i = new Intent(this, CustomerHomePage.class);
                        //   i.putExtra("order", orderString);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                .setAutoCancel(true)
                                .setContentTitle("Order Delivered")
                                .setContentText("Order has been delivered")
                                .setSmallIcon(R.drawable.addproduct)
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_SOUND);

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        manager.notify(0, builder.build());
                        if (SharedPrefManagerFirebase.getInstance(this).getActivityStateCustomerHomePage()) {
                            Intent intent = new Intent("customerReciever");
                            sendCustomerBroadCast(intent);
                            DatabaseHandling handling = new DatabaseHandling(FirebaseMessagingService.this);
                            handling.insert(NotificationTags.CUSTOMERDELIVERED, "Order Delivered", "Order has been delivered", message.getInt("reciever_id"));
                        }
                    } else if (vendor_id != 0) {
                        Intent i = new Intent(this, SellerHomePage.class);
                        //   i.putExtra("order", orderString);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                .setAutoCancel(true)
                                .setContentTitle("Order Delivered")
                                .setContentText("Order has been delivered")
                                .setSmallIcon(R.drawable.addproduct)
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_SOUND);

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        manager.notify(0, builder.build());
                        if (SharedPrefManagerFirebase.getInstance(this).getActivityStateSellerHomePage()) {
                            Intent intent = new Intent("speedExceeded");
                            sendLocationBroadcast(intent);
                            DatabaseHandling handling = new DatabaseHandling(FirebaseMessagingService.this);
                            handling.insert(NotificationTags.VENDORDELIVERED, "Order Delivered", "Order has been Delivered", message.getInt("reciever_id1"));
                        }
                    }


                }
            }

        } catch (Exception e) {

        }


    }


    private void sendLocationBroadcast(Intent intent) {
        intent.putExtra("currentSpeed", message1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void sendAssignOrderBroadcast(Intent intent) {
        intent.putExtra("newOrderAssigned", "New Order Assigned");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void sendCustomerBroadCast(Intent intent) {
        intent.putExtra("confirmorder", "Order hass been confirmed");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

}