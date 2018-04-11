package com.makelifesimple.duplicatedetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class TopConatcts extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_conatcts);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top_conatcts, menu);
		return true;
	}

}
