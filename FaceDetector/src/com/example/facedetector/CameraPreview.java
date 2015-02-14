package com.example.facedetector;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "FaceDetector::CameraView";
	private SurfaceHolder 	mHolder = null;
	private Camera 			mCamera = null;
	public CameraPreview(Context context, Camera camera) {
		super(context);
		// TODO Auto-generated constructor stub
		mCamera = camera;
		
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "[INFO] surfaceCreated");
		try {
			mCamera.setPreviewDisplay(holder);
			//mCamera.startPreview();
		} catch (IOException error) {
			error.printStackTrace();
			Log.e(TAG, "[ERROR] surfaceCreated failed: " + error.getMessage());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.i(TAG, "[INFO] surfaceChanged width x height = (" + width + "x" + height + ")");
		
		if (holder.getSurface() == null) 
		{
			return;
		}
		
		try {
			mCamera.stopPreview();
		} catch (Exception error) {
			error.printStackTrace();
			Log.e(TAG, "[ERROR] surfaceChanged() stopPreview() failed: " + error.getMessage());
		}
		
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException error) {
			error.printStackTrace();
			Log.e(TAG, "[ERROR] surfaceChanged() startPreview failed: " + error.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "[INFO] surfaceDestroyed");
		try {
			mCamera.stopPreview();
			mCamera.setPreviewDisplay(null);
		} catch (IOException error) {
			error.printStackTrace();
			Log.e(TAG, "[ERROR] surfaceDestroyed() stopPreview() failed: " + error.getMessage());
		}
	}
	
}