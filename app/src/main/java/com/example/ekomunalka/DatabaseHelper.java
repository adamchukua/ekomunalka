package com.example.ekomunalka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ekomunalka.db";

    public static final String TABLE_RECORDS = "records";
    public static final String RECORDS_ID = "_id";
    public static final String RECORDS_DATE = "date";
    public static final String RECORDS_SERVICE = "service";
    public static final String RECORDS_CURRENT = "current";
    public static final String RECORDS_PAID = "paid";
    public static final String RECORDS_SUM = "sum";
    public static final String RECORDS_TARIFF_ID = "tariff_id";
    public static final String RECORDS_COMMENT = "comment";

    public static final String TABLE_TARIFFS = "tariffs";
    public static final String TARIFFS_ID = "_id";
    public static final String TARIFFS_NAME = "name";
    public static final String TARIFFS_PRICE = "price";
    public static final String TARIFFS_COMMENT = "comment";

    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String NOTIFICATIONS_ID = "_id";
    public static final String NOTIFICATIONS_TITLE = "title";
    public static final String NOTIFICATIONS_SUBTITLE = "subtitle";
    public static final String NOTIFICATIONS_DAY = "day";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
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
                " tariff_id INTEGER," +
                " comment TEXT," +
                "UNIQUE(date, service))";

        String createTariffsTable = "CREATE TABLE " + TABLE_TARIFFS +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " name TEXT NOT NULL," +
                " price REAL NOT NULL," +
                " comment TEXT," +
                "UNIQUE(name))";

        String createNotificationsTable = "CREATE TABLE " + TABLE_NOTIFICATIONS +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " title TEXT NOT NULL," +
                " subtitle TEXT NOT NULL," +
                " day INTEGER NOT NULL)";

        db.execSQL(createRecordsTable);
        db.execSQL(createTariffsTable);
        db.execSQL(createNotificationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARIFFS);
        onCreate(db);
    }

    public boolean addRecord(Map<String, String> values) throws ParseException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECORDS_DATE, values.get("date"));
        contentValues.put(RECORDS_SERVICE, values.get("service"));
        contentValues.put(RECORDS_CURRENT, Integer.parseInt(Objects.requireNonNull(values.get("current"))));
        contentValues.put(RECORDS_PAID, Integer.parseInt(Objects.requireNonNull(values.get("paid"))));
        contentValues.put(RECORDS_SUM, Objects.requireNonNull(format.parse(Objects.requireNonNull(values.get("sum")))).floatValue());
        contentValues.put(RECORDS_TARIFF_ID, Integer.parseInt(Objects.requireNonNull(values.get("tariff_id"))));
        contentValues.put(RECORDS_COMMENT, values.get("comment"));

        long result = db.insertOrThrow(TABLE_RECORDS, null, contentValues);

        return result != -1;
    }

    public boolean updateRecord(Map<String, String> values, int id) throws ParseException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECORDS_DATE, values.get("date"));
        contentValues.put(RECORDS_SERVICE, values.get("service"));
        contentValues.put(RECORDS_CURRENT, Integer.valueOf(Objects.requireNonNull(values.get("current"))));
        contentValues.put(RECORDS_PAID, Integer.valueOf(Objects.requireNonNull(values.get("paid"))));
        contentValues.put(RECORDS_SUM, Objects.requireNonNull(format.parse(Objects.requireNonNull(values.get("sum")))).floatValue());
        contentValues.put(RECORDS_TARIFF_ID, Integer.parseInt(Objects.requireNonNull(values.get("tariff_id"))));
        contentValues.put(RECORDS_COMMENT, values.get("comment"));

        long result = db.update(TABLE_RECORDS, contentValues, "_id = ?",
                new String[]{String.valueOf(id)});

        return result != -1;
    }

    public Cursor getRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECORDS + " ORDER BY date DESC", null);
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

    public boolean deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RECORDS, RECORDS_ID + "=" + id, null) > 0;
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

    public boolean deleteTariff(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TARIFFS, TARIFFS_ID + "=" + id, null) > 0;
    }

    public boolean updateTariff(Map<String, String> values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TARIFFS_NAME, values.get("name"));
        contentValues.put(TARIFFS_PRICE, Float.parseFloat(Objects.requireNonNull(values.get("price"))));
        contentValues.put(TARIFFS_COMMENT, values.get("comment"));

        long result = db.update(TABLE_TARIFFS, contentValues, "_id = ?",
                new String[]{String.valueOf(id)});

        return result != -1;
    }

    public Cursor getTariff(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TARIFFS + " WHERE _id = " + id,
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
        contentValues.put(NOTIFICATIONS_TITLE, values.get("title"));
        contentValues.put(NOTIFICATIONS_SUBTITLE, values.get("subtitle"));
        contentValues.put(NOTIFICATIONS_DAY, Integer.parseInt(Objects.requireNonNull(values.get("day"))));

        long result = db.insertOrThrow(TABLE_NOTIFICATIONS, null, contentValues);
        return (int) result;
    }

    public Cursor getNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS, null);
    }

    public Cursor getNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE _id = " + id,
                null);
    }

    public boolean updateNotification(Map<String, String> values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATIONS_TITLE, values.get("title"));
        contentValues.put(NOTIFICATIONS_SUBTITLE, values.get("subtitle"));
        contentValues.put(NOTIFICATIONS_DAY, Integer.parseInt(Objects.requireNonNull(values.get("day"))));

        long result = db.update(TABLE_NOTIFICATIONS, contentValues, "_id = ?",
                new String[]{String.valueOf(id)});

        return result != -1;
    }

    public boolean deleteNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTIFICATIONS, NOTIFICATIONS_ID + "=" + id, null) > 0;
    }

    /*public void backup(String outFileName) {

        //database path
        final String inFileName = context.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(context, "Backup Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void restore(String inFileName) {

        final String outFileName = context.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(context, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }*/
}
