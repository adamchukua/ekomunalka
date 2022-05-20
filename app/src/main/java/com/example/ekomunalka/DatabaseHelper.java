package com.example.ekomunalka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "base.db";
    public static final String TABLE_NAME = "records";
    public static final String COL1 = "ID";
    public static final String COL2 = "DATE";
    public static final String COL3 = "SERVICE";
    public static final String COL4 = "CURRENT";
    public static final String COL5 = "PAID";
    public static final String COL6 = "COMMENT";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " DATE TEXT NOT NULL," +
                " SERVICE TEXT NOT NULL," +
                " CURRENT INTEGER NOT NULL," +
                " PAID INTEGER NOT NULL," +
                " COMMENT TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Object[] values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, (String) values[0]);
        contentValues.put(COL3, (String) values[1]);
        contentValues.put(COL4, (int) values[2]);
        contentValues.put(COL5, (int) values[3]);
        contentValues.put(COL6, (String) values[4]);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }
    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
