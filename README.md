## Android Simple Barcode Scanner 
ScanActivity using Google Barcode Detector

## References / 참조
* Google Mobile Vision: https://developers.google.com/vision/android/barcodes-overview
* Google Barcode Detection Codelab: https://codelabs.developers.google.com/codelabs/bar-codes/#0
* Kryptonite-android: https://github.com/kryptco/kryptonite-android

## Details / 설명
### app/MainActivity

1. check Permission (**Android API 23 above**)

need to check CAMERA permission.

2. start ScanActivity
``` java
Intent intent = new Intent(this, ScanActivity.class);
startActivityForResult(intent, REQUEST_SCAN);
```
3. print Result
``` java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
        if (data != null) {
            String scanResult = data.getStringExtra(ScanActivity.EXTRA_RESULT);
            tvResult.setText(scanResult);
        }
    }
}
```

### library/
1. class
* ScanActivity 
* CameraPreview
* graphic/GraphicOverlay

2. permission
* camera

3. build.gradle
```
compile 'com.google.android.gms:play-services:11.0.2'
```

