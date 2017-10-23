package com.innoaus.barcodelist;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by ryusei on 20/10/2017.
 */

public class BarcodeItemManager {
    private static BarcodeItemManager instance;

    synchronized public static BarcodeItemManager getInstance(Context context) {
        if (instance == null) {
            instance = new BarcodeItemManager(context);
        }
        return instance;
    }

    ArrayList<BarcodeItem> items;
    Context context;
    BarcodeItemDB db;

    private BarcodeItemManager(Context context) {
        items = new ArrayList<>();
        db = new BarcodeItemDB(context);
        loadItems();
    }

    public void addItem(String result, int format) {
        BarcodeItem item = new BarcodeItem(result, format);
        long ret = db.add(item);
        item.columnId = ret;
        items.add(item);
    }

    private void loadItems() {
        items = db.getAllData();
    }

    public String getItemText(int position) {
        return items.get(position).result;
    }

    public ArrayList<BarcodeItem> getItems() {
        return items;
    }

    public int getItemCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    public void swap(int from, int to) {
        Collections.swap(items, from, to);
    }

    public void removeItem(int position) {
        BarcodeItem item = items.get(position);
        db.delete(item.columnId);
        items.remove(item);
    }

    private void log(String m) {
        Log.d("barcode-list", "BarcodeItemManager@@ " + m);
    }
}
