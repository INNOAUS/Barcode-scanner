package com.innoaus.barcodescanner.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.innoaus.barcodescanner.ScanActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_scan).setOnClickListener(this);
        tvResult = (TextView) findViewById(R.id.text_result);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_scan) {
            checkPermission();
        }
    }

    private void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, ScanActivity.REQUEST_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ScanActivity.REQUEST_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String scanResult = data.getStringExtra(ScanActivity.EXTRA_RESULT);
                tvResult.setText(scanResult);
            }
        } else {
        }
    }

    final int PERMISSIONS_REQUEST_CAMERA = 201;

    public void checkPermission() {
        int result = checkSelf(Manifest.permission.CAMERA);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        } else {
            startScan();
        }
    }

    private int checkSelf(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
