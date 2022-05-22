package com.example.ekomunalka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Map;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database.db";
    public static final String TABLE_NAME = "records";
    public static final String ID = "_id";
    public static final String DATE = "DATE";
    public static final String SERVICE = "SERVICE";
    public static final String CURRENT = "CURRENT";
    public static final String PAID = "PAID";
    public static final String COMMENT = "COMMENT";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " DATE TEXT NOT NULL," +
                " SERVICE TEXT NOT NULL," +
                " CURRENT INTEGER NOT NULL," +
                " PAID INTEGER NOT NULL," +
                " COMMENT TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Map<String, String> values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, values.get("date"));
        contentValues.put(SERVICE, values.get("service"));
        contentValues.put(CURRENT, Integer.valueOf(Objects.requireNonNull(values.get("current"))));
        contentValues.put(PAID, Integer.valueOf(Objects.requireNonNull(values.get("paid"))));
        contentValues.put(COMMENT, values.get("comment"));

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public boolean UpdateData(Map<String, String> values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, values.get("date"));
        contentValues.put(SERVICE, values.get("service"));
        contentValues.put(CURRENT, Integer.valueOf(Objects.requireNonNull(values.get("current"))));
        contentValues.put(PAID, Integer.valueOf(Objects.requireNonNull(values.get("paid"))));
        contentValues.put(COMMENT, values.get("comment"));

        long result = db.update(TABLE_NAME, contentValues, "_id = ?",
                new String[]{String.valueOf(id)});

        return result != -1;
    }

    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor getItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE _id = " + id,
                null);
    }
}
