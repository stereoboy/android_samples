package com.example.simplecamera;

import java.util.Arrays;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	
	private final static String TAG = "SimpleCamera";
	private TextureView mTextureView = null;
	private TextureView.SurfaceTextureListener mSurfaceTextureListner = new TextureView.SurfaceTextureListener() {
		
		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {
			// TODO Auto-generated method stub
			//Log.i(TAG, "onSurfaceTextureUpdated()");
			
		}
		
		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
				int height) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onSurfaceTextureSizeChanged()");

		}
		
		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onSurfaceTextureDestroyed()");
			return false;
		}
		
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
				int height) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onSurfaceTextureAvailable()");
			
			CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
			try{
				String cameraId = manager.getCameraIdList()[0];
				CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
				StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
				mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
				
				manager.openCamera(cameraId, mStateCallback, null);
			}
			catch(CameraAccessException e)
			{
				e.printStackTrace();
			}
			
		}
	};
	
	private Size mPreviewSize = null;
	private CameraDevice mCameraDevice = null;
	private CaptureRequest.Builder mPreviewBuilder = null;
	private CameraCaptureSession mPreviewSession = null;
	private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
		
		@Override
		public void onOpened(CameraDevice camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onOpened");
			mCameraDevice = camera;
			
			SurfaceTexture texture = mTextureView.getSurfaceTexture();
			if (texture == null) {
				Log.e(TAG, "texture is null");
				return;
			}
			
			texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
			Surface surface = new Surface(texture);
			
			try {
				mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			} catch (CameraAccessException e){
				e.printStackTrace();
			}
			
			mPreviewBuilder.addTarget(surface);
			
			try {
				mCameraDevice.createCaptureSession(Arrays.asList(surface), mPreviewStateCallback, null);
			} catch (CameraAccessException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onError(CameraDevice camera, int error) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onError");
			
		}
		
		@Override
		public void onDisconnected(CameraDevice camera) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onDisconnected");
			
		}
	};
	private CameraCaptureSession.StateCallback mPreviewStateCallback = new CameraCaptureSession.StateCallback() {
		
		@Override
		public void onConfigured(CameraCaptureSession session) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onConfigured");
			mPreviewSession = session;
			
			mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
			
			HandlerThread backgroundThread = new HandlerThread("CameraPreview");
			backgroundThread.start();
			Handler backgroundHandler = new Handler(backgroundThread.getLooper());
			
			try {
				mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
			} catch (CameraAccessException e) {
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void onConfigureFailed(CameraCaptureSession session) {
			// TODO Auto-generated method stub
			Log.e(TAG, "CameraCaptureSession Configure failed");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//same as set-up android:screenOrientation="portrait" in <activity>, AndroidManifest.xml
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		
		mTextureView = (TextureView) findViewById(R.id.textureView1);
		mTextureView.setSurfaceTextureListener(mSurfaceTextureListner);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (mCameraDevice != null)
		{
			mCameraDevice.close();
			mCameraDevice = null;
		}
	}
	
	
}
