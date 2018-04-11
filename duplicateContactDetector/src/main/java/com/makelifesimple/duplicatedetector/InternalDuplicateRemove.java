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
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplelife.beans.AddressBean;
import com.simplelife.beans.ContactbeanDup;
import com.simplelife.beans.ContactsMainBean;
import com.simplelife.beans.EmailBean;
import com.simplelife.beans.PhoneBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import com.google.analytics.tracking.android.EasyTracker;

public class InternalDuplicateRemove extends BaseActivity {

	
	List<ContactbeanDup> dupNamesList = new ArrayList<ContactbeanDup>();
	List<ContactsMainBean> insertionList = new ArrayList<ContactsMainBean>();
	List<String> idsList = new ArrayList<String>();
	ProgressDialog progressBar;
	TextView tv;
	Button b1;
	Button b2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_internal_duplicate_remove);
		tv = (TextView) findViewById(R.id.intdupcount);
		b1 = (Button) findViewById(R.id.button2);
		b2 = (Button) findViewById(R.id.button1);
		new BackgroundAsyncTask().execute();
		
	}
	
	
	
	public void viewduplicates(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(InternalDuplicateRemove.this);
		builder.setTitle("Contacts with Duplicate Internal Data");
		ListView modeList = new ListView(InternalDuplicateRemove.this);
		modeList.setBackgroundColor(0xFFFFFF);
		modeList.setCacheColorHint(00000000);
		modeList.setScrollingCacheEnabled(false);
		ArrayAdapter<ContactbeanDup> modeAdapter = new ArrayAdapter<ContactbeanDup>(InternalDuplicateRemove.this,android.R.layout.simple_list_item_1, dupNamesList);
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
			progressBar = new ProgressDialog(InternalDuplicateRemove.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Inspecting Contacts ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
		}
		
		
	


		@Override
		protected Void doInBackground(Void... params) {
	
			ContentResolver cr = getContentResolver();
			
	    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
			cur.moveToFirst();
			progressBar.setMax(cur.getCount());
	    	if(cur!=null && cur.getCount()>0){
            while (cur.moveToNext()) {
            	
	            	 myProgress++;
	            	 progressBar.setProgress(myProgress);
	            	 String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	                 if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
	                 {
	             	   List<String> contactsDataList = new ArrayList<String>();
	                   Cursor pCur = cr.query(Phone.CONTENT_URI, null,Phone.CONTACT_ID + " = " + id, null, null);
	                   String contactNumber="";
	                   
	                   if(pCur!=null && pCur.getCount()>0){
	                  int count = 1;
	                   while (pCur.moveToNext()) 
	                   {
	                	   String typeid = pCur.getString(pCur.getColumnIndex(Phone.TYPE));
	                	   String type = pCur.getString(pCur.getColumnIndex(Phone.TYPE));
	                	   switch(pCur.getInt(pCur.getColumnIndex(Phone.TYPE)))
	              	       {
	              	       case 1:type = "Home   ";break;
	              	       case 2:type = "Mobile   ";break;
	              	       case 3:type = "Work   ";break;
			               default:break;
	              	       }
	                	   
	                	    String displayname = pCur.getString(pCur.getColumnIndex(Phone.DISPLAY_NAME));
	                	    
	                 	    String tempnumber = pCur.getString(pCur.getColumnIndex(Phone.NUMBER));
	                	    
	                	    if(tempnumber!=null && tempnumber.length()>0)
	                	    {
	                	    	tempnumber = tempnumber.replaceAll("[^+0-9]+","");
	                	    }
	                	    
	                	    if(tempnumber!=null && tempnumber.length()>10)
	                	    {
	                	    	tempnumber = tempnumber.substring(tempnumber.length()-10, tempnumber.length());
	                	    }
	                	    
	                	    if(contactsDataList!=null && contactsDataList.size()!=0 && contactsDataList.contains(tempnumber))
	                	     {
	                	    	 
	                	    	 removefromlist(displayname,tempnumber);
	                	    	
	                	    	 count++;
	                	    	 
	                	    	 ContactbeanDup cb = new ContactbeanDup();
	                	    	 cb.setContactid(Integer.valueOf(id).intValue());
	                	    	 cb.setContactName(displayname);
	                	    	 cb.setTypeid(typeid);
	                	    	 cb.setContactNumber(tempnumber);
	                	    	 cb.setType(type);
	                	    	 cb.setCount(count);
	                	    	 dupNamesList.add(cb);
	                	    	 
	                	      }else{
	                	    	  count=1;
	                			  contactsDataList.add(tempnumber);
	                			
	                		 }
	                	   
	                	  
			               //System.out.println(contactname +" : " + contactNumber);
	                   }
			 	 	   }
	                  // System.out.println("---------------");
	              	 if(pCur!=null)  pCur.close();
                     
	            }else{
	            	//noNumberIds.add(id);
	            }
	          }
            }
			        
	          
	         if(cur!=null) cur.close();
	          
	        
	  		return null;
		}

		
	}


public class DeleteContacts extends AsyncTask<Void, Integer, Void> {
	
	int myProgress;

	@Override
	protected void onPostExecute(Void result) {
		
		progressBar.dismiss();
		System.out.println(idsList +":"+insertionList);
		new DeleteContactsSelected().execute();
		
	}

	@Override
	protected void onPreExecute() {
		myProgress = 0;
		progressBar = new ProgressDialog(InternalDuplicateRemove.this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Analysing duplicates with in a Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
		
	}
	
	



	@Override
	protected Void doInBackground(Void... params) {
		
		try {
			progressBar.setMax(dupNamesList.size());
			
			for (Iterator<ContactbeanDup> iterator = dupNamesList.iterator(); iterator.hasNext();) {
				myProgress++;
				progressBar.setProgress(myProgress);
				ContactbeanDup obj = (ContactbeanDup) iterator.next();
				ContactsMainBean CMB = readContacts(String.valueOf(obj.getContactid()));
				if(!idsList.contains(CMB.getContact_id())){
				idsList.add(CMB.getContact_id());
				insertionList.add(CMB);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
}

public ContactsMainBean readContacts(String id){
    ContentResolver cr = getContentResolver();
    //Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1", null, null);

    //if (cur.getCount() > 0) {
      // while (cur.moveToNext()) {
       	ContactsMainBean cmb = new ContactsMainBean();
       	
         //  String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
           cmb.setContact_id(id);
           
                 Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{id}, null);
                List<PhoneBean> phonelist = new ArrayList<PhoneBean>();
               
           
                if(pCur!=null && pCur.getCount()>0){
                while (pCur.moveToNext()) {
                	
                    String name = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    cmb.setName(name);
                	
               	     PhoneBean pb = new PhoneBean();
             	
                     String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                     

             	    if(phone!=null && phone.length()>0)
             	    {
             	    	phone = phone.replaceAll("[^+0-9]+","");
             	    }
                     
                     //String tempnumber = pCur.getString(pCur.getColumnIndex(Phone.NUMBER));
                     pb.setNumber(phone);
                     String type = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                     pb.setType(type);
                     
                     //if(!phonelist.contains(phone))
                     phonelist.add(pb);
               }
                }
               if(pCur!=null)pCur.close();
               cmb.setPhonelist(phonelist);

               // get email and type

              Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id}, null);
              List<EmailBean> emailList = new ArrayList<EmailBean>();
             if(emailCur!=null && emailCur.getCount()>0){
              while (emailCur.moveToNext()) {
           	    EmailBean eb = new EmailBean();
                   String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                   eb.setEmail(email);
                   String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                   eb.setEmailType(emailType);
                  emailList.add(eb);
               }
             }
              if(emailCur!=null) emailCur.close();
               cmb.setEmaillist(emailList);

               // Get note.......
               String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
               String[] noteWhereParams = new String[]{id,ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
               Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
               List<String> noteslist = new ArrayList<String>();
               if(noteCur!=null && noteCur.getCount()>0){
               while (noteCur.moveToNext()) {
                   String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                   noteslist.add(note);
                   System.out.println("Note " + note);
               }
               }
               if(noteCur!=null)noteCur.close();
               cmb.setNotesList(noteslist);
               //Get Postal Address....

               String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
               String[] addrWhereParams = new String[]{id,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
               Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,null, addrWhere, addrWhereParams, null);
               List<AddressBean> addresslist = new ArrayList<AddressBean>();
               if(addrCur!=null && addrCur.getCount()>0){
               while(addrCur.moveToNext()) {
               	AddressBean aBean = new AddressBean();
                   String poBox = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                   String street = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                   String city = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                   String state = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                   String postalCode = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                   String country = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                   String type = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                   aBean.setAddresstype(type);
                   aBean.setCity(city);
                   aBean.setCountry(country);
                   aBean.setPoBox(poBox);
                   aBean.setPostalCode(postalCode);
                   aBean.setState(state);
                   aBean.setStreet(street);
                   addresslist.add(aBean);
               }
               }
               if(addrCur!=null)addrCur.close();
               cmb.setAddressbean(addresslist);


               String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
               String[] orgWhereParams = new String[]{id,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
               Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,null, orgWhere, orgWhereParams, null);
               if(orgCur!=null && orgCur.getCount()>0){
               if (orgCur.moveToFirst()) {
                   String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                   String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
               }
               }
               if(orgCur!=null)orgCur.close();
               
               
           
           return cmb;

}

	


	public void inertnewcontacts(List<ContactsMainBean> insertionList2) {
	try {
		
		ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();
		
		for (Iterator<ContactsMainBean> iterator = insertionList2.iterator(); iterator.hasNext();) {
			
			ContactsMainBean contactsMainBean = (ContactsMainBean) iterator.next();
			
			 ops.add(ContentProviderOperation.newInsert(
			 ContactsContract.RawContacts.CONTENT_URI)
			     .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
			     .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
			     .build());
			 
			 if (contactsMainBean.getName() != null) {
			     ops.add(ContentProviderOperation.newInsert(
			     ContactsContract.Data.CONTENT_URI)
			         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			         .withValue(ContactsContract.Data.MIMETYPE,
			     ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
			         .withValue(
			     ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
			     contactsMainBean.getName()).build());
			 }
			 
			 
			 for (Iterator<PhoneBean> iterator2 = contactsMainBean.getPhonelist().iterator(); iterator2.hasNext();) {
				PhoneBean phineobj = (PhoneBean) iterator2.next();
				ops.add(ContentProviderOperation.
					     newInsert(ContactsContract.Data.CONTENT_URI)
					         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					         .withValue(ContactsContract.Data.MIMETYPE,
					     ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					         .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phineobj.getNumber())
					         .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phineobj.getType())
					         .build());
			}
			 
		}
		try {
		     getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		 } catch (Exception e) {
		     e.printStackTrace();
		     Toast.makeText(InternalDuplicateRemove.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		 } 
		
	} catch (Exception e) {
		e.printStackTrace();// TODO: handle exception
	}
	
}



	public void removefromlist(String displayname, String Numnber) {
		
		try {
			
			for (Iterator iterator = dupNamesList.iterator(); iterator.hasNext();) {
				ContactbeanDup obj = (ContactbeanDup) iterator.next();
				if(obj.getContactName().equals(displayname)&&obj.getContactNumber().equals(Numnber))
				{
					iterator.remove();
				}
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}
		
		
	}
	
public class DeleteContactsSelected extends AsyncTask<Void, Integer, Void> {
		
		int deleteconatactemailidss ;
		int progress;
		String exception="";

		@Override
		protected void onPostExecute(Void result) {
			 try {
				 progressBar.dismiss();
				 progressBar = null;
			    } catch (Exception e) {
			        // nothing
			    }
			 
			 if(exception.length()>0)
			 {
				 Toast.makeText(InternalDuplicateRemove.this, "Error Occured :" + exception, Toast.LENGTH_LONG).show();
			 }
			 
			 dupNamesList.clear();
			new InsertContactsSelected().execute();
		}

		@Override
		protected void onPreExecute() {
			deleteconatactemailidss = 0;
			progress = 0;
	    	progressBar = new ProgressDialog(InternalDuplicateRemove.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("processing  Contacts ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
		}
		
		@Override
		protected Void doInBackground(Void... param) {
			ContentResolver cr = getContentResolver();
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			progressBar.setMax(idsList.size());
			int c = idsList.size();
			int tmpnumber = 0 ;
			for (Iterator iterator = idsList.iterator(); iterator.hasNext();) {
				deleteconatactemailidss++;
				progressBar.setProgress(deleteconatactemailidss);
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
		                ops =  ops = new ArrayList<ContentProviderOperation>(100);
		                tmpnumber = 0;
		          } catch (Exception e) {
		        	  exception = e.getMessage();
		        	  e.printStackTrace();
		        	  
		          }
		    	}
		    	
		        iterator.remove();
		       
		     }
			
	        if(ops.size()>0)
	        {
	        	try {
	        		progressBar.setProgress(c);
	        		 cr.applyBatch(ContactsContract.AUTHORITY, ops);
	    		       ops.clear();
	                
	          } catch (Exception e) {
	        	  e.printStackTrace();
	          }
	        	
	        }
	        
			return null;
		}
		
	}
	

public class InsertContactsSelected extends AsyncTask<Void, Integer, Void> {
	
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
		 
		 tv.setText("0");
		 	b1.setEnabled(false);b1.setVisibility(View.INVISIBLE);
			b2.setEnabled(false);b2.setVisibility(View.INVISIBLE);
		
	}

	@Override
	protected void onPreExecute() {
		deleteconatactemailidss = 0;
		progress = 0;
    	progressBar = new ProgressDialog(InternalDuplicateRemove.this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Cleaning Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
	}
	
	@Override
	protected Void doInBackground(Void... param) {
		
		
		progressBar.setMax(insertionList.size());
		
		for (Iterator<ContactsMainBean> iterator = insertionList.iterator(); iterator.hasNext();) {
			
			progress++;
			progressBar.setProgress(progress);
			ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();
			
			ContactsMainBean contactsMainBean = (ContactsMainBean) iterator.next();
			
			ops.add(ContentProviderOperation.newInsert(
			 ContactsContract.RawContacts.CONTENT_URI)
			     .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
			     .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
			     .build());
			 
			 if (contactsMainBean.getName() != null) {
			     ops.add(ContentProviderOperation.newInsert(
			     ContactsContract.Data.CONTENT_URI)
			         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			         .withValue(ContactsContract.Data.MIMETYPE,
			     ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
			         .withValue(
			     ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
			     contactsMainBean.getName()).build());
			 }
			 
			 
			List <PhoneBean> objList =  removeduplicatesfornumber(contactsMainBean.getPhonelist());
			 
			 for (Iterator<PhoneBean> iterator2 = objList.iterator(); iterator2.hasNext();) {
				PhoneBean phineobj = (PhoneBean) iterator2.next();
				ops.add(ContentProviderOperation.
					     newInsert(ContactsContract.Data.CONTENT_URI)
					         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					         .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					         .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phineobj.getNumber())
					         .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phineobj.getType())
					         .build());
			}
			 
			 List <EmailBean> objList2 =  removeduplicatesforemail(contactsMainBean.getEmaillist());
			 for (Iterator<EmailBean> iterator2 = objList2.iterator(); iterator2.hasNext();) {
				 EmailBean emailobj = (EmailBean) iterator2.next();
			     ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			         .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
			         .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailobj.getEmail())
			         .withValue(ContactsContract.CommonDataKinds.Email.TYPE,  emailobj.getEmailType())
			         .build());
			 
			 }
			 
			
			 List <AddressBean> objList3 =  removeduplicatesforaddress(contactsMainBean.getAddressbean());
			 for (Iterator<AddressBean> iterator2 = objList3.iterator(); iterator2.hasNext();) {
				 AddressBean addobj = (AddressBean) iterator2.next(); 
				 	  ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					 .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                     .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                     .withValue(ContactsContract.Data.DATA5, addobj.getPoBox())
                     .withValue(ContactsContract.Data.DATA4, addobj.getStreet())
                     .withValue(ContactsContract.Data.DATA7, addobj.getCity())
                     .withValue(ContactsContract.Data.DATA8, addobj.getState())
                     .withValue(ContactsContract.Data.DATA9, addobj.getPostalCode())
                     .withValue(ContactsContract.Data.DATA10, addobj.getCountry())
                     .withValue(ContactsContract.Data.DATA2, addobj.getAddresstype())
                     .build());
			 }
			 
			List<String> notes = contactsMainBean.getNotesList();
			for (Iterator<String> iterator2 = notes.iterator(); iterator2.hasNext();) {
				String addobj = (String) iterator2.next(); 
				 	 ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.Data.DATA1, addobj)
                    .build());
			 }
			 
				try {
				     getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
				 } catch (Exception e) {
				     e.printStackTrace();
				     Toast.makeText(InternalDuplicateRemove.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				 } 
		}

		
		
		
		
		
        
		return null;
	}
	
}

public List<PhoneBean> removeduplicatesfornumber(List<PhoneBean> phonelist) {
	
	List<String> tempList = new ArrayList<String>();
	List<PhoneBean> phonebeanList = new ArrayList<PhoneBean>();
	try {
		
		for (Iterator<PhoneBean> iterator = phonelist.iterator(); iterator.hasNext();) {
			PhoneBean phoneBean = (PhoneBean) iterator.next();
			String tempnumber = phoneBean.getNumber();
			if(tempnumber!=null && tempnumber.length()>0)
    	    {
    	    	tempnumber = tempnumber.replaceAll("[^+0-9]+","");
    	    }
    	    
    	    if(tempnumber!=null && tempnumber.length()>10)
    	    {
    	    	tempnumber = tempnumber.substring(tempnumber.length()-10, tempnumber.length());
    	    }
			
			
			if(!tempList.contains(tempnumber)){
				tempList.add(tempnumber);
				phonebeanList.add(phoneBean);
			}
			  
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return phonebeanList;
	
}

public List<EmailBean> removeduplicatesforemail(List<EmailBean> phonelist) {
	
	List<String> tempList = new ArrayList<String>();
	List<EmailBean> phonebeanList = new ArrayList<EmailBean>();
	try {
		
		for (Iterator<EmailBean> iterator = phonelist.iterator(); iterator.hasNext();) {
			EmailBean phoneBean = (EmailBean) iterator.next();
			if(!tempList.contains(phoneBean.getEmail())){
				tempList.add(phoneBean.getEmail());
				phonebeanList.add(phoneBean);
			}
			  
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return phonebeanList;
	
}

public List<AddressBean> removeduplicatesforaddress(List<AddressBean> phonelist) {
	
	List<String> tempList = new ArrayList<String>();
	List<AddressBean> phonebeanList = new ArrayList<AddressBean>();
	try {
		
		for (Iterator<AddressBean> iterator = phonelist.iterator(); iterator.hasNext();) {
			AddressBean phoneBean = (AddressBean) iterator.next();
			
			if(!tempList.contains(phoneBean.getAddresstype()+":"+phoneBean.getCity()+":"+phoneBean.getCountry()+":"+phoneBean.getPoBox()+":"+phoneBean.getPostalCode()+":"+phoneBean.getState()+":"+phoneBean.getStreet()+":"+phoneBean.getState())){
				tempList.add(phoneBean.getAddresstype()+":"+phoneBean.getCity()+":"+phoneBean.getCountry()+":"+phoneBean.getPoBox()+":"+phoneBean.getPostalCode()+":"+phoneBean.getState()+":"+phoneBean.getStreet()+":"+phoneBean.getState());
				phonebeanList.add(phoneBean);
			}
			  
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return phonebeanList;
	
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
