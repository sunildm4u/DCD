package com.makelifesimple.duplicatedetector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import com.google.analytics.tracking.android.EasyTracker;

public class InvisibleContacts extends BaseActivity {
	
	List<String > dupNamesList = new ArrayList<String>();
	List<String > Idslist = new ArrayList<String>();
	ProgressDialog progressBar;
	TextView tv;
	Button b1;
	Button b2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invisible_contacts);
		tv = (TextView) findViewById(R.id.intdupcount);
		b1 = (Button) findViewById(R.id.button2);
		b2 = (Button) findViewById(R.id.button1);
		new BackgroundAsyncTask().execute();
	}
	
	public void viewduplicates(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(InvisibleContacts.this);
		builder.setTitle("Invisible Contacts");
		ListView modeList = new ListView(InvisibleContacts.this);
		modeList.setBackgroundColor(0xFFFFFF);
		modeList.setCacheColorHint(00000000);
		modeList.setScrollingCacheEnabled(false);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(InvisibleContacts.this,android.R.layout.simple_list_item_1, dupNamesList);
		modeList.setAdapter(modeAdapter);
		
		builder.setView(modeList);
		modeList.invalidate();
		final Dialog dialog = builder.create();
		
		dialog.show();
		
		
	}
	
	public void removeduplicates(View view)
	{
		new DeleteContacts().execute();
		
		
	}

public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void> {
		
		int myProgress;

		@Override
		protected void onPostExecute(Void result) {
			
			progressBar.dismiss();
			
		tv.setText(String.valueOf(dupNamesList.size()));
		
		if(dupNamesList.size()==0)
		{
			b1.setEnabled(false);b1.setVisibility(View.INVISIBLE);
			b2.setEnabled(false);b2.setVisibility(View.INVISIBLE);
		}
			
			
		}

		@Override
		protected void onPreExecute() {
			myProgress = 0;
			progressBar = new ProgressDialog(InvisibleContacts.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Finding Invisible Contacts ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
		}
		
		
	


		@Override
		protected Void doInBackground(Void... params) {
	
			//ContentResolver cr = getContentResolver();
			
			
			    String[] projection = new String[] {
		                ContactsContract.Contacts._ID,
		                ContactsContract.Contacts.DISPLAY_NAME
		        };
		        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 0";
		        String[] selectionArgs = null;
		        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

			
		        ContentResolver cr = getContentResolver();
		        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,projection,selection, selectionArgs, sortOrder);
	    	
		        progressBar.setMax(cur.getCount());
		       if(cur!=null && cur.getCount()>0){
		        while (cur.moveToNext()) {
            	
	            	 myProgress++;
	            	 progressBar.setProgress(myProgress);
	            	 String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	            	 String displayname = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	            	 Idslist.add(id);
					 if(displayname!=null)
	            	 dupNamesList.add(displayname);
	            }
		       }
	          
	        if(cur!=null) cur.close();
	         myProgress = 0;
	         
	         String[] projection2 = new String[] {
		                ContactsContract.Contacts._ID,
		                ContactsContract.Contacts.DISPLAY_NAME
		        };
		        String selection2 = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 AND "+ContactsContract.Contacts.HAS_PHONE_NUMBER +" = 0";
		        String[] selectionArgs2 = null;
		        String sortOrder2 = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

			
		        ContentResolver cr2 = getContentResolver();
		        Cursor cur2 = cr2.query(ContactsContract.Contacts.CONTENT_URI,projection2,selection2, selectionArgs2, sortOrder2);
	    	
		        progressBar.setMax(cur2.getCount());
		        if(cur2!=null && cur2.getCount()>0){
		        while (cur2.moveToNext()) {
         	
	            	 myProgress++;
	            	 progressBar.setProgress(myProgress);
	            	 String id = cur2.getString(cur2.getColumnIndex(ContactsContract.Contacts._ID));
	            	 String displayname = cur2.getString(cur2.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	            	 System.out.println("No Number :" + displayname);
	            	 
	            	 if(!haseMailId(id) && !hasNote(id) && !hasaddress(id))
	            	 {
						 if(displayname!=null) {
							 Idslist.add(id);
							 dupNamesList.add("(Empty) " + displayname);
						 }
	            	 }
	            	 
	            	 
	            }
		        }
	          
	          if(cur2!=null)cur2.close();
	        
	  		return null;
		}
		

		public boolean hasNote(String id) {
			
			try {
				
				  String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
	              String[] noteWhereParams = new String[]{id,ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
	               Cursor noteCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
	               if(noteCur!=null && noteCur.getCount()!=0){
	            		 return true;
	            	 }
	               noteCur.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return false;
		}

		private boolean hasaddress(String id) {
			try {
				
					String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
	               String[] addrWhereParams = new String[]{id,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
	               Cursor addrCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI,null, addrWhere, addrWhereParams, null);
	               if(addrCur!=null && addrCur.getCount()!=0){
	            		 return true;
	            	 }
	               addrCur.close();
				
			} catch (Exception e) {
				e.printStackTrace();// TODO: handle exception
			}
			return false;
		}

		private boolean haseMailId(String id) {
			
			try {
			 Cursor emailCur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id}, null);
           	 if(emailCur!=null && emailCur.getCount()!=0){
            		 return true;
            	 }
				emailCur.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return false;
		}

		
	}

public class DeleteContacts extends AsyncTask<Void, Integer, Void> {
	
	int myProgress;

	@Override
	protected void onPostExecute(Void result) {
		progressBar.dismiss();
		b1.setEnabled(false);b1.setVisibility(View.INVISIBLE);
		b2.setEnabled(false);b2.setVisibility(View.INVISIBLE);
		tv.setText("0");
		//System.out.println(idsList +":"+insertionList);
		//new DeleteContactsSelected().execute();
		
	}

	@Override
	protected void onPreExecute() {
		myProgress = 0;
		progressBar = new ProgressDialog(InvisibleContacts.this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Deleting Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
		
	}
	
	



	@Override
	protected Void doInBackground(Void... param) {
		
		try {
			ContentResolver cr = getContentResolver();
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			//removefromCache(temIdsList);
			progressBar.setMax(Idslist.size());
			int c = Idslist.size();
			int tmpnumber = 0 ;
			for (Iterator iterator = Idslist.iterator(); iterator.hasNext();) {
				myProgress++;
				progressBar.setProgress(myProgress);
				tmpnumber++;
				String  object = (String) iterator.next();
			   	String where =ContactsContract.Data.CONTACT_ID + " = ? ";
		    	String[] params = new String[] {String.valueOf(object)};
		       	ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(where, params).build());
		    	
		    	if(tmpnumber==100)
		    	{
		    	 try {
		    		    //progressBar.setProgress(deleteconatactemailidss);
		    		    cr.applyBatch(ContactsContract.AUTHORITY, ops);
		                ops.clear();
		                tmpnumber = 0;
		                ops = ops = new ArrayList<ContentProviderOperation>(100);
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
	    		    progressBar.setProgress(c);
	                 ops.clear();
	                
	          } catch (Exception e) {
	        	  e.printStackTrace();
	          }
	        	
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
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
  //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
}


}
