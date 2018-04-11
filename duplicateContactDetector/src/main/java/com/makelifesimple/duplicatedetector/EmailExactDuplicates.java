package com.makelifesimple.duplicatedetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EmailExactDuplicates extends Activity {

	String countrycode;
	List<String> correctlist = new ArrayList<String>();
	//ArrayList<Contactbean> duplist = new ArrayList<Contactbean>();
	Map<String,String> aMap = new HashMap<String, String>();
	ArrayList<String> dupStringlist = new ArrayList<String>();
	
	TextView totalcount;
	TextView dupcount;
    Button dupViewButton;
    Button removeButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_exact_duplicates);
		
		Bundle extras = getIntent().getExtras();
		countrycode = extras.getString("contryCode");
		
		
		totalcount = (TextView) findViewById(R.id.totalcontactcount);
		dupcount = (TextView) findViewById(R.id.duplicatecount);
		dupViewButton= (Button) findViewById(R.id.viewduplicates);
		removeButton= (Button) findViewById(R.id.removeduplicates);
		
		correctlist.clear();
		aMap.clear();
		dupStringlist.clear();
		
		readContacts();
		
	}
	public void readContacts(){
		
		
		
		new BackgroundAsyncTask().execute();
}

	
	
	public void deleteContacts(View view)
	{
		new DeleteEmailContacts().execute();	
		
	}
	
	public void viewDpuplicates(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Duplicate e-Mail Contacts");
		ListView modeList = new ListView(this);
		modeList.setBackgroundColor(0xFFFFFF);
		modeList.setCacheColorHint(00000000);
		modeList.setScrollingCacheEnabled(false);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dupStringlist);
		//ArrayAdapter modeAdapter = new ArrayAdapter(this,R.layout.activity_cutom_list_objectvew, dupStringlist);
		modeList.setAdapter(modeAdapter);
		
		builder.setView(modeList);
		modeList.invalidate();
		final Dialog dialog = builder.create();
		
		dialog.show();
	}
	
public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void> {
		
		int myProgress;
		int tmpcount = 0;
		ProgressDialog progressBar;

		@Override
		protected void onPostExecute(Void result) {
			 try {
				 progressBar.dismiss();
				 progressBar = null;
			    } catch (Exception e) {
			        // nothing
			    }
			
			 totalcount.setText(String.valueOf(tmpcount));
			 
			if(dupStringlist.size()>0)
			{
				
				dupcount.setText(String.valueOf(dupStringlist.size()));
				dupViewButton.setVisibility(Button.VISIBLE);
				removeButton.setVisibility(Button.VISIBLE);
			}else{
				dupViewButton.setVisibility(Button.INVISIBLE);
				removeButton.setVisibility(Button.INVISIBLE);	
			}
		}

		@Override
		protected void onPreExecute() {
			progressBar = new ProgressDialog(EmailExactDuplicates.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Verifying Contacts ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
			myProgress = 0;
		}
		
		
	


		@Override
		protected Void doInBackground(Void... params) {
	
			        
			ContentResolver cr = getContentResolver();
			 Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,new String[]{ContactsContract.Contacts._ID,ContactsContract.Contacts.HAS_PHONE_NUMBER}, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 ", null, null);
			   progressBar.setMax(cur.getCount());
			   if(cur!=null && cur.getCount()>0){
			    while (cur.moveToNext())
		        {
			    	String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
			    	 if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
	                 {
			    		 
	                 }else{
	                	 String email = "";
	                	 Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id}, null);
	                    
	                     if (emailCur != null && emailCur.getCount()>0 ) {
	 	       	            if  (emailCur.moveToFirst()) {
	 	       	                do {
	 	       	                 tmpcount++;
	 	       	                 progressBar.setProgress(tmpcount);
	 	       	                 email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	 	                         if(email==null||email.equalsIgnoreCase("null")){
	 	                      	   email="";
	 	                         }
	 	                         if(correctlist.contains(email))
	 	                         {
	 	                        	aMap.put(id, email);
	 	                        	 dupStringlist.add(email);
	 	                         }else{
	 	                        	 correctlist.add(email);
	 	                         }
	 	       	                	
	 	       	             }while (emailCur.moveToNext());
	 	       	 	    }
	 	       	 	}
	                     if(emailCur!=null)emailCur.close();
	                      
	                 }
			
		        }
			    }
			    if(cur!=null)cur.close();
	                  
	  		return null;
		}

		
	}

public class DeleteEmailContacts extends AsyncTask<Void, Integer, Void> {
	
	int deleteconatactemailidss ;
	int progress;
	ProgressDialog progressBar;

	@Override
	protected void onPostExecute(Void result) {
		 try {
			 progressBar.dismiss();
			 progressBar = null;
		    } catch (Exception e) {
		        // nothing
		    }
		dupcount.setText(String.valueOf(0));
		
		
		Toast.makeText(EmailExactDuplicates.this,deleteconatactemailidss+ " e-mail contacts Deleted..", Toast.LENGTH_LONG).show();
		
	}

	@Override
	protected void onPreExecute() {
		deleteconatactemailidss = 0;
		progress = 0;
    	progressBar = new ProgressDialog(EmailExactDuplicates.this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Deleting Email Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
	}
	
	@Override
	protected Void doInBackground(Void... param) {
		ContentResolver cr = getContentResolver();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		progressBar.setMax(aMap.size());
		int tmpnumber = 0 ;
		for (Iterator<String> iterator = aMap.keySet().iterator(); iterator.hasNext();) {
			deleteconatactemailidss++;
			tmpnumber++;
			String  object = (String) iterator.next();
		   	String where =ContactsContract.Data.CONTACT_ID + " = ? ";
	    	String[] params = new String[] {String.valueOf(object)};
	       	ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(where, params).build());
	    	
	    	if(tmpnumber==100)
	    	{
	    	 try {
	    		    progressBar.setProgress(deleteconatactemailidss);
	    		    cr.applyBatch(ContactsContract.AUTHORITY, ops);
	                ops.clear();
	                tmpnumber = 0;
	          } catch (Exception e) {
	        	  e.printStackTrace();
	          }
	    	}
	    	
	        iterator.remove();
	       
	     }
		
        if(ops.size()>0)
        {
        	try {
        		 cr.applyBatch(ContactsContract.AUTHORITY, ops);
    		    progressBar.setProgress(aMap.size());
                 ops.clear();
                
          } catch (Exception e) {
        	  e.printStackTrace();
          }
        	
        }
        
		return null;
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
 // EasyTracker.getInstance(this).activityStop(this);  // Add this method.
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.email_exact_duplicates, menu);
		return true;
	}

}
