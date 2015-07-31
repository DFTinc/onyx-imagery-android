package com.dft.onyximagery;

import org.opencv.android.OpenCVLoader;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.dft.onyx.core;
import com.dft.onyx.wizardroid.WizardFlow;
import com.dft.onyx.wizardroid.enrollwizard.EnrollFingerSelect;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizard;

public class ImageryWizard extends EnrollWizard {
	private static final String TAG = "ImageryWizard";
	static {
    	if(!OpenCVLoader.initDebug()) {
    		Log.d(TAG, "Unable to load OpenCV!");
    	} else {
    		Log.i(TAG, "OpenCV loaded successfully");
            core.initOnyx();
        }
    }
	
	public void onCreate(Bundle savedInstanceState) {
        setContentViewId(R.layout.onyximagery_wizard);
        if (null != getActionBar()) {
            getActionBar().hide();
       	}

        setEnrollWizardFlow(new WizardFlow.Builder()
                .setActivity(this)
                .setContainerId(R.id.step_container)
                .addStep(EnrollFingerSelect.class)
                .addStep(OnyxImageryStep1.class)
                .addStep(OnyxImageryStep2.class)
                .create());

        super.onCreate(savedInstanceState);
    }

    //You must override this method and create a wizard flow by
    //using WizardFlow.Builder as shown in this example
    @Override
    public void onSetup(WizardFlow flow) {


        //Call the super method using the newly created flow
        super.onSetup(flow);
    }
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Log.v(TAG, "Going back one step");
//
//            if (0 == getWizard().getCurrentStepPosition()) {
//            	onWizardDone();
//            } else {
//            	getWizard().getCurrentStep().abort();
//            }
//			return true;
//    	}
//    	return super.onKeyDown(keyCode, event);
//    }

    //Overriding this method is optional
    @Override
    public void onWizardDone() {
        //Do whatever you want to do once the Wizard is complete
        //in this case I just close the activity, which causes Android
        //to go back to the previous activity.
    	// Set RESULT_OK back for any calling startActivityForResult
    	setResult(RESULT_OK);
        finish();
    }

    @Override
    public int getContentViewId() {
        return R.layout.onyximagery_wizard;
    }

    @Override
    public void setContentViewId(int i) {
    }

    @Override
    public String getOnyxLicenseKey() {
        return getResources().getString(R.string.onyx_license);
    }

    @Override
    public void setOnyxLicenseKey(String s) {
    }

    @Override
    public String getUID() {
        return null;
    }

    @Override
    public void setUID(String s) {
    }

    @Override
    public int getNumStepsBeforeEnrollStepCapture() {
        return 0;
    }

    @Override
    public void setNumStepsBeforeEnrollStepCapture(int i) {
    }
}
