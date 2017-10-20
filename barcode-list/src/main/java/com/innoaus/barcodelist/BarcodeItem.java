package com.innoaus.barcodelist;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ryusei on 20/10/2017.
 */

public class BarcodeItem {
    String result;
    String format;
    String timestamp;

    public BarcodeItem() {
    }

    // added from user view
    public BarcodeItem(String r, String f) {
        result = r;
        format = f;
        Date currentTime = Calendar.getInstance().getTime();
        timestamp = currentTime.toString();
    }

    // added from db
    public BarcodeItem(String r, String f, String t) {
        result = r;
        format = f;
        timestamp = t;
    }
}
