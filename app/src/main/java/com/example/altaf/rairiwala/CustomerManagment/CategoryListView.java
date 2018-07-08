package com.example.altaf.rairiwala.CustomerManagment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.altaf.rairiwala.Models.Category;
import com.example.altaf.rairiwala.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AltafHussain on 2/12/2018.
 */

public class CategoryListView extends BaseAdapter {

    private Context mContext;
    List<Category> categories;

    public CategoryListView(Context context, List<Category> categories) {
        mContext = context;
        this.categories = categories;

    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Category category = categories.get(i);
        if (convertView == null) {

            gridViewAndroid = new View(mContext);
            gridViewAndroid = inflater.inflate(R.layout.category_list_view, null);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid =  gridViewAndroid.findViewById(R.id.category_image);
            textViewAndroid.setText(category.getCategroy_name());
            //  imageViewAndroid.setImageResource(gridViewImageId[i]);
            //Lloading image
            Glide.with(mContext)
                    .load(category.getCategroy_image())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(imageViewAndroid);
        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }
}

