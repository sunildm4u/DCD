 package com.makelifesimple.duplicatedetector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplelife.beans.Contactbean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

//import com.google.analytics.tracking.android.EasyTracker;

public class Remover1 extends BaseActivity {


	ArrayList<Contactbean> contactsList = new ArrayList<Contactbean>(); 
	ArrayList<String> tempcontactsList = new ArrayList<String>(); 
	ArrayList<Contactbean> vcardExportList = new ArrayList<Contactbean>(); 
	ArrayList<Contactbean> dupList = new ArrayList<Contactbean>(); 
	ArrayList<String> noNumberIds = new ArrayList<String>(); 
	ProgressDialog progressBar;
	int contactcount;
	Button removeduplicatesButton;
	//Button exportContactsButton ;
	String countrycode;
	ListView listView = null;
	TextView t1 = null;
	
	  boolean mIsPremium = false;
	
	  static final String TAG = "remover";
	  
	    static final int RC_REQUEST = 10001;

	    
	    // Does the user have an active subscription to the infinite gas plan?
	    static boolean mSubscribedToInfiniteGas = false;

	    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
	    static final String SKU_PREMIUM = "premium";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remover1);
		listView  = (ListView) findViewById(R.id.listview);
		try {
		
			Bundle extras = getIntent().getExtras();
			countrycode = extras.getString("contryCode");
			removeduplicatesButton = (Button)findViewById(R.id.removeduplicates);
			t1 = (TextView) findViewById(R.id.delconfirmID);
			t1.setVisibility(View.INVISIBLE);
			removeduplicatesButton.setEnabled(false);
			 readContacts();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
    // Listener that's called when we finish querying the items and subscriptions we own
    
	

	public void deleteContacts(View view) {

		new DeleteContacts().execute();
	}
	
	
	public void viewDpuplicates(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Duplicate Contacts");
		ListView modeList = new ListView(this);
		modeList.setBackgroundColor(0xFFFFFF);
		modeList.setCacheColorHint(00000000);
		modeList.setScrollingCacheEnabled(false);
		CustomListAdapter modeAdapter = new CustomListAdapter(this,R.layout.activity_cutom_list_objectvew, dupList);
		modeList.setAdapter(modeAdapter);
		
		builder.setView(modeList);
		modeList.invalidate();
		final Dialog dialog = builder.create();
		
		dialog.show();
	}
	
	public void readContacts(View view){
		readContacts();	
	}

	public void readContacts(){
			
			//findduplicatesButton.setEnabled(false);
			contactsList.clear();
			noNumberIds.clear();
			tempcontactsList.clear();
			dupList.clear();
			
			progressBar = new ProgressDialog(this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Verifying Contacts ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
			new BackgroundAsyncTask().execute();
	}


	
	
public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void> {
		
		int myProgress;

		@Override
		protected void onPostExecute(Void result) {
			 try {
				 progressBar.dismiss();
				 progressBar = null;
			    } catch (Exception e) {
			        // nothing
			    }
			if(dupList.size()>0)
			{
				removeduplicatesButton.setEnabled(true);removeduplicatesButton.setVisibility(View.VISIBLE);

				//ListView modeList = new ListView(this);
				listView.setBackgroundColor(0xFFFFFF);
				listView.setCacheColorHint(00000000);
				listView.setScrollingCacheEnabled(false);
				CustomListAdapter modeAdapter = new CustomListAdapter(Remover1.this,R.layout.activity_cutom_list_objectvew, dupList);
				listView.setAdapter(modeAdapter);


				//viewduplicatesButton.setEnabled(true);viewduplicatesButton.setVisibility(View.VISIBLE);
				//exportContactsButton.setEnabled(true);
			}else{
				removeduplicatesButton.setEnabled(false);removeduplicatesButton.setVisibility(View.INVISIBLE);
				listView.setVisibility(View.INVISIBLE);
				t1.setVisibility(View.VISIBLE);
				//viewduplicatesButton.setEnabled(false);viewduplicatesButton.setVisibility(View.INVISIBLE);
				//exportContactsButton.setEnabled(false);
				//findduplicatesButton.setEnabled(true);
			}
			

			if(noNumberIds.size()>0)
			{
				//cbbox.setEnabled(true);
			}

			
		}

		@Override
		protected void onPreExecute() {
			myProgress = 0;
		}
		
		
	


		@Override
		protected Void doInBackground(Void... params) {
	
			ContentResolver cr = getContentResolver();
	    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
            progressBar.setMax(cur.getCount());
	        contactcount = cur.getCount();
	        	if(cur!=null && cur.getCount()>0){
	             while (cur.moveToNext()) {
	            	 myProgress++;
	            	 Contactbean cb = new Contactbean();
	          	     String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	                 if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
	                 {
	             	   cb.setContactid(Integer.valueOf(id));
			             	    Cursor pCur = cr.query(Phone.CONTENT_URI, null,Phone.CONTACT_ID + " = " + id, null, null);
	                   StringBuffer sbr = new StringBuffer();
	                   if(pCur!=null && pCur.getCount()>0){
			 	 	            while (pCur.moveToNext()) {
			 	                 String contactNumber = pCur.getString(pCur.getColumnIndex(Phone.NUMBER));
			 		             cb.setLookupkey( pCur.getString(pCur.getColumnIndex(Phone.LOOKUP_KEY)));
	              	          
			 		            if(contactNumber!=null)
		              	         {
		              	        	 
		              	        	 contactNumber = contactNumber.replaceAll("[^0-9+]+","");
		              	        	 
		              	        	 if(contactNumber.startsWith(countrycode))
		              	        	 {
		              	        		 contactNumber = contactNumber.substring(countrycode.length(), contactNumber.length());
		              	        	 }
		              	        	 
		              	        	 
		              	        	 if(contactNumber.length()>10)
		              	        	 {
		              	        		contactNumber =   contactNumber.substring((contactNumber.length()-10), contactNumber.length());
		              	        	 }
		              	         }
	              	           sbr.append(contactNumber+":");
			              	          
			                        
			                           
			                           String contactname = pCur.getString(pCur.getColumnIndex(Phone.DISPLAY_NAME));
			                           System.out.println(contactname +" : " + contactNumber);
	                           if(contactname==null || contactname.equalsIgnoreCase("null"))
	                           {
	                        	   contactname = "Empty Contact";
	                           }
	                           cb.setContactName(contactname.toLowerCase());
	                   }
	                   }
	              	   if(pCur!=null)pCur.close();
	                   cb.setContactNumber(sbr.toString());
	                   
	                   String email = "";
	                   Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id}, null);
	                  if(emailCur!=null && emailCur.getCount()>0){
	                   while (emailCur.moveToNext()) {
	                  	
	                	   email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	                         if(email==null||email.equalsIgnoreCase("null"))
	                      	   email="";
	                   			}
	                  }
	                     if(emailCur!=null)emailCur.close();
	                     cb.setEmail(email);
	               contactsList.add(cb);
	               progressBar.setProgress(myProgress);
	            }else{
	            	noNumberIds.add(id);
	            }
	          }
	        	}     
	          
	          if(cur!=null)cur.close();
	          
	          Collections.sort(contactsList, new Comparator<Contactbean>(){
	  			  public int compare(Contactbean c1, Contactbean c2) {
	  				  return  c2.getEmail().compareToIgnoreCase(c1.getEmail());
	  			     }
	  			});
	  		
	  		for (Iterator<Contactbean> iterator = contactsList.iterator(); iterator.hasNext();) 
	  		{
	  			Contactbean objBean =  iterator.next();
	  			System.out.println(objBean.getContactName()+":"+objBean.getContactNumber());
	  			if(!tempcontactsList.contains(objBean.getContactName()+":"+objBean.getContactNumber()))
	  			{
	  				vcardExportList.add(objBean);
	  				tempcontactsList.add(objBean.getContactName()+":"+objBean.getContactNumber());
	  			}else{
	  				if(objBean!=null && objBean.getContactName()!=null && !objBean.getContactName().equals("Empty Contact"))
	  				dupList.add(objBean);
	  			}
	  			
	  		}
	  		return null;
		}

		
	}
	

public static void appendLog(String text)
{       
   File logFile = new File(Environment.getExternalStorageDirectory()+"/duplicateremovererror.log");
   if (!logFile.exists())
   {
      try
      {
         logFile.createNewFile();
      } 
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   try
   {
      //BufferedWriter for performance, true to set append to file flag
      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
      buf.append(text);
      buf.newLine();
      buf.close();
   }
   catch (IOException e)
   {
      // TODO Auto-generated catch block
      e.printStackTrace();
   }
}
	
public class DeleteContacts extends AsyncTask<Void, Integer, Void> {
	
	int deletecontactNumbers ;
	String exception="";

	@Override
	protected void onPostExecute(Void result) {
		dupList.clear(); 
		listView.setVisibility(View.INVISIBLE);
		t1.setVisibility(View.VISIBLE);
		try {
			 progressBar.dismiss();
			 progressBar = null;
		    } catch (Exception e) {
		        // nothing
		    }
		 
		 
		 
		 if(exception.length()>1)
		 {
			 Toast.makeText(Remover1.this, exception , Toast.LENGTH_LONG).show();
		 }
		 
		 
		 
		removeduplicatesButton.setEnabled(false);removeduplicatesButton.setVisibility(View.INVISIBLE);
		contactcount = contactcount - deletecontactNumbers;


	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(Remover1.this);
		builder.setTitle("Bingoo!! Contacts Cleaned..");
		builder.setIcon(R.drawable.rate2);
		builder.setMessage("Duplicate Contacts : 0 \n Remeber to use our other feature : \n 1. Remove Duplicates by Name \n 2. Remove Duplicates by Number . \n\n Would you like to rate us, pls ?");
		builder.setCancelable(false);
		builder.setPositiveButton("Rate App",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Uri uri = Uri.parse("market://details?id=" + "com.makelifesimple.duplicatedetector");
						Intent intent = new Intent(Intent.ACTION_VIEW,uri);
						//intent.setData(Uri.parse("market://details?id= com.simplelife.duplicatecontactchecker"));
						startActivity(intent);
					}
				});
		builder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	protected void onPreExecute() {
        
		progressBar = new ProgressDialog(Remover1.this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Removing Duplicate Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
		deletecontactNumbers = 0;
	}
	
	@Override
	protected Void doInBackground(Void... param) {
		
		
		ContentResolver cr = getContentResolver();
		progressBar.setMax(dupList.size());
		ArrayList<ContentProviderOperation> ops =  new ArrayList<ContentProviderOperation>();
		for (Iterator<Contactbean> iterator = dupList.iterator(); iterator.hasNext();) 
		{
		
			
			deletecontactNumbers++;
			progressBar.setProgress(deletecontactNumbers);
			Contactbean object =  iterator.next();
		   	String where = ContactsContract.Data.CONTACT_ID + " = ? ";
	    	String[] params = new String[] {String.valueOf(object.getContactid())};
	       	ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(where, params).build());
			if(ops.size()>=400)
			{
					
		   		    try {
						appendLog("Giong to delete " + ops.size() +" Contacts");
						getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
						appendLog("Deleted" + ops.size() +" Contacts");
					} catch (RemoteException e) {
						exception = e.getMessage()+"\n"+e.getLocalizedMessage()+"\n"+Arrays.toString(e.getStackTrace()) + " : Pls mail this error to Makinglifeeasy4u@gmail.com";
						// TODO Auto-generated catch block
						appendLog("Exception 1" + e.getMessage()+"\n"+e.getLocalizedMessage()+"\n"+Arrays.toString(e.getStackTrace()));
						Log.e(TAG, "Exception Occured :" + e.getMessage()+"\n"+e.getLocalizedMessage()+"\n"+Arrays.toString(e.getStackTrace()) + " : Pls mail this error to Makinglifeeasy4u@gmail.com");
						e.printStackTrace();
					} catch (OperationApplicationException e) {
						appendLog("Exception 2" + e.getMessage()+"\n"+e.getLocalizedMessage()+"\n"+Arrays.toString(e.getStackTrace()));
						exception = e.getMessage()+"\n"+e.getLocalizedMessage()+"\n"+Arrays.toString(e.getStackTrace()) + " : Pls mail this error to Makinglifeeasy4u@gmail.com";
						// TODO Auto-generated catch block
						Log.e(TAG, "Exception Occured :" + e.getMessage()+"\n"+e.getLocalizedMessage()+"\n"+Arrays.toString(e.getStackTrace()) + " : Pls mail this error to Makinglifeeasy4u@gmail.com");
						e.printStackTrace();
					}
		            // ops.clear();
		   		   ops = new ArrayList<ContentProviderOperation>(400);
				
			}
		}
       if(ops.size()>0)
        {
		try {
        		cr.applyBatch(ContactsContract.AUTHORITY, ops);
	    		    progressBar.setProgress(dupList.size());
                ops.clear();
                
          } catch (Exception e) {
			e.printStackTrace();
          }
        	
        }

		return null;
	}
	
}


public class ExportContacts extends AsyncTask<Void, Integer, Void> {
	
	int exportcontactnumbers ; 

	@Override
	protected void onPostExecute(Void result) {
		 try {
			 progressBar.dismiss();
			 progressBar = null;
		    } catch (Exception e) {
		        // nothing
		    }
		Toast msg = Toast.makeText(Remover1.this, "Back Up is Made in you sdcard @ /ContactsBackup/Contacts.vcf ", Toast.LENGTH_LONG);
		msg.show();
	}

	@Override
	protected void onPreExecute() {
		exportcontactnumbers = 0;
	}
	
	@Override
	protected Void doInBackground(Void... param) {
		String filepath   = Environment.getExternalStorageDirectory() + "/ContactsBackup/Contacts.vcf";
		
		
		if (!new File(Environment.getExternalStorageDirectory() + "/ContactsBackup").exists()) {
			new File(Environment.getExternalStorageDirectory() + "/ContactsBackup").mkdir();
		}
		progressBar.setMax(vcardExportList.size());
		 Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
		for (Iterator<Contactbean> iterator = vcardExportList.iterator(); iterator.hasNext();) {
			Contactbean cbObj =  iterator.next();
			Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI,cbObj.getLookupkey());
            AssetFileDescriptor fd;
            try {
            	exportcontactnumbers ++;
                    fd = getContentResolver().openAssetFileDescriptor(uri, "r");
                    FileInputStream fis = fd.createInputStream();
                    byte[] buf = new byte[(int) fd.getDeclaredLength()];
                    fis.read(buf);
                    String VCard = new String(buf);
                    String path = filepath;
                    FileOutputStream mFileOutputStream = new FileOutputStream(path,true);
                    mFileOutputStream.write(VCard.toString().getBytes());
                    phones.moveToNext();
                    Log.d("Vcard", VCard);
            } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
            }
            progressBar.setProgress(exportcontactnumbers);
		}
		return null;
	}
	
}

public class DeleteEmailContacts extends AsyncTask<Void, Integer, Void> {
	
	int deleteconatactemailidss ;

	@Override
	protected void onPostExecute(Void result) {

		listView.clearChoices();
		listView.setVisibility(View.INVISIBLE);
		removeduplicatesButton.setVisibility(View.INVISIBLE);

		 try {
			 progressBar.dismiss();
			 progressBar = null;
		    } catch (Exception e) {
		        // nothing
		    }
		//cbbox.setChecked(false);
    	//cbbox.setEnabled(false);
	}

	@Override
	protected void onPreExecute() {
		deleteconatactemailidss = 0;
		
	}
	
	@Override
	protected Void doInBackground(Void... param) {
		ContentResolver cr = getContentResolver();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		progressBar.setMax(noNumberIds.size());
		int c = noNumberIds.size();
		int tmpnumber = 0 ;
		for (Iterator iterator = noNumberIds.iterator(); iterator.hasNext();) {
			deleteconatactemailidss++;
			tmpnumber++;
			String  object = (String) iterator.next();
		   	String where = ContactsContract.Data.CONTACT_ID + " = ? ";
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
    	   Intent i = new Intent(Remover1.this,MainActivity2.class);
    	   i.putExtra("contryCode", countrycode);
    	   startActivity(i);
    	}
}
