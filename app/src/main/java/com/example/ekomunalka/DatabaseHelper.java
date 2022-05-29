package com.example.ekomunalka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ekomunalka_db.db";

    public static final String TABLE_RECORDS = "records";
    public static final String ID_RECORDS = "_id";
    public static final String DATE_RECORDS = "date";
    public static final String SERVICE_RECORDS = "service";
    public static final String CURRENT_RECORDS = "current";
    public static final String PAID_RECORDS = "paid";
    public static final String TRANSPORTATION_FEE_RECORDS = "transportationFee";
    public static final String SUM_RECORDS = "sum";
    public static final String TARIFF_ID_RECORDS = "tariff_id";
    public static final String COMMENT_RECORDS = "comment";

    public static final String TABLE_TARIFFS = "tariffs";
    public static final String ID_TARIFFS = "_id";
    public static final String NAME_TARIFFS = "name";
    public static final String PRICE_TARIFFS = "price";
    public static final String COMMENT_TARIFFS = "comment";

    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String ID_NOTIFICATIONS = "_id";
    public static final String TITLE_NOTIFICATIONS = "title";
    public static final String SUBTITLE_NOTIFICATIONS = "subtitle";
    public static final String DAY_NOTIFICATIONS = "day";

    public DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRecordsTable = "CREATE TABLE " + TABLE_RECORDS +
                " (" + ID_RECORDS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE_RECORDS + "  TEXT NOT NULL," +
                SERVICE_RECORDS + " TEXT NOT NULL," +
                CURRENT_RECORDS + " INTEGER NOT NULL," +
                PAID_RECORDS + " INTEGER NOT NULL," +
                TRANSPORTATION_FEE_RECORDS + " REAL," +
                SUM_RECORDS + " REAL NOT NULL," +
                TARIFF_ID_RECORDS + " INTEGER," +
                COMMENT_RECORDS + " TEXT," +
                "UNIQUE(" + DATE_RECORDS + ", " + SERVICE_RECORDS + "))";

        String createTariffsTable = "CREATE TABLE " + TABLE_TARIFFS +
                " (" + ID_TARIFFS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_TARIFFS + " TEXT NOT NULL," +
                PRICE_TARIFFS + " REAL NOT NULL," +
                COMMENT_TARIFFS + " TEXT," +
                "UNIQUE(" + NAME_TARIFFS +"))";

        String createNotificationsTable = "CREATE TABLE " + TABLE_NOTIFICATIONS +
                " (" + ID_NOTIFICATIONS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE_NOTIFICATIONS + " TEXT NOT NULL," +
                SUBTITLE_NOTIFICATIONS + " TEXT NOT NULL," +
                DAY_NOTIFICATIONS + " INTEGER NOT NULL)";

        db.execSQL(createRecordsTable);
        db.execSQL(createTariffsTable);
        db.execSQL(createNotificationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARIFFS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    public void addRecord(Map<String, String> values) throws ParseException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_RECORDS, values.get(DATE_RECORDS));
        contentValues.put(SERVICE_RECORDS, values.get(SERVICE_RECORDS));
        contentValues.put(CURRENT_RECORDS, Integer.parseInt(Objects.requireNonNull(values.get(CURRENT_RECORDS))));
        contentValues.put(PAID_RECORDS, Integer.parseInt(Objects.requireNonNull(values.get(PAID_RECORDS))));
        if (!Objects.requireNonNull(values.get(TRANSPORTATION_FEE_RECORDS)).isEmpty())
            contentValues.put(TRANSPORTATION_FEE_RECORDS, Objects.requireNonNull(format.parse(Objects.requireNonNull(values.get(TRANSPORTATION_FEE_RECORDS)))).floatValue());
        contentValues.put(SUM_RECORDS, Objects.requireNonNull(format.parse(Objects.requireNonNull(values.get(SUM_RECORDS)))).floatValue());
        contentValues.put(TARIFF_ID_RECORDS, Integer.parseInt(Objects.requireNonNull(values.get(TARIFF_ID_RECORDS))));
        contentValues.put(COMMENT_RECORDS, values.get(COMMENT_RECORDS));

        db.insertOrThrow(TABLE_RECORDS, null, contentValues);
    }

    public void updateRecord(Map<String, String> values, int id) throws ParseException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_RECORDS, values.get(DATE_RECORDS));
        contentValues.put(SERVICE_RECORDS, values.get(SERVICE_RECORDS));
        contentValues.put(CURRENT_RECORDS, Integer.parseInt(Objects.requireNonNull(values.get(CURRENT_RECORDS))));
        contentValues.put(PAID_RECORDS, Integer.parseInt(Objects.requireNonNull(values.get(PAID_RECORDS))));
        if (!Objects.requireNonNull(values.get(TRANSPORTATION_FEE_RECORDS)).isEmpty())
            contentValues.put(TRANSPORTATION_FEE_RECORDS, Objects.requireNonNull(format.parse(Objects.requireNonNull(values.get(TRANSPORTATION_FEE_RECORDS)))).floatValue());
        contentValues.put(SUM_RECORDS, Objects.requireNonNull(format.parse(Objects.requireNonNull(values.get(SUM_RECORDS)))).floatValue());
        contentValues.put(TARIFF_ID_RECORDS, Integer.parseInt(Objects.requireNonNull(values.get(TARIFF_ID_RECORDS))));
        contentValues.put(COMMENT_RECORDS, values.get(COMMENT_RECORDS));

        db.update(TABLE_RECORDS, contentValues, ID_RECORDS + " = ?", new String[]{ String.valueOf(id) });
    }

    public Cursor getRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECORDS + " ORDER BY " + DATE_RECORDS + " DESC", null);
    }

    public Cursor getRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECORDS + " WHERE " + ID_RECORDS + " = " + id,
                null);
    }

    public int getRecordPrevious(String service, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT " + CURRENT_RECORDS + " FROM " + TABLE_RECORDS + " WHERE " + SERVICE_RECORDS + " = '" +
                        service + "' AND " + DATE_RECORDS + " = '" + date + "'",
                null);
        result.moveToNext();
        int id = (result.getCount() != 0) ? Integer.parseInt(result.getString(0)) : 0;
        result.close();
        return id;
    }

    public boolean deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RECORDS, ID_RECORDS + "=" + id, null) > 0;
    }

    public void addTariff(Map<String, String> values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_TARIFFS, values.get(NAME_TARIFFS));
        contentValues.put(PRICE_TARIFFS, Float.valueOf(Objects.requireNonNull(values.get(PRICE_TARIFFS))));
        contentValues.put(COMMENT_TARIFFS, values.get(COMMENT_TARIFFS));

        db.insertOrThrow(TABLE_TARIFFS, null, contentValues);
    }

    public Cursor getTariffs() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TARIFFS, null);
    }

    public float getTariffPrice(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT " + PRICE_TARIFFS + " FROM " + TABLE_TARIFFS +
                        " WHERE " + NAME_TARIFFS + " = '" + name + "'",
                null);
        result.moveToNext();
        float price = Float.parseFloat(result.getString(0));
        result.close();
        return price;
    }

    public boolean deleteTariff(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TARIFFS, ID_TARIFFS + "=" + id, null) > 0;
    }

    public boolean updateTariff(Map<String, String> values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_TARIFFS, values.get(NAME_TARIFFS));
        contentValues.put(PRICE_TARIFFS, Float.parseFloat(Objects.requireNonNull(values.get(PRICE_TARIFFS))));
        contentValues.put(COMMENT_TARIFFS, values.get(COMMENT_TARIFFS));

        long result = db.update(TABLE_TARIFFS, contentValues, ID_TARIFFS + " = ?",
                new String[]{String.valueOf(id)});

        return result != -1;
    }

    public Cursor getTariff(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TARIFFS + " WHERE " + ID_TARIFFS + " = " + id,
                null);
    }

    public void clearData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_RECORDS);
        db.execSQL("DELETE FROM " + TABLE_TARIFFS);
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS);
    }

    public int addNotification(Map<String, String> values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE_NOTIFICATIONS, values.get(TITLE_NOTIFICATIONS));
        contentValues.put(SUBTITLE_NOTIFICATIONS, values.get(SUBTITLE_NOTIFICATIONS));
        contentValues.put(DAY_NOTIFICATIONS, Integer.parseInt(Objects.requireNonNull(values.get(DAY_NOTIFICATIONS))));

        long result = db.insertOrThrow(TABLE_NOTIFICATIONS, null, contentValues);
        return (int) result;
    }

    public Cursor getNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS, null);
    }

    public Cursor getNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + ID_NOTIFICATIONS + " = " + id,
                null);
    }

    public boolean updateNotification(Map<String, String> values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE_NOTIFICATIONS, values.get(TITLE_NOTIFICATIONS));
        contentValues.put(SUBTITLE_NOTIFICATIONS, values.get(SUBTITLE_NOTIFICATIONS));
        contentValues.put(DAY_NOTIFICATIONS, Integer.parseInt(Objects.requireNonNull(values.get(DAY_NOTIFICATIONS))));

        long result = db.update(TABLE_NOTIFICATIONS, contentValues, ID_NOTIFICATIONS + " = ?",
                new String[]{String.valueOf(id)});

        return result != -1;
    }

    public boolean deleteNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTIFICATIONS, ID_NOTIFICATIONS + "=" + id, null) > 0;
    }
}
