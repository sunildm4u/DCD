package com.makelifesimple.duplicatedetector;

//import com.google.analytics.tracking.android.EasyTracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class Ratethisapp extends BaseActivity {

	RatingBar ratingBar;
	Uri uri = Uri.parse("market://details?id=" + "com.makelifesimple.duplicatedetector");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ratethisapp);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(final RatingBar ratingBar, float rating,boolean fromUser)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Ratethisapp.this, R.style.AlertDialogCustom));
				
				//AlertDialog.Builder builder = new AlertDialog.Builder(Ratethisapp.this);
				
				
	 if(rating<3)
	 {
		 builder.setMessage("Please tell us what went wrong ? We will fix for you");
			builder.setCancelable(false);
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							sendemail();	
						}
					});
			builder.setNegativeButton("Never",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							//intent.setData(Uri.parse("market://details?id= com.simplelife.duplicatecontactchecker"));
							startActivity(intent);
						}
					});
		 
		 
	 }else{
		  
		 builder.setMessage("Please RATE us in the next google play screen, This matters a lot for us");
			builder.setCancelable(false);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							//intent.setData(Uri.parse("market://details?id= com.simplelife.duplicatecontactchecker"));
							startActivity(intent);	
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Toast.makeText(Ratethisapp.this, "This App is Not Rated,Pls Rate", Toast.LENGTH_LONG).show();
							//ratingBar.setRating(0);
							//ratingBar.setOnRatingBarChangeListener(null);
							dialog.cancel();
						}
					});
		 
	 }
	 AlertDialog alertDialog = builder.create();
		alertDialog.show();
		
	 
			}
		});
		
	}
	
	public void feedback(View view)
	{
		
		sendemail();
		
		//Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL); intent.setData(Uri.parse("market://details?id=com.makelifesimple.duplicatedetector")); startActivity(intent);
		
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	   // EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	   // EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
	
   public void sendemail(){
	   Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"makinglifeeasy4u@gmail.com"});
		//i.putExtra(Intent.EXTRA_STREAM,uri);
		i.putExtra(Intent.EXTRA_SUBJECT, "App Feedback for Duplicate Contact Detector");
		//i.putExtra(Intent.EXTRA_TEXT   , "Find Attached the File with SMS Conversation");
		try {
		    startActivity(Intent.createChooser(i, "Send feedback mail..."));
		    Ratethisapp.this.finish();
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(Ratethisapp.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}

		
   }
}
