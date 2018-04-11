package com.makelifesimple.duplicatedetector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.simplelife.beans.Contactbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Detector extends BaseActivity {
	


	ArrayList<Contactbean> contactsList = new ArrayList<Contactbean>(); 
	ArrayList<String> tempcontactsList = new ArrayList<String>(); 
	static ArrayList<String> noNumberIds = new ArrayList<String>(); 
	
	ArrayList<Contactbean> dupList = new ArrayList<Contactbean>(); 
	TextView totalnumberofcontactsview;
	TextView numberOfDuplicateCounts;
	TextView contactswithnophonenumber;
	TextView contactswithphonenumber;
	ProgressDialog progressBar;
	//ProgressDialog pd;
	int contactcount;
	//Button findduplicatesButton ;
	Button viewduplicatesButton ;
	//Button exportContactsButton ;
	String countrycode;
	int totalnumberofcontacts;
	
	Map<String,List<Contactbean>> nameBasedMap = new HashMap<String,List<Contactbean>>();
	Map<String,List<Contactbean>> nameBasedTempMap = new HashMap<String,List<Contactbean>>();
	
	Map<String,List<Contactbean>> numberBasedMap = new HashMap<String,List<Contactbean>>();
	Map<String,List<Contactbean>> numberBasedTempMap = new HashMap<String,List<Contactbean>>();
	
	TextView numberbasedDuplicates;
	TextView nameBasedDuplicates;
	TextView invisibleContacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detector);
		try {
		
			Bundle extras = getIntent().getExtras();
			countrycode = extras.getString("contryCode");
			totalnumberofcontactsview = (TextView)findViewById(R.id.totalcontactcount);
			numberOfDuplicateCounts = (TextView)findViewById(R.id.duplicatecount);
			contactswithnophonenumber = (TextView)findViewById(R.id.nonidcount);
			contactswithphonenumber = (TextView)findViewById(R.id.phonenumbercount);
			viewduplicatesButton = (Button)findViewById(R.id.viewduplicates);
		    viewduplicatesButton.setEnabled(false);
			 
		    numberbasedDuplicates = (TextView)findViewById(R.id.duplicatecountonnumbers);
		    nameBasedDuplicates =  (TextView)findViewById(R.id.duplicatecountonnames);
		    invisibleContacts =  (TextView)findViewById(R.id.invcount);
		    
		   
		    
			 readContacts();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void taketomyapp(View view) {
        
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.simplelife.duplicatecontactremover"));
		startActivity(intent);
	}
	
	
	public void viewDpuplicates(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Showing Duplicate Contacts");
		ListView modeList = new ListView(this);
		CustomListAdapter modeAdapter = new CustomListAdapter(this,R.layout.activity_cutom_list_objectvew, dupList);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		dialog.show();
	}
	
	public void showhelp(View view) {
		
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.help);
		dialog.setTitle("Help Doc ...");
		
		
		dialog.show();
	}	

	public void readContacts(){
			
			contactsList.clear();
			tempcontactsList.clear();
			dupList.clear();
			noNumberIds.clear();
			
			new BackgroundAsyncTask().execute();
	}



	
//	private String getInvisibleContactCount() {
//		
//		if(progressBar!=null)
//		{
//			progressBar.dismiss();
//		}
//		
//		Cursor cur2 = null;
//		String countvalue = null;
//		try {
//			
//			ContentResolver cr = getContentResolver();
//	    	cur2 = cr.query(ContactsContract.Contacts.CONTENT_URI,null, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 0 ", null, null);
//	    	countvalue = String.valueOf(cur2.getCount() + getEmptyContactsCout());
//	    	pd.dismiss();
//	   } catch (Exception e) {
//		e.printStackTrace();
//		}
//		cur2.close();
//		return countvalue;
//	}
//	
//private int getEmptyContactsCout() {
//		try {
//			
//			String[] projection2 = new String[] {
//	                ContactsContract.Contacts._ID,
//	                ContactsContract.Contacts.DISPLAY_NAME
//	        };
//	        String selection2 = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 AND "+ContactsContract.Contacts.HAS_PHONE_NUMBER +" = 0";
//	        String[] selectionArgs2 = null;
//	        String sortOrder2 = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
//int tmpcountvalue = 0;
//		
//	        ContentResolver cr2 = getContentResolver();
//	        Cursor cur2 = cr2.query(ContactsContract.Contacts.CONTENT_URI,projection2,selection2, selectionArgs2, sortOrder2);
//    	
//	        //progressBar.setMax(cur2.getCount());
//	        while (cur2.moveToNext()) {
//     	
//            	// myProgress++;
//            	 //progressBar.setProgress(myProgress);
//            	 String id = cur2.getString(cur2.getColumnIndex(ContactsContract.Contacts._ID));
//            	 String displayname = cur2.getString(cur2.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//            	 System.out.println("No Number :" + displayname);
//            	 
//            	 if(!haseMailId(id) && !hasNote(id) && !hasaddress(id))
//            	 {
//            		 tmpcountvalue++; 
//            	 }
//            	 
//            	 
//            }
//          
//          cur2.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}

public boolean hasNote(String id) {
	Cursor noteCur = null;
	try {
		
		  String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
          String[] noteWhereParams = new String[]{id,ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
           noteCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
           if(noteCur!=null && noteCur.getCount()!=0){
        		 return true;
        	 }
           //noteCur.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		if(noteCur!=null)
		{
			noteCur.close();
		}
	}
	
	return false;
}

private boolean hasaddress(String id) {
	 Cursor addrCur = null;;
	try {
		
			String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
           String[] addrWhereParams = new String[]{id,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
            addrCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI,null, addrWhere, addrWhereParams, null);
           if(addrCur!=null && addrCur.getCount()!=0){
        		 return true;
        	 }
           //addrCur.close();
		
	} catch (Exception e) {
		e.printStackTrace();// TODO: handle exception
	}finally{
		if(addrCur!=null)
		{
			addrCur.close();
		}
	}
	return false;
}

private boolean haseMailId(String id) {
	 Cursor emailCur = null;
	try {
	 emailCur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id}, null);
   	 if(emailCur!=null && emailCur.getCount()!=0){
    		 return true;
    	 }
		//emailCur.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		if(emailCur!=null)
		{
			emailCur.close();
		}
	}
	
	return false;
}

public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void> {
		
		int myProgress;
		int invisblecout = 0;

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
				viewduplicatesButton.setEnabled(true);viewduplicatesButton.setVisibility(View.VISIBLE);
			}else{
			
				viewduplicatesButton.setEnabled(false);viewduplicatesButton.setVisibility(View.INVISIBLE);
			}
			
			
			totalnumberofcontactsview.setText(String.valueOf(contactcount));			
			contactswithphonenumber.setText(String.valueOf(contactsList.size()));
			contactswithnophonenumber.setText(String.valueOf(noNumberIds.size()));
			numberOfDuplicateCounts.setText(String.valueOf(dupList.size()));
			
			numberbasedDuplicates.setText(String.valueOf(numberBasedTempMap.keySet().size()));
			nameBasedDuplicates.setText(String.valueOf(nameBasedTempMap.keySet().size()));
			invisibleContacts.setText(String.valueOf(invisblecout));
		}

		

		@Override
		protected void onPreExecute() {
			progressBar = new ProgressDialog(Detector.this);
			
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setMessage("Verifying Contacts ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
			myProgress = 0;
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			
			  
					ContentResolver cr = getContentResolver();
			    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 ", null, null);
			    	
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
			 	                 
			 	 	            String contactname = pCur.getString(pCur.getColumnIndex(Phone.DISPLAY_NAME));	
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
			                           sbr.append(contactNumber);
			              	          
			                        
			                           
			                          
			                           System.out.println(contactname +" : " + contactNumber);
			                           if(contactname==null || contactname.equalsIgnoreCase("null"))
			                           {
			                        	   contactname = "Empty Contact";
			                 
			                           }
			                           cb.setContactName(contactname.toLowerCase());
			             
			                           if(numberBasedMap.get(contactNumber)!=null && contactNumber!=null && contactNumber.length()>2){
					              	    	  List<Contactbean> list = numberBasedMap.get(contactNumber);
					              	    	  list.add(cb);
					              	    	  }else{
					              	    	  List<Contactbean> list = new ArrayList<Contactbean>();
					              	    	  list.add(cb);
					              	    	  numberBasedMap.put(contactNumber,list);
					              	    }
			                           
			                           
			                           if(nameBasedMap.get(contactname)!=null && contactNumber!=null && contactNumber.length()>2){
					              	    	  List<Contactbean> list = nameBasedMap.get(contactname);
					              	    	  list.add(cb);
					              	    	  }else{
					              	    	  List<Contactbean> list = new ArrayList<Contactbean>();
					              	    	  list.add(cb);
					              	    	nameBasedMap.put(contactname,list);
					              	    }
			                           
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
			          
			          for (Iterator iterator = numberBasedMap.keySet().iterator(); iterator.hasNext();) 
			          {
			        	 	String number = (String) iterator.next();
			      		    List<Contactbean> cdl =  (List<Contactbean>) numberBasedMap.get(number);
			                 if(cdl.size()>=2 && isValiddupnumber(cdl))
			                 {
			                	 numberBasedTempMap.put(number, cdl);
			                 }
			          }

			          
			          for (Iterator iterator = nameBasedMap.keySet().iterator(); iterator.hasNext();) 
			          {
			        	 	String name = (String) iterator.next();
			      		    List<Contactbean> cdl =  (List<Contactbean>) nameBasedMap.get(name);
			                 if(cdl.size()>=2 && isValiddupname(cdl))
			                 {
			                	 nameBasedTempMap.put(name, cdl);
			                 }
			          }
			                 
			          System.out.println(nameBasedMap.size());
			          System.out.println(nameBasedTempMap.size());
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
			  				tempcontactsList.add(objBean.getContactName()+":"+objBean.getContactNumber());
			  			}else{
			  				
			  				if(!objBean.getContactName().equals("Empty Contact"))
			  			       dupList.add(objBean);
			  			}
			  			
			  		}
			  		
			  		Cursor cur2 = null;
					String countvalue = null;
					try {
						
						//ContentResolver cr = getContentResolver();
				    	cur2 = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 0 ", null, null);
				    	invisblecout += cur2.getCount();
				    	
				   } catch (Exception e) {
					e.printStackTrace();
					}
					cur2.close();
					
					
					try {
						
						String[] projection2 = new String[] {
				                ContactsContract.Contacts._ID,
				                ContactsContract.Contacts.DISPLAY_NAME
				        };
				        String selection2 = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 AND "+ContactsContract.Contacts.HAS_PHONE_NUMBER +" = 0";
				        String[] selectionArgs2 = null;
				        String sortOrder2 = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
				        int tmpcountvalue = 0;
				        myProgress =0;
				        ContentResolver cr2 = getContentResolver();
				        Cursor cur3 = cr2.query(ContactsContract.Contacts.CONTENT_URI,projection2,selection2, selectionArgs2, sortOrder2);
			    	
				        progressBar.setMax(cur3.getCount());
				     if(cur3!=null && cur3.getCount()>0){
				        while (cur3.moveToNext()) {
			     	
			            	 myProgress++;
			            	progressBar.setProgress(myProgress);
			            	 String id = cur3.getString(cur3.getColumnIndex(ContactsContract.Contacts._ID));
			            	 String displayname = cur3.getString(cur3.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			            	 System.out.println("No Number :" + displayname);
			            	 
			            	 if(!haseMailId(id) && !hasNote(id) && !hasaddress(id))
			            	 {
			            		 tmpcountvalue++; 
			            	 }
			            	 
			            	 
			            }
				        
				        invisblecout+=tmpcountvalue;
					}
			          if(cur3!=null)cur3.close();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					
			  		return null;
				
		}



		private int getEmptyContactsCout() {
			
			return 0;
		}

		
	}



public boolean isValiddupname(List<Contactbean> cdl) {
	
	try {
		
		List<String>  obj = new ArrayList<String>();
		
		for (Iterator iterator = cdl.iterator(); iterator.hasNext();) {
			Contactbean contactbean = (Contactbean) iterator.next();
					if(!obj.contains(contactbean.getContactNumber())){
					obj.add(contactbean.getContactNumber());	
				}else{
					iterator.remove();
				}
		}
		
		if(obj.size()>1)
		{
			return true;
		}
		
	} catch (Exception e) {
		e.printStackTrace();// TODO: handle exception
	}
	
	return false;
}

public boolean isValiddupnumber(List<Contactbean> cdl) {
	
	try {
	List<String>  obj = new ArrayList<String>();
	for (Iterator iterator = cdl.iterator(); iterator.hasNext();) {
			Contactbean contactbean = (Contactbean) iterator.next();
				if(!obj.contains(contactbean.getContactName())){
					obj.add(contactbean.getContactName());	
				}else{
					iterator.remove();
				}
	}
		
		if(obj.size()>1)
		{
			return true;
		}
		
	} catch (Exception e) {
		e.printStackTrace();// TODO: handle exception
	}
	
	return false;
}
	
	
public class DeleteContacts extends AsyncTask<Void, Integer, Void> {
	
	int deletecontactNumbers ;

	@Override
	protected void onPostExecute(Void result) {
		 try {
			 progressBar.dismiss();
			 progressBar = null;
		    } catch (Exception e) {
		        // nothing
		    }
		 //removeduplicatesButton.setEnabled(false);
		 viewduplicatesButton.setEnabled(false);
		// exportContactsButton.setEnabled(false);
		//findduplicatesButton.setEnabled(true);
		totalnumberofcontactsview.setText(String.valueOf(contactsList.size() - deletecontactNumbers));
	    numberOfDuplicateCounts.setText(String.valueOf(dupList.size()));
	}

	@Override
	protected void onPreExecute() {
		deletecontactNumbers = 0;
	}
	
	@Override
	protected Void doInBackground(Void... param) {
		ContentResolver cr = getContentResolver();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		progressBar.setMax(dupList.size());
		
		for (Iterator<Contactbean> iterator = dupList.iterator(); iterator.hasNext();) {
			deletecontactNumbers++;
			Contactbean object =  iterator.next();
		   	String where = ContactsContract.Data.CONTACT_ID + " = ? ";
	    	String[] params = new String[] {String.valueOf(object.getContactid())};
	       cr.delete(ContactsContract.RawContacts.CONTENT_URI, where, params);
	    	//ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(where, params).build());
	        iterator.remove();
	        progressBar.setProgress(deletecontactNumbers);
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
