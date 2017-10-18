package com.innoaus.barcodescanner;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView {
    SurfaceHolder holder;

    public CameraPreview(Context context, SurfaceHolder.Callback cb) {
        super(context);

        holder = getHolder();
        holder.addCallback(cb);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
}