package com.innoaus.barcodelist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Created by ryusei on 20/10/2017.
 */

public class BarcodeItemDB {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "barcodelist";
    private static final int DATABASE_VERSION = 1;
    //"table_barcode", "id", "result", "format", "timestamp"
    private static final String TABLE_NAME = "table_barcode";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_RESULT = "result";
    private static final String COLUMN_FORMAT = "format";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String QUERY_DATABASE_CREATE = String.format("create table if not exists %s (%s integer primary key autoincrement, %s text not null, %s integer, %s text)",
            TABLE_NAME, COLUMN_ID, COLUMN_RESULT, COLUMN_FORMAT, COLUMN_TIMESTAMP);
    private final String QUERY1 = "";


    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(QUERY_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        }
    }

    private Context context;

    public BarcodeItemDB(Context context) {
        this.context = context;
        open();
    }

    private void open() throws SQLiteException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void fini() {
        close();
    }

    private void close() {
        if (db.isOpen()) {
            db.close();
        }
    }

    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    public long add(BarcodeItem item) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESULT, item.result);
        values.put(COLUMN_FORMAT, item.format);
        values.put(COLUMN_TIMESTAMP, item.timestamp);
        return db.insert(TABLE_NAME, null, values);
    }

    public boolean delete(int columnId) {
        String whereClause = String.format("%s=%d", COLUMN_ID, columnId);
        return (db.delete(TABLE_NAME, whereClause, null) > 0);
    }
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    public int getAllKeys(Collection<Integer> keys) {
        String query = String.format(Locale.getDefault(), "select * from %s order by %s;", TABLE_NAME, COLUMN_ID);
        Cursor cursor = db.rawQuery(query, null);
        if (cursorIsEmpty(cursor)) {
            return 0;
        }

        int count = cursor.getCount();
        int index = cursor.getColumnIndex(COLUMN_ID);
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int cid = cursor.getInt(index);
            keys.add(cid);
        }
        return count;
    }

    private static boolean cursorIsEmpty(Cursor c) {
        return c == null || c.getCount() == 0;
    }

    public ArrayList<BarcodeItem> getAllData() {
        ArrayList<BarcodeItem> items = new ArrayList<>();
        String query = String.format(Locale.getDefault(), "select * from %s order by %s;", TABLE_NAME, COLUMN_ID);
        Cursor cursor = db.rawQuery(query, null);
        if (!cursorIsEmpty(cursor)) {
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                String result = cursor.getString(cursor.getColumnIndex(COLUMN_RESULT));
                int format = cursor.getInt(cursor.getColumnIndex(COLUMN_FORMAT));
                String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                BarcodeItem item = new BarcodeItem(result, format, timestamp);
                items.add(item);
            }
        }
        return items;
    }
}
