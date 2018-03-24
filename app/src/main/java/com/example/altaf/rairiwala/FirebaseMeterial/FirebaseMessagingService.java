package com.example.altaf.rairiwala.FirebaseMeterial;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.altaf.rairiwala.DeliverPersonManagement.DeliveryPersonHomePage;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.OrderDetail;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


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
                    String orderString = message.getString("message");
                    //  orderObject = new JSONObject(orderString);
                    Intent i = new Intent(this, OrderDetail.class);
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
                    //    message1 = orderString;
                } else if (message.getString("type").equals("dp")) {
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
                    if (SharedPrefManagerFirebase.getInstance(this).getStateActivity()) {
                        Intent intent = new Intent("newOrderAssign");
                        sendAssignOrderBroadcast(intent);
                    }


                } else if (message.getString("type").equals("ordersent")) {
                    String orderString = message.getString("message");
                    Intent i = new Intent(this, OrderDetail.class);
                    i.putExtra("order", orderString);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setContentTitle("New Order")
                            .setContentText(orderString)
                            .setSmallIcon(R.drawable.addproduct)
                            .setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_SOUND);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    manager.notify(0, builder.build());
                    message1 = orderString;
                    //practicer
                    if (SharedPrefManagerFirebase.getInstance(this).getStateActivity()) {
                        Intent intent = new Intent("speedExceeded");
                        sendLocationBroadcast(intent);
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

}