package com.makelifesimple.duplicatedetector;




//import com.google.analytics.tracking.android.EasyTracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OnDemand extends BaseActivity {

	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_demand);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_demand);
		
		tv = (TextView) findViewById(R.id.editText1);
	}

	public void sendemail(View v)
	{
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		//setType("message/rfc822")
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"makinglifeeasy4u@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "New Feature Required for Duplicate Contacts Detector");
		i.putExtra(Intent.EXTRA_TEXT   , tv.getText());
		try {
		    startActivity(Intent.createChooser(i, "Send new feature Request..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(OnDemand.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	  public void onStart() {
	    super.onStart();
	    //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }


}
