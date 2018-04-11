package com.makelifesimple.duplicatedetector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EmailRemover extends BaseActivity {

	ProgressDialog progressBar;
	int contactcount;
	//List<String> emailList = new ArrayList<String>();
	List<String> temIdsList = new ArrayList<String>();
	Map<String,String> aMap = new HashMap<String, String>();
	String countrycode;

	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remover2);
		Bundle extras = getIntent().getExtras();
		countrycode = extras.getString("contryCode");
		populateEmailData();
	
	}
	
	
	public void removedupicates(View view)
	{
		new DeleteEmailContactsAll().execute();
	}
	
	
	public void removedupicatesselected(View view)
	{
		temIdsList.clear();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select e-Mail Contacts to Delete");
		builder.setMultiChoiceItems(aMap.values().toArray(new String[aMap.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int i, boolean isChecked) {
	            if (isChecked) {
	            	
	            		  System.out.println(aMap.keySet().toArray(new String[aMap.size()])[i]);
	           			temIdsList.add(aMap.keySet().toArray(new String[aMap.size()])[i]);
	                
	            }else{
                  	temIdsList.remove(aMap.keySet().toArray(new String[aMap.size()])[i]);
                  } 
	        }
	    })
	   .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int whichButton) {
                                	new DeleteEmailContacts().execute();
                                }

                            })
                            
                             .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int whichButton) {
                                	dialog.cancel();
                                }

                            })
                            ;
	    
		
		final Dialog dialog = builder.create();
		dialog.show();
		
		
	}
	
	
	public void populateEmailData(){
		progressBar = new ProgressDialog(this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Verifying Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
		new GetData().execute();
}



public class GetData extends AsyncTask<Void, Integer, Void> {
	
	int myProgress;

	@Override
	protected void onPostExecute(Void result) {
		 try {
			 progressBar.dismiss();
			 progressBar = null;
		    } catch (Exception e) {
		        // nothing
		    }
		
		tv = (TextView)findViewById(R.id.emailview);
		
		tv.setText(String.valueOf(aMap.size()));
		//if(temIdsList.size()>0)
		//readContacts();
	}

	@Override
	protected void onPreExecute() {
		myProgress = 0;
	}
	
	



	@Override
	protected Void doInBackground(Void... params) {
		
		
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 ", null, null);
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
                    
                	 
                	 if(emailCur!=null && emailCur.getCount()!=0){
                	 while (emailCur.moveToNext()) {
                    	progressBar.setProgress(myProgress++);
                  	   email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                           if(email==null||email.equalsIgnoreCase("null"))
                        	   email="Empty Contact";
                     			}
                	 }else{
                		 email = "Empty Contact";
                	 }
                       if(emailCur!=null)emailCur.close();
                       aMap.put(id, email);
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

	@Override
	protected void onPostExecute(Void result) {
		 try {
			 progressBar.dismiss();
			 progressBar = null;
		    } catch (Exception e) {
		        // nothing
		    }
		tv.setText(String.valueOf(aMap.size()));
		
		
		Toast.makeText(EmailRemover.this,deleteconatactemailidss+ " e-mail contacts Deleted..", Toast.LENGTH_LONG).show();
		
	}

	@Override
	protected void onPreExecute() {
		deleteconatactemailidss = 0;
		progress = 0;
    	progressBar = new ProgressDialog(EmailRemover.this);
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
		removefromCache(temIdsList);
		progressBar.setMax(temIdsList.size());
		int c = temIdsList.size();
		int tmpnumber = 0 ;
		for (Iterator iterator = temIdsList.iterator(); iterator.hasNext();) {
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
    		    progressBar.setProgress(c);
                 ops.clear();
                
          } catch (Exception e) {
        	  e.printStackTrace();
          }
        	
        }
        
		return null;
	}
	
}


public void viewEmails(View v) {
	temIdsList.clear();
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Showin e-Mail Contacts");
	ListView modeList = new ListView(this);
	ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, new ArrayList<String>(aMap.values()));
	modeList.setAdapter(modeAdapter);
	builder.setView(modeList);
	
	final Dialog dialog = builder.create();
	dialog.show();
}

public void removefromCache(List<String> temIdsList2) {
	try {
		
		
	    for(Iterator<Map.Entry<String, String>> it = aMap.entrySet().iterator(); it.hasNext(); ) {
	        Map.Entry<String, String> entry = it.next();
	        if(temIdsList2.contains(entry.getKey())) {
	          it.remove();
	        }
	      }
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}



public class DeleteEmailContactsAll extends AsyncTask<Void, Integer, Void> {
	
	int deleteconatactemailidss ;
	int progress;

	@Override
	protected void onPostExecute(Void result) {
		 try {
			 progressBar.dismiss();
			 progressBar = null;
		    } catch (Exception e) {
		        // nothing
		    }
		tv.setText(String.valueOf(0));
		Toast.makeText(EmailRemover.this,deleteconatactemailidss+ " e-mail contacts Deleted..", Toast.LENGTH_LONG).show();
		
	}

	@Override
	protected void onPreExecute() {
		deleteconatactemailidss = 0;
		progress = 0;
    	progressBar = new ProgressDialog(EmailRemover.this);
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
		progressBar.setMax(aMap.keySet().size());
		int c = aMap.keySet().size();
		int tmpnumber = 0 ;
		for (Iterator iterator = aMap.keySet().iterator(); iterator.hasNext();) {
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
    		    progressBar.setProgress(c);
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
  //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
}

	 public void onBackPressed() {
  	   Log.d("CDA", "onBackPressed Called");
  	   Intent i = new Intent(EmailRemover.this,MainActivity2.class);
  	 i.putExtra("contryCode", countrycode);
  	   startActivity(i);
  	}
}
