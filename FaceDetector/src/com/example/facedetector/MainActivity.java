package com.example.facedetector;

import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends Activity implements OnTouchListener {

	private static final String TAG = "FaceDetector";

	private Camera mCamera = null;
	private CameraPreview mPreview = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar, notification bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Hide Softkey
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		Point displaySize = new Point();
		getWindowManager().getDefaultDisplay().getSize(displaySize);
		int displayWidth = displaySize.x;
		int displayHeight = displaySize.y;
		Log.i(TAG, "[INFO] \t Picture size: (" + displayWidth + "x" + displayHeight + ")");

		setContentView(R.layout.activity_main);

		// Camera Initialization
		Log.i(TAG, "[INFO] Camera, CameraPreview initialization");
		mCamera = getCameraInstance();

		// Print CameraInfo
		Camera.Parameters params = mCamera.getParameters();

		// picture size
		Size pictureSize = params.getPictureSize();
		Log.i(TAG, "[INFO] \t Picture size: (" + pictureSize.width + "x" + pictureSize.height + ")");
		List<Size> pictureSizeList = params.getSupportedPictureSizes();
		Log.i(TAG, "[INFO] \t Supported picture size list:");
		for (int i = 0; i < pictureSizeList.size(); i++)
		{
			Size temp = pictureSizeList.get(i);
			Log.i(TAG, "[INFO] \t\t (" + temp.width + "x" + temp.height + ")");
		}

		// preview size
		Size previewSize = params.getPreviewSize();
		Log.i(TAG, "[INFO] \t Preview size: (" + previewSize.width + "x" + previewSize.height + ")");
		Size preferredSize = params.getPreferredPreviewSizeForVideo();
		Log.i(TAG, "[INFO] \t Preferred preview size: (" + preferredSize.width + "x" + preferredSize.height + ")");

		List<Size> previewSizeList = params.getSupportedPreviewSizes();
		Log.i(TAG, "[INFO] \t Supported preview size list:");
		for (int i = 0; i < previewSizeList.size(); i++)
		{
			Size temp = previewSizeList.get(i);
			Log.i(TAG, "[INFO] \t\t (" + temp.width + "x" + temp.height + ")");
		}

		//params.setPictureSize(preferredSize.width, preferredSize.height);
		params.setPreviewSize(preferredSize.width, preferredSize.height);
		mCamera.setParameters(params);

		// CameraPreview Initialization
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		preview.setOnTouchListener(this);  // for Hiding Softkey

		LayoutParams layoutParams = preview.getLayoutParams();
		layoutParams.width = preferredSize.width;
		layoutParams.height = preferredSize.height;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "[INFO] onDestroy");
		releaseCamera();
	}

	public static Camera getCameraInstance(){
		Camera camera = null;
		try {
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		} catch (Exception error)
		{
			error.printStackTrace();
			Log.e(TAG, "[ERROR] Camera open failed." + error.getMessage() );
		}

		return camera;
	}

	public void releaseCamera(){
		if (mCamera != null)
		{
			Log.i(TAG, "[INFO] release Camera");
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		// Hide Softkey
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		return false;
	}
}
