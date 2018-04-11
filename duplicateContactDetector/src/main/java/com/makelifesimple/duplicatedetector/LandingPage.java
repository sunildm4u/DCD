package com.makelifesimple.duplicatedetector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.simplelife.beans.CustomOnSelectListener;

import java.util.ArrayList;
import java.util.List;

//import com.google.analytics.tracking.android.EasyTracker;

public class LandingPage extends BaseActivity {
	
	
	List<String> isdCodes = new ArrayList<String>();
	Spinner spinner ;
	final private static int DIALOG_LOGIN = 1;
	SharedPreferences settings ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_page);
		
		
		
		isdCodes.add("Select your Country with Code");
		isdCodes.add("+91:India");
		isdCodes.add("+1:United States");
		isdCodes.add("+27:South Africa");
		isdCodes.add("+32:Belgium");
		isdCodes.add("+33:France");
		isdCodes.add("+34:Spain");
		isdCodes.add("+359:Bulgaria");
		isdCodes.add("+380:Ukraine");
		isdCodes.add("+39:Italy");
		isdCodes.add("+40:Romania");
		isdCodes.add("+420:Czech Republic");
		isdCodes.add("+44:United Kingdom");
		isdCodes.add("+46:Sweden");
		isdCodes.add("+47:Norway");
		isdCodes.add("+48:Poland");
		isdCodes.add("+49:Germany");
		isdCodes.add("+51:Peru");
		isdCodes.add("+54:Argentina");
		isdCodes.add("+55:Brazil");
		isdCodes.add("+56:Chile");
		isdCodes.add("+598:Uruguay");
		isdCodes.add("+64:New Zealand");
		isdCodes.add("+672:Australia");
		isdCodes.add("+7:Russia");
		isdCodes.add("+81:Japan");
		isdCodes.add("+82:South Korea");
		isdCodes.add("+86:China");
		isdCodes.add("Others");
		
		spinner = (Spinner) findViewById(R.id.spinnerId);
		spinner.setOnItemSelectedListener(new CustomOnSelectListener(){
			
			@SuppressWarnings("deprecation")
			public void onItemSelected(AdapterView<?> parent, View arg1, int pos,long id) {
				
				if(parent.getItemAtPosition(pos).toString()=="Others")
				{
					showDialog(DIALOG_LOGIN);
				}
				
				
			}
		});
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, isdCodes); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	
	protected Dialog onCreateDialog(int id) {
		 
		  AlertDialog dialogDetails = null;
		 
		  switch (id) {
		  case DIALOG_LOGIN:
		   LayoutInflater inflater = LayoutInflater.from(this);
		   View dialogview = inflater.inflate(R.layout.activity_isddialodue, null);
		 
		   AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		   dialogbuilder.setTitle("Enter County Code ..");
		   dialogbuilder.setView(dialogview);
		   dialogDetails = dialogbuilder.create();
		 
		   break;
		  }
		 
		  return dialogDetails;
		 }
		 
		 @Override
		 protected void onPrepareDialog(int id, Dialog dialog) {
		 
		  switch (id) {
		  case DIALOG_LOGIN:
		   final AlertDialog alertDialog = (AlertDialog) dialog;
		   Button loginbutton = (Button) alertDialog.findViewById(R.id.dialogbutton1);
		   final EditText userName = (EditText) alertDialog.findViewById(R.id.dialogtext1);
		  
		 
		   loginbutton.setOnClickListener(new View.OnClickListener() {
		 
		    @Override
		    public void onClick(View v) {
		    String otherCode = userName.getText().toString();
		    	otherCode = otherCode+":Others";
		    	isdCodes.add(otherCode);
		    	spinner.setSelection(isdCodes.indexOf(otherCode));
		    	 alertDialog.dismiss();
		    	
		    }
		   });
	
		   break;
		  }
		 }

			public void nextScreen(View view) {
				if(spinner.getSelectedItem().equals("Select your Country with Code"))
				{
					Toast msg = Toast.makeText(this, "Please select Your country Code", Toast.LENGTH_LONG);
					msg.show();
					return;
				}else{
				saveisdcode(spinner.getSelectedItem().toString());
				Intent intoBJ = new Intent(this,MainActivity2.class);
				String tmpobj = spinner.getSelectedItem().toString();
				if(tmpobj.contains(":")){
				intoBJ.putExtra("contryCode", tmpobj.substring(0, tmpobj.indexOf(":")));
				}else{
					intoBJ.putExtra("contryCode", tmpobj);	
				}
				startActivity(intoBJ);

				}
			}

			
			
			  public String isdcodesavedinapp()
			    {
			    	try {
			    	    return settings.getString("isdcodesavedfordetect", null);
			    	} catch (Exception e) {
						e.printStackTrace();
					}
			    	return null;
			    } 
			  
			  public void saveisdcode(String isdcode) 
			    {
			      	try {
			      		
			            SharedPreferences.Editor editor = settings.edit();
			            editor.putString("isdcodesavedfordetect", isdcode);
			    	    editor.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
			 	}
			  
			  @Override
				public void onStart() {
				  settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				  //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
				  
				  if(isdcodesavedinapp()!=null)
					{
					  Intent intoBJ = new Intent(this,MainActivity2.class);
					  String tmpobj = isdcodesavedinapp();
					  if(tmpobj.contains(":")){
				      intoBJ.putExtra("contryCode", tmpobj.substring(0, tmpobj.indexOf(":")));
					  }else{
						  intoBJ.putExtra("contryCode", tmpobj);
					  }
					  startActivity(intoBJ);
					
					}
				  super.onStart();
				}

				@Override
				public void onStop() {
				  super.onStop();
				  //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
				}
			  
	 public void onBackPressed() {
		 Intent intent = new Intent(Intent.ACTION_MAIN);
		 intent.addCategory(Intent.CATEGORY_HOME);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 startActivity(intent);
  	  
  	}
}
