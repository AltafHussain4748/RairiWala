package com.example.altaf.rairiwala.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.altaf.rairiwala.Models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AltafHussain on 3/20/2018.
 */

public class DbHandler extends SQLiteOpenHelper {
    public static final String DATBASE_NAME = "rairiwala.db";

    public DbHandler(Context context) {
        super(context, DATBASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table category(Cat_ID INTEGER PRIMARY KEY,Cat_NAME TEXT,Cat_Image TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS category");
        onCreate(db);
    }

    public String addCategory(Category category) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put("Cat_ID", category.getCategroy_id());
            contentValues.put("Cat_NAME", category.getCategroy_name());
            contentValues.put("Cat_Image", category.getCategroy_image());
            long result = db.insert("category", null, contentValues);
            return "" + result;

        } catch (Exception c) {
            return c.getMessage();
        }
    }

    // code to get all contacts in a list view
    public List<Category> getAllContacts() {
        List<Category> contactList = new ArrayList<Category>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + "category";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCategroy_id(cursor.getInt(0));
                category.setCategroy_name(cursor.getString(1));
                category.setCategroy_image(cursor.getString(2));
                // Adding contact to list
                contactList.add(category);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }
}
