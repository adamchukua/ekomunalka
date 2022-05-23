package com.example.ekomunalka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Map;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "e4.db";

    public static final String TABLE_RECORDS = "records";
    public static final String RECORDS_ID = "_id";
    public static final String RECORDS_DATE = "date";
    public static final String RECORDS_SERVICE = "service";
    public static final String RECORDS_CURRENT = "current";
    public static final String RECORDS_PAID = "paid";
    public static final String RECORDS_SUM = "sum";
    public static final String RECORDS_COMMENT = "comment";

    public static final String TABLE_TARIFFS = "tariffs";
    public static final String TARIFFS_ID = "_id";
    public static final String TARIFFS_NAME = "name";
    public static final String TARIFFS_PRICE = "price";
    public static final String TARIFFS_COMMENT = "comment";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRecordsTable = "CREATE TABLE " + TABLE_RECORDS +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " date TEXT NOT NULL," +
                " service TEXT NOT NULL," +
                " current INTEGER NOT NULL," +
                " paid INTEGER NOT NULL," +
                " sum REAL NOT NULL," +
                " comment TEXT," +
                "UNIQUE(date, service))";

        String createTariffsTable = "CREATE TABLE " + TABLE_TARIFFS +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " name TEXT NOT NULL," +
                " price REAL NOT NULL," +
                " comment TEXT," +
                "UNIQUE(name))";

        db.execSQL(createRecordsTable);
        db.execSQL(createTariffsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARIFFS);
        onCreate(db);
    }

    public boolean addRecord(Map<String, String> values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECORDS_DATE, values.get("date"));
        contentValues.put(RECORDS_SERVICE, values.get("service"));
        contentValues.put(RECORDS_CURRENT, Integer.parseInt(Objects.requireNonNull(values.get("current"))));
        contentValues.put(RECORDS_PAID, Integer.parseInt(Objects.requireNonNull(values.get("paid"))));
        contentValues.put(RECORDS_SUM, Float.parseFloat(Objects.requireNonNull(values.get("sum"))));
        contentValues.put(RECORDS_COMMENT, values.get("comment"));

        long result = db.insertOrThrow(TABLE_RECORDS, null, contentValues);

        return result != -1;
    }

    public boolean updateRecord(Map<String, String> values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECORDS_DATE, values.get("date"));
        contentValues.put(RECORDS_SERVICE, values.get("service"));
        contentValues.put(RECORDS_CURRENT, Integer.valueOf(Objects.requireNonNull(values.get("current"))));
        contentValues.put(RECORDS_PAID, Integer.valueOf(Objects.requireNonNull(values.get("paid"))));
        contentValues.put(RECORDS_SUM, Float.parseFloat(Objects.requireNonNull(values.get("sum"))));
        contentValues.put(RECORDS_COMMENT, values.get("comment"));

        long result = db.update(TABLE_RECORDS, contentValues, "_id = ?",
                new String[]{String.valueOf(id)});

        return result != -1;
    }

    public Cursor getRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECORDS, null);
    }

    public Cursor getRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECORDS + " WHERE _id = " + id,
                null);
    }

    public int getRecordPrevious(String service, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT current FROM " + TABLE_RECORDS + " WHERE service = '" +
                        service + "' AND date = '" + date + "'",
                null);
        result.moveToNext();

        if (result.getCount() == 0) {
            return 0;
        }

        return Integer.parseInt(result.getString(0));
    }

    public boolean addTariff(Map<String, String> values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TARIFFS_NAME, values.get("name"));
        contentValues.put(TARIFFS_PRICE, Float.valueOf(Objects.requireNonNull(values.get("price"))));
        contentValues.put(TARIFFS_COMMENT, values.get("comment"));

        long result = db.insertOrThrow(TABLE_TARIFFS, null, contentValues);

        return result != -1;
    }

    public Cursor getTariffs() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TARIFFS, null);
    }

    public float getTariffPrice(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT price FROM " + TABLE_TARIFFS + " WHERE name = '" +
                        name + "'",
                null);
        result.moveToNext();
        return Float.parseFloat(result.getString(0));
    }
}
