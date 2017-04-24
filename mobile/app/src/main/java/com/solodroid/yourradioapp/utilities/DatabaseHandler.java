package com.solodroid.yourradioapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.solodroid.yourradioapp.models.ItemListRadio;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AddtoFav";
    private static final String TABLE_NAME = "Favorite";
    private static final String KEY_ID = "id";
    private static final String KEY_RADIOID = "radioidid";
    private static final String KEY_RADIO_URL = "radiourlurl";
    private static final String KEY_RADIO_IMAGE = "radioimage";
    private static final String KEY_RADIO_NAME = "radioname";
    private static final String KEY_RADIO_CATEGORY_NAME = "radiocategoryname";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_RADIOID + " TEXT,"
                + KEY_RADIO_NAME + " TEXT,"
                + KEY_RADIO_CATEGORY_NAME + " TEXT,"
                + KEY_RADIO_URL + " TEXT,"
                + KEY_RADIO_IMAGE + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    //Adding Record in Database

    public void AddtoFavorite(ItemListRadio pj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RADIOID, pj.getRadioId());
        values.put(KEY_RADIO_NAME, pj.getRadioName());
        values.put(KEY_RADIO_CATEGORY_NAME, pj.getRadioCategoryName());
        values.put(KEY_RADIO_URL, pj.getRadiourl());
        values.put(KEY_RADIO_IMAGE, pj.getRadioImageurl());
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection

    }

    // Getting All Data
    public List<ItemListRadio> getAllData() {
        List<ItemListRadio> dataList = new ArrayList<ItemListRadio>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemListRadio contact = new ItemListRadio();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setRadioId(cursor.getString(1));
                contact.setRadioName(cursor.getString(2));
                contact.setRadioCategoryName(cursor.getString(3));
                contact.setRadiourl(cursor.getString(4));
                contact.setRadioImageurl(cursor.getString(5));
                // Adding contact to list
                dataList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return dataList;
    }

    //getting single row

    public List<ItemListRadio> getFavRow(String id) {
        List<ItemListRadio> dataList = new ArrayList<ItemListRadio>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE radioidid=" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemListRadio contact = new ItemListRadio();

                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setRadioId(cursor.getString(1));
                contact.setRadioName(cursor.getString(2));
                contact.setRadioCategoryName(cursor.getString(3));
                contact.setRadiourl(cursor.getString(4));
                contact.setRadioImageurl(cursor.getString(5));

                // Adding contact to list
                dataList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return dataList;
    }

    //for remove favorite

    public void RemoveFav(ItemListRadio contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_RADIOID + " = ?",
                new String[]{String.valueOf(contact.getRadioId())});
        db.close();
    }

    public enum DatabaseManager {
        INSTANCE;
        private SQLiteDatabase db;
        private boolean isDbClosed = true;
        DatabaseHandler dbHelper;

        public void init(Context context) {
            dbHelper = new DatabaseHandler(context);
            if (isDbClosed) {
                isDbClosed = false;
                this.db = dbHelper.getWritableDatabase();
            }

        }

        public boolean isDatabaseClosed() {
            return isDbClosed;
        }

        public void closeDatabase() {
            if (!isDbClosed && db != null) {
                isDbClosed = true;
                db.close();
                dbHelper.close();
            }
        }
    }
}
