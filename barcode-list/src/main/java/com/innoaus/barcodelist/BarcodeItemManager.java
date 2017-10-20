package com.innoaus.barcodelist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ryusei on 20/10/2017.
 */

public class BarcodeItemManager {
    private static BarcodeItemManager instance;

    synchronized public static BarcodeItemManager getInstance() {
        if (instance == null) {
            instance = new BarcodeItemManager();
        }
        return instance;
    }

    ArrayList<BarcodeItem> items;

    private BarcodeItemManager() {
        items = new ArrayList<>();
    }

    public void addItem(String result, String format) {
        BarcodeItem item = new BarcodeItem();
        item.result = result;
        Date currentTime = Calendar.getInstance().getTime();
        item.timestamp = currentTime.toString();
        item.format = format;
        items.add(item);
    }

    public ArrayList<BarcodeItem> getItems() {
        return items;
    }

    public int getItemCount() {
        if (items == null)
            return 0;
        return items.size();
    }
}
