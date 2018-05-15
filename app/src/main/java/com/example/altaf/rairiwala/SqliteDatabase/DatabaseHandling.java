package com.example.altaf.rairiwala.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.altaf.rairiwala.Models.Category;
import com.example.altaf.rairiwala.Models.Notifications;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AltafHussain on 10/28/2017.
 */

public class DatabaseHandling extends SQLiteOpenHelper {
    public static final String DATBASE_NAME = "rairiwala.db";

    public DatabaseHandling(Context context) {
        super(context, DATBASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table notification(ID INTEGER PRIMARY KEY AUTOINCREMENT,TAG TEXT,TITLE TEXT,MESSAGE TEXT,RecieverId INTEGER)");
        db.execSQL("create table category(CATID INTEGER PRIMARY KEY,CATNAME TEXT,CATIMAGE TEXT)");
        //db.execSQL("create table" + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS notification");
        db.execSQL("DROP TABLE IF EXISTS category");


        onCreate(db);
    }

    public String insert(String TAG, String title, String Message, int reciever_id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put("TAG", TAG);
            contentValues.put("TITLE", title);
            contentValues.put("MESSAGE", Message);
            contentValues.put("RecieverId", reciever_id);


            long result = db.insert("notification", null, contentValues);


            return "inserted" + result;
        } catch (Exception c) {
            return "No" + c.getMessage();

        }
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM  notification";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public List<Notifications> getAllNotes(int recieverid) {
        List<Notifications> notes;
        notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM notification WHERE RecieverId=" + recieverid;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notifications note = new Notifications();
                note.setTag(cursor.getString(1));
                note.setTitle(cursor.getString(2));
                note.setMessage(cursor.getString(3));

                notes.add(note);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public String deleteNote(int id, String tag) {


        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("notification", "RecieverId=? AND TAG=?", new String[]{Integer.toString(id), tag});
            db.close();
            return "Sucess";
        } catch (Exception exption) {
            return exption.getMessage();
        }
    }

    public void insertCategories(Category category) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put("CATID", category.getCategroy_id());
            contentValues.put("CATNAME", category.getCategroy_name());
            contentValues.put("CATIMAGE", category.getCategroy_image());


            long result = db.insert("category", null, contentValues);

        } catch (Exception c) {


        }
    }

    public List<Category> getAllCategories() {
        List<Category> notes;
        notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM category";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCategroy_id(cursor.getInt(0));
                category.setCategroy_name(cursor.getString(1));
                category.setCategroy_image(cursor.getString(2));

                notes.add(category);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();

        // return notes list
        return notes;
    }
}

