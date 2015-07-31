package com.dft.onyximagery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.enroll.util.CaptureAnimationCallbackUtil;
import com.dft.onyx.enroll.util.EnrollmentMetric;
import com.dft.onyx.enroll.util.LicenseCheckerUtil;
import com.dft.onyx.enroll.util.OnyxFragmentBuilder;
import com.dft.onyx.enroll.util.imageareas.EnumFinger;
import com.dft.onyx.onyx_enroll_wizard.R;
import com.dft.onyx.wizardroid.ContextVariable;
import com.dft.onyx.wizardroid.WizardActivity;
import com.dft.onyx.wizardroid.WizardStep;
import com.dft.onyxcamera.ui.CaptureMetrics;
import com.dft.onyxcamera.ui.OnyxFragment;

public class OnyxImageryStep1 extends WizardStep {
	private View mView;
	private Context mContext;
	private WizardActivity mWizardActivity;
	private OnyxFragment mFragment;
	private EnrollmentMetric mEnrolledEnrollmentMetric;
    private Animation mFadeIn;
    private Animation mFadeOut;
	private ImageView innerSpinner;
    private ImageView outerSpinner;
    private OnyxFragment.CaptureAnimationCallback mCaptureAnimationCallback;
    private ImageView mPreviewFingerprintView;
	
	@ContextVariable
	private String mTimeStamp = null;
	@ContextVariable
	private byte[] mRawBytes = null;
	@ContextVariable
	private byte[] mProcessedBytes = null;
	@ContextVariable
	private byte[] mEnhancedBytes = null;
	
	@ContextVariable
    private EnumFinger enumFinger = null;
	

	// Must have an empty constructor for every step
	public OnyxImageryStep1() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.base_layout, container, false); 
		mContext = mWizardActivity = (WizardActivity) getActivity();
		
		LicenseCheckerUtil.validateLicense(mContext,
				mContext.getString(com.dft.onyximagery.R.string.onyx_license));
		
		setupLayout();
		return mView;
	}

	private void setupLayout() {
		// Hide the Action Bar
    	if (null != getActivity().getActionBar()) {
        	getActivity().getActionBar().hide();
    	}

        createFadeInAnimation();
        createFadeOutAnimation();

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mPreviewFingerprintView = new ImageView(mContext);
        mWizardActivity.addContentView(mPreviewFingerprintView, layoutParams);
        
        // Set progress bar to gone
        mView.findViewById(R.id.base_layout_progress_bar).setVisibility(View.GONE);
        outerSpinner = (ImageView) mView.findViewById(R.id.base_layout_spinner_outer);
        innerSpinner = (ImageView) mView.findViewById(R.id.base_layout_spinner_inner);
        mCaptureAnimationCallback = new	CaptureAnimationCallbackUtil().createCaptureAnimationCallback(mWizardActivity);
	}
	
	private void createFadeInAnimation() {
		mFadeIn = new AlphaAnimation(0.0f, 1.0f);
        mFadeIn.setDuration(500);
        mFadeIn.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                new CountDownTimer(1000, 1000) {

                    @Override
                    public void onFinish() {
                        mPreviewFingerprintView.startAnimation(mFadeOut);
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                }.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                mPreviewFingerprintView.setVisibility(View.VISIBLE);
            }
        });
	}
	
	private void createFadeOutAnimation() {
		mFadeOut = new AlphaAnimation(1.0f, 0.0f);
        mFadeOut.setDuration(500);
        mFadeOut.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                mPreviewFingerprintView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
	}

	@Override
	public void onResume() {
		super.onResume();

		mEnrolledEnrollmentMetric = new EnrollmentMetric(0.9f, enumFinger);

		hideSpinner();

		// Add the OnyxFragment programatically
		mFragment = new OnyxFragmentBuilder(mWizardActivity, R.id.fragment_content)
			.setEnrollmentMetric(mEnrolledEnrollmentMetric)
			.setRawBitmapCallback(mRawBitmapCallback)
			.setProcessedBitmapCallback(mProcessedBitmapCallback)
			.setEnhancedBitmapCallback(mEnhancedBitmapCallback)
			.setFingerprintTemplateCallback(mFingerprintTemplateCallback)
			.setCaptureAnimationCallback(mCaptureAnimationCallback)
			.build();
	}
	
	private OnyxFragment.RawBitmapCallback mRawBitmapCallback = new OnyxFragment.RawBitmapCallback() {

		@Override
		public void onRawBitmapReady(Bitmap rawBitmap) {
			mRawBytes = convertBitmapToBytes(rawBitmap);
		}		
	};
	
	private OnyxFragment.ProcessedBitmapCallback mProcessedBitmapCallback =
			new OnyxFragment.ProcessedBitmapCallback() {

		@Override
		public void onProcessedBitmapReady(Bitmap processedBitmap, CaptureMetrics captureMetrics) {
			mProcessedBytes = convertBitmapToBytes(processedBitmap);
		}
	};

	private OnyxFragment.EnhancedBitmapCallback mEnhancedBitmapCallback =
			new OnyxFragment.EnhancedBitmapCallback() {

		@Override
		public void onEnhancedBitmapReady(Bitmap enhancedBitmap, CaptureMetrics captureMetrics) {
			mEnhancedBytes = convertBitmapToBytes(enhancedBitmap);
		}
	};

	private OnyxFragment.FingerprintTemplateCallback mFingerprintTemplateCallback =
			new OnyxFragment.FingerprintTemplateCallback() {

		@Override
		public void onFingerprintTemplateReady(FingerprintTemplate fingerprintTemplate) {
			closeFragment();
			done();
		}
	};
	
	private void closeFragment() {
        if (null != mFragment) {
        	FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(mFragment);
            ft.commit();
        }
	}
	
	private void hideSpinner() {
        if (null != innerSpinner) {
        	innerSpinner.setVisibility(View.GONE);
        }
        
		if (null != outerSpinner) {
			View baseLayoutSpinnerOuterView = getView().findViewById(R.id.base_layout_spinner_outer);
			baseLayoutSpinnerOuterView.clearAnimation();
			outerSpinner.setVisibility(View.GONE);
		}
	}
	
	private byte[] convertBitmapToBytes(Bitmap bitmap) {
		byte[] bitmapInBytes = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		bitmapInBytes = outStream.toByteArray();
		try{
			outStream.close();
			outStream = null;
			}
		catch (IOException e){
			e.printStackTrace();
			}
		return bitmapInBytes;
	}
}
