package com.example.facedetector;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, FaceDetectionListener {

	private static final String TAG = "FaceDetector::CameraView";
	private static final int colorTable[] = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

	private SurfaceHolder 	mHolder 	= null;
	private Camera 			mCamera 	= null;
	private int				mCameraId 	= 0;
	private final Paint[]   mPaintList 	= new Paint[colorTable.length];
	private int 			mViewWidth 	= 0;
	private int 			mViewHeight = 0;

	private Face[] 			mFaces 		= null;

	public CameraPreview(Context context, Camera camera, int cameraId) {
		super(context);
		// TODO Auto-generated constructor stub
		// Declare drawing
		setWillNotDraw(false);
		mCamera = camera;
		mCameraId = cameraId;

		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mCamera.setFaceDetectionListener(this);

		for(int i = 0; i < mPaintList.length; i++)
		{
			mPaintList[i] = new Paint();
			mPaintList[i].setColor(colorTable[i]);
			mPaintList[i].setStyle(Paint.Style.STROKE);
			mPaintList[i].setStrokeWidth(7);
		}
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

		mViewWidth = width;
		mViewHeight = height;

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

			int MaxNumDetectedFaces = mCamera.getParameters().getMaxNumDetectedFaces();
			Log.i(TAG, "[INFO] MaxNumDetectedFaces: " + MaxNumDetectedFaces);
			if (MaxNumDetectedFaces > 0)
				mCamera.startFaceDetection();

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

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Log.i(TAG, "[INFO] OnDraw()");

		//Coordinate Transform
		if (mFaces != null)
		{
			for (int i = 0; i < mFaces.length; i++)
			{
				Paint paint = mPaintList[i%mPaintList.length];
				canvas.drawRect(transFromCameraToView(mFaces[i].rect), paint);
			}

			mFaces = null;
		}

		//canvas.drawRect(new Rect(0, 0, 400, 400), mPaintList[0]);
	}

	private Rect transFromCameraToView(Rect cameraRect) {


		int left 	= (int)((float)mViewWidth/2000 * (cameraRect.left + 1000));
		int top 	= (int)((float)mViewHeight/2000 * (cameraRect.top + 1000));
		int right 	= (int)((float)mViewWidth/2000 * (cameraRect.right + 1000));
		int bottom 	= (int)((float)mViewHeight/2000 * (cameraRect.bottom + 1000));

		// Flip for Front Camera
		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			left 	= (int)((float)mViewWidth/2000 * (-cameraRect.right + 1000));
			right 	= (int)((float)mViewWidth/2000 * (-cameraRect.left + 1000));
		}

		Rect viewRect = new Rect( left, top, right, bottom);
		return viewRect;
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		// TODO Auto-generated method stub
		Log.i(TAG, "[INFO] Face Detect: " + faces.length);
		for (int i =  0; i < faces.length; i++)
		{
			Log.i(TAG, "[INFO] \t["+i+"] (" + faces[i].rect.left + "x" + faces[i].rect.right + ", "
					+ faces[i].rect.top + "x" + faces[i].rect.bottom + ", "
					+ faces[i].rect.width() + "x" + faces[i].rect.height() + ")" );
		}

		mFaces = faces;
		invalidate();
	}
}