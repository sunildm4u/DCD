package com.makelifesimple.duplicatedetector;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



public class BaseActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AnalticsApplication application = (AnalticsApplication) getApplication();
		//mTracker = application.getDefaultTracker();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, 1, 1, "Report Issue").setIcon(R.drawable.saveicon);
		menu.add(0, 2, 2, "Like It ?- Rate App pls").setIcon(R.drawable.saveicon);
		menu.add(0, 3, 3, "FAQ - Questions").setIcon(R.drawable.saveicon);
		menu.add(0, 4, 4, "Privacy Policy").setIcon(R.drawable.importicon);
		menu.add(0, 5, 5, "Exit App").setIcon(R.drawable.sad);
		//getMenuInflater().inflate(R.menu.remover1, menu);
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		case 1:
			   Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"makinglifeeasy4u@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT, "Report Issue for Duplicate Contact Detector");
				
				try {
				    startActivity(Intent.createChooser(i, "Report Issue mail..."));
				    this.finish();
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}

		    
		 
		    return true;

		case 2:
			Uri uri = Uri.parse("market://details?id=" + "com.makelifesimple.duplicatedetector");
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			//intent.setData(Uri.parse("market://details?id= com.simplelife.duplicatecontactchecker"));
			startActivity(intent);	
			return true;
			
		case 3:
			  this.finish();
			  
			  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://makelifesimple4u.weebly.com/faq.html"));
			  startActivity(browserIntent);
		    return true;

		case 4:

			String url = "http://makinglifeeasy4u.weebly.com/privacy-policy.html";
			Intent objint = new Intent(Intent.ACTION_VIEW);
			objint.setData(Uri.parse(url));
			startActivity(objint);
				return true;

		case 5:
			  this.finish();
			  Intent startMain = new Intent(Intent.ACTION_MAIN);
		        startMain.addCategory(Intent.CATEGORY_HOME);
		        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(startMain);
		    return true;

		default:break;
		    //code here
		}
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	  public void onStart() {
	    super.onStart();

	  //  EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
}
