package com.innoaus.barcodelist;

import android.util.Log;

import com.google.android.gms.vision.barcode.Barcode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ryusei on 20/10/2017.
 */

public class BarcodeItem {
    long columnId;
    String result;
    int format;
    String timestamp;

    // added from user view
    public BarcodeItem(String r, int f) {
        columnId = -1;
        result = r;
        format = f;
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // added from db
    public BarcodeItem(long cid, String r, int f, String t) {
        columnId = cid;
        result = r;
        format = f;
        timestamp = t;
    }

    public String getFormat() {
        switch (format) {
            case Barcode.CODE_39:
                return "CODE 39";
            case Barcode.CODE_128:
                return "CODE 128";
            case Barcode.QR_CODE:
                return "QR CODE";
            case Barcode.DATA_MATRIX:
                return "DATA MATRIX";
        }
        return "";
    }
}
