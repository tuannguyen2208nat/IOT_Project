package com.example.iot_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.iot_project.model.Item;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateDB = "CREATE TABLE items(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "time TEXT," +
                "detail TEXT)";
        db.execSQL(sqlCreateDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if necessary
    }

    public List<Item> getAll() {
        List<Item> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor rs = null;
        try {
            String order = "id DESC"; // Order by id in descending order
            rs = db.query("items", null, null, null, null, null, order);
            while (rs != null && rs.moveToNext()) {
                int id = rs.getInt(0);
                String time = rs.getString(1);
                String detail = rs.getString(2);
                Item item = new Item(id, time, detail);
                list.add(item);
            }
        } catch (Exception e) {
            Log.e("SQLiteHelper", "Error reading from database", e);
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return list;
    }

    public long addItem(Item i) {
        ContentValues values = new ContentValues();
        values.put("time", i.getTime());
        values.put("detail", i.getDetail());
        SQLiteDatabase db = getWritableDatabase();
        return db.insert("items", null, values);
    }
}
