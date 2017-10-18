package com.innoaus.barcodescanner;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.innoaus.barcodescanner.graphic.BarcodeGraphic;
import com.innoaus.barcodescanner.graphic.BarcodeTrackerFactory;
import com.innoaus.barcodescanner.graphic.GraphicOverlay;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScanActivity extends AppCompatActivity {
    public static final String EXTRA_RESULT = "scan_result";

    BarcodeDetector detector;
    CameraPreview preview;
    FrameLayout previewLayout;
    LinkedBlockingQueue<byte[]> frames;

    ScanThread scanThread;
    Camera camera;
    Camera.Size previewSize;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphic_overlay);
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);

        detector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE | Barcode.CODE_128)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());
        if (!detector.isOperational()) {
            Intent intent = new Intent();
            intent.putExtra("error", "Could not set up the detector");
            setResult(RESULT_CANCELED);
            finish();
        }

        frames = new LinkedBlockingQueue<>(1);
        preview = new CameraPreview(this, surfaceHolderCallback);
        previewLayout = (FrameLayout) findViewById(R.id.preview);
        previewLayout.addView(preview);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                camera.release();
                camera = null;
                e.printStackTrace();
            }

            camera.setPreviewCallback(cameraPreviewCallback);
            previewSize = camera.getParameters().getPreviewSize();
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            int previewWidth = params.getPreviewSize().width;
            int previewHeight = params.getPreviewSize().height;
            if (scanThread == null) {
                scanThread = new ScanThread(ScanActivity.this, previewWidth, previewHeight);
                scanThread.start();
            }

            if (mGraphicOverlay != null) {
                Size size = new Size(previewWidth, previewHeight);
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                // Swap width and height sizes when in portrait, since it will be rotated by
                // 90 degrees
                mGraphicOverlay.setCameraInfo(min, max, Camera.CameraInfo.CAMERA_FACING_BACK);
                mGraphicOverlay.clear();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Camera.Parameters params = camera.getParameters();
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.setRotation(90);
                camera.setDisplayOrientation(90);
            } else {
            }
            params.setPreviewSize(width, height);
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            camera.stopPreview();
            camera = null;
        }
    };

    Camera.PreviewCallback cameraPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (data == null)
                return;

            frames.offer(data);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        frames.clear();
    }

    class ScanThread extends Thread {
        ScanThread self;
        Context context;
        AtomicBoolean stopped;
        int width;
        int height;

        public ScanThread(Context context, int width, int height) {
            self = this;
            self.context = context;
            stopped = new AtomicBoolean(false);
            self.width = width;
            self.height = height;
        }

        @Override
        public void run() {
            while (true) {
                if (stopped.get()) {
                    return;
                }
                try {
                    byte[] data = frames.poll(500, TimeUnit.MILLISECONDS);
                    if (data != null) {
                        Bitmap bitmap = Bitmap.createBitmap(self.width, self.height, Bitmap.Config.ARGB_8888);
                        Allocation bmData = renderScriptNV21ToRGBA888(
                                self.context,
                                self.width,
                                self.height,
                                data);
                        bmData.copyTo(bitmap);
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<Barcode> barcodes = detector.detect(frame);
                        for (int i = 0; i < barcodes.size(); i++) {
                            final Barcode barcode = barcodes.valueAt(i);
                            if (barcode == null) {
                                continue;
                            }

                            Frame outputFrame = new Frame.Builder()
                                    .setBitmap(bitmap)
                                    .setRotation(45)
                                    .build();
                            detector.receiveFrame(outputFrame);
                            if (barcode.rawValue != null) {
                                //parsing or print rawValue
                                sleep(500);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        terminate();
                                        Intent data = new Intent();
                                        data.putExtra(EXTRA_RESULT, barcode.rawValue);
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                });
                                return;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private Allocation renderScriptNV21ToRGBA888(Context context, int width, int height, byte[] nv21) {
            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

            Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
            Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
            Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

            in.copyFrom(nv21);

            yuvToRgbIntrinsic.setInput(in);
            yuvToRgbIntrinsic.forEach(out);
            return out;
        }

        public void terminate() {
            stopped.set(true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Camera.AutoFocusCallback cb = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if (b) {
                }
            }
        };
        try {
            camera.autoFocus(cb);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }
}
