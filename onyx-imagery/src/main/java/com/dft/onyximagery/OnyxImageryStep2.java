package com.dft.onyximagery;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.dft.onyx.wizardroid.ContextVariable;
import com.dft.onyx.wizardroid.WizardStep;

public class OnyxImageryStep2 extends WizardStep implements OnClickListener{
	private static final String TAG = "OnyxImageryStep2";

	@ContextVariable
	private byte[] mRawBytes;
	@ContextVariable
	private byte[] mProcessedBytes;
	@ContextVariable
	private byte[] mEnhancedBytes;

	private Bitmap mRawBitmap;
	private Bitmap mProcessedBitmap;
	private Bitmap mEnhancedBitmap;

	private DateFormat formatter = new SimpleDateFormat("yyyMMdd_hhmmss", Locale.US);
	private File mRawFile;
	private File mPreprocessedFile;
	private File mEnhancedFile;

	private String mTimeStamp = null;
	private static final String ONYX_IMAGERY_DEMO_FOLDER = "/OnyxImageryDemo";
	private static final String CAPTURE_FOLDER = "/Capture_";
	private static final String RAW_FILE = "/Raw_";
	private static final String PREPROCESSED_FILE = "/Preprocessed_";
	private static final String ENHANCED_FILE = "/Enhanced_";	
	private static final String FILE_TYPE = ".PNG";


	private Context mContext;
	private Activity mActivity;
	private ImageView mFingerprintViewRaw;
	private ImageView mFingerprintViewPreprocessed;
	private ImageView mFingerprintViewEnhanced;
	private Button btnCancel;
	private Button btnSave;

	// Must have an empty constructor for every step
	public OnyxImageryStep2() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.save_images_layout, container, false);
		mContext = mActivity = getActivity();
		mFingerprintViewRaw = (ImageView) v.findViewById(R.id.save_images_fingerprint_image_raw);
		mFingerprintViewPreprocessed = (ImageView) v.findViewById(R.id.save_images_fingerprint_image_preprocessed);
		mFingerprintViewEnhanced = (ImageView) v.findViewById(R.id.save_images_fingerprint_image_enhanced);
		btnCancel = (Button) v.findViewById(R.id.save_images_btn_cancel);
		btnSave = (Button) v.findViewById(R.id.save_images_btn_save);
		btnCancel.setOnClickListener(this);
		btnSave.setOnClickListener(this);


		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Restore and set bitmaps
		mRawBitmap = convertBytesToBitmap(mRawBytes);
		mProcessedBitmap = convertBytesToBitmap(mProcessedBytes);
		mEnhancedBitmap = convertBytesToBitmap(mEnhancedBytes);

		mFingerprintViewRaw.setImageBitmap(mRawBitmap);
		mFingerprintViewPreprocessed.setImageBitmap(mProcessedBitmap);
		mFingerprintViewEnhanced.setImageBitmap(mEnhancedBitmap);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_images_btn_cancel:
			mActivity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
			break;
		case R.id.save_images_btn_save:
			mTimeStamp = formatter.format(new Date());
			File folder = new File(Environment.getExternalStorageDirectory() + ONYX_IMAGERY_DEMO_FOLDER);
			boolean folderExists = false;
			if (!folder.exists()) {
				Log.d(TAG,"Directory Does Not Exist, Create It");
				folderExists = folder.mkdir();
			} else {
				folderExists = true;
			}
			if (folderExists) {
				Log.d(TAG, "Directory Exists");
				File subFolder = new File(Environment.getExternalStorageDirectory() + ONYX_IMAGERY_DEMO_FOLDER 
						+ CAPTURE_FOLDER + mTimeStamp);
				if (!subFolder.exists()) {
					if (subFolder.mkdir()) {
						mRawFile = new File(Environment.getExternalStorageDirectory() + ONYX_IMAGERY_DEMO_FOLDER 
								+ CAPTURE_FOLDER + mTimeStamp + RAW_FILE + mTimeStamp + FILE_TYPE);
						mPreprocessedFile = new File(Environment.getExternalStorageDirectory() + ONYX_IMAGERY_DEMO_FOLDER 
								+ CAPTURE_FOLDER + mTimeStamp + PREPROCESSED_FILE + mTimeStamp + FILE_TYPE);
						mEnhancedFile = new File(Environment.getExternalStorageDirectory() + ONYX_IMAGERY_DEMO_FOLDER 
								+ CAPTURE_FOLDER + mTimeStamp + ENHANCED_FILE + mTimeStamp + FILE_TYPE);	        		
					} else {
						Log.d(TAG, "Failed to make Capture sub folder");
					}
				}
			} else {
				Log.d(TAG, "Failed to make OnyxImageryDemo Folder - Error");
			}
			convertBitmapToFile(mRawBitmap, mRawFile);
			convertBitmapToFile(mProcessedBitmap, mPreprocessedFile);
			convertBitmapToFile(mEnhancedBitmap, mEnhancedFile);
			galleryAddPic(mRawFile);
			galleryAddPic(mPreprocessedFile);
			galleryAddPic(mEnhancedFile);
			mActivity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
			break;
		}		
	}

	private Bitmap convertBytesToBitmap (byte[] bytes) {
		ByteArrayInputStream imageStream = new ByteArrayInputStream(bytes);
		return BitmapFactory.decodeStream(imageStream);	
	}	

	/**
	 * @param bitmap
	 */
	private void convertBitmapToFile(Bitmap bitmap, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void galleryAddPic(File image) {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    Uri contentUri = Uri.parse("file://" + image.getAbsolutePath());
	    mediaScanIntent.setData(contentUri);
	    mContext.sendBroadcast(mediaScanIntent);
	}
}
