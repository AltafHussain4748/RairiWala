package com.example.altaf.rairiwala.Singelton;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.altaf.rairiwala.CustomerManagment.CategoryListFragment;
import com.example.altaf.rairiwala.CustomerManagment.CustomerOrderList;
import com.example.altaf.rairiwala.DeliverPersonManagement.DeliveryPersonAssignedOrders;
import com.example.altaf.rairiwala.Models.Notifications;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerNewOrderList;
import com.example.altaf.rairiwala.RairriWalaManagment.VendorSellingHistory;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AltafHussain on 3/5/2018.
 */

public class NotificationFragment extends Fragment {
    List<Notifications> notifications;
    RecyclerView recyclerView;
    View view;
    String rule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.notification_fragment, container, false);
        notifications = new ArrayList<>();
        recyclerView = view.findViewById(R.id.notification_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final DatabaseHandling databaseHandling = new DatabaseHandling(getActivity());
        if (getArguments() != null) {
            rule = getArguments().getString("rule");
        }
        if (rule.equals("dp")) {

            notifications = databaseHandling.getAllNotes(SharedPrefManager.getInstance(getActivity()).getDeliveryPerson().getDelivery_person_id());
        } else if (rule.equals("vendor")) {
            int count = 0;
            count = SharedPrefManager.getInstance(getActivity()).getSeller().getVendor_id();
            if (count != 0) {
                notifications = databaseHandling.getAllNotes(count);
            }
        } else if (rule.equals("customer")) {
            notifications = databaseHandling.getAllNotes(SharedPrefManager.getInstance(getActivity()).getCustomer().getCustomer_id());
        }

        NotificationAdapter newOrderAdapter = new NotificationAdapter(getActivity(), notifications);
        recyclerView.setAdapter(newOrderAdapter);
        //long press listener
        //recyclerView Item click listener
        //inner class
        class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

            private NotificationFragment.ClickListener clicklistener;
            private GestureDetector gestureDetector;

            public RecyclerTouchListener(NotificationFragment context, final RecyclerView recycleView, final NotificationFragment
                    .ClickListener clicklistener) {

                this.clicklistener = clicklistener;
                gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && clicklistener != null) {
                            clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                        }
                    }
                });
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                    clicklistener.onClick(child, rv.getChildAdapterPosition(child));
                }

                return false;
            }

            //
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        }
        //end of inner class click listener
        //start recycler view click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view, final int position) {
                try {
                    Notifications deliveryPerson = notifications.get(position);
                    if (deliveryPerson.getTag().equals(NotificationTags.NEWORDER)) {
                        startActivity(new Intent(getActivity(), SellerNewOrderList.class));
                        DatabaseHandling handling = new DatabaseHandling(getActivity());
                        handling.deleteNote(SharedPrefManager.getInstance(getActivity()).getSeller().getVendor_id(), NotificationTags.NEWORDER);
                        TextView counter = getActivity().findViewById(R.id.notificationcount);
                        int count = 0;
                        for (Notifications notification : notifications) {
                            if (notification.getTag().equals(NotificationTags.NEWORDER)) {
                                count++;
                            }
                        }
                        int noti = notifications.size() - count;
                        if (noti == 0) {
                            counter.setText(Integer.toString(0));
                            counter.setVisibility(View.GONE);
                        } else {
                            counter.setText(Integer.toString(count));
                        }


                    } else if (deliveryPerson.getTag().equals(NotificationTags.ORDERASSIGNED)) {
                        Fragment fragment = new DeliveryPersonAssignedOrders();
                        FragmentManager fm = getFragmentManager();
                        // create a FragmentTransaction to begin the transaction and replace the Fragment
                        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        // replace the FrameLayout with new Fragment
                        fragmentTransaction.replace(R.id.frameLayout, fragment);
                        fragmentTransaction.commit();
                        databaseHandling.deleteNote(SharedPrefManager.getInstance(getActivity()).getDeliveryPerson().getDelivery_person_id(), NotificationTags.ORDERASSIGNED);
                        TextView delievryPersonCounter = getActivity().findViewById(R.id.txtCount);
                        delievryPersonCounter.setVisibility(View.GONE);
                        delievryPersonCounter.setText("0");
                    } else if (deliveryPerson.getTag().equals(NotificationTags.CONFIRMORDER)) {

                        startActivity(new Intent(getActivity(), CustomerOrderList.class));
                        Fragment fragment = new CategoryListFragment();
                        FragmentManager fm = getFragmentManager();
                        // create a FragmentTransaction to begin the transaction and replace the Fragment
                        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        // replace the FrameLayout with new Fragment
                        fragmentTransaction.replace(R.id.frameLayout, fragment);
                        fragmentTransaction.commit();
                        databaseHandling.deleteNote(SharedPrefManager.getInstance(getActivity()).getCustomer().getCustomer_id(), NotificationTags.CONFIRMORDER);
                        TextView delievryPersonCounter = getActivity().findViewById(R.id.customer_notifictaion_count);
                        delievryPersonCounter.setVisibility(View.GONE);
                        delievryPersonCounter.setText("0");
                    } else if (deliveryPerson.getTag().equals(NotificationTags.VENDORDELIVERED)) {

                        startActivity(new Intent(getActivity(), VendorSellingHistory.class));
                      /*  Fragment fragment = new FragmentAccountDetail();
                        FragmentManager fm = getFragmentManager();
                        // create a FragmentTransaction to begin the transaction and replace the Fragment
                        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        // replace the FrameLayout with new Fragment
                        fragmentTransaction.replace(R.id.frameLayout, fragment);
                        fragmentTransaction.commit();*/
                        databaseHandling.deleteNote(SharedPrefManager.getInstance(getActivity()).getSeller().getVendor_id(), NotificationTags.VENDORDELIVERED);
                        TextView delievryPersonCounter = getActivity().findViewById(R.id.notificationcount);
                        delievryPersonCounter.setVisibility(View.GONE);
                        delievryPersonCounter.setText("" + databaseHandling.getNotesCount());
                    }

                } catch (Exception e) {

                }

            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));
        //end of recycler view item click listener
        //end of recycler view item click listener
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("AccountDetails");
    }

    //Interface
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }
}

