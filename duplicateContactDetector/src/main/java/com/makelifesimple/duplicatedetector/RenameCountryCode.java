package com.makelifesimple.duplicatedetector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.simplelife.beans.AddressBean;
import com.simplelife.beans.Contactbean;
import com.simplelife.beans.ContactsMainBean;
import com.simplelife.beans.EmailBean;
import com.simplelife.beans.PhoneBean;


public class RenameCountryCode extends BaseActivity {

	ProgressDialog progressBar;
	List<Contactbean> numbersList = new ArrayList<Contactbean>();
	List<String> idsList = new ArrayList<String>();
	List<ContactsMainBean> datalist = new ArrayList<ContactsMainBean>();
	EditText et;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rename_country_code);
		et = (EditText)findViewById(R.id.editText1);
	}

	
	public void startwork(View view)
	{
		numbersList.clear();
		idsList.clear();
		datalist.clear();
		
		if(et.getText().toString().matches("^[0-9+]+$") && et.getText().toString().contains("+"))
		{
			new BackgroundAsyncTask().execute();
		}else{
			Toast.makeText(RenameCountryCode.this, "Country Code can only be Numbers with + Sign", Toast.LENGTH_LONG).show();
		}
		
	}
	
	
	
public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void> {
		
		int myProgress;

		@Override
		protected void onPostExecute(Void result) {
			progressBar.cancel();
			
			if(numbersList.size()==0)
			{
				Toast.makeText(RenameCountryCode.this, "Sorry, you have no numbers without country code(10 Digits)", Toast.LENGTH_LONG).show();
			}else{
			 AlertDialog.Builder builder = new AlertDialog.Builder(RenameCountryCode.this);
				builder.setTitle("Warning ..");
				builder.setIcon(R.drawable.warning);
				builder.setMessage(numbersList.size() +" Contacts will be appended with " +et.getText().toString() +" Country Code, Do you want to continue ?");
				builder.setCancelable(false);
				builder.setPositiveButton("Continue",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								new DeleteContactsSelected().execute();
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}
			
		}

		@Override
		protected void onPreExecute() {
			myProgress = 0;
			progressBar = new ProgressDialog(RenameCountryCode.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Verifying Contacts ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
		}
		
		
	


		@Override
		protected Void doInBackground(Void... params) {
	
			ContentResolver cr = getContentResolver();
	    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
            progressBar.setMax(cur.getCount());
	       if(cur!=null && cur.getCount()>0){
            while (cur.moveToNext()) {
	            	 myProgress++;
	            	 progressBar.setProgress(myProgress);
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
			 		             
			 		             if(!idsList.contains(id) && contactNumber.length()==10)
			 		             {
			 		            	 idsList.add(id);
			 		            	 numbersList.add(cb);
			 		            	ContactsMainBean CMB = readContacts2(String.valueOf(id));
			 						datalist.add(CMB);
			 		             }
	              	          
			 	 	      }
	                  	}
			 	 	    if(pCur!=null)pCur.close();
	                 }
            }}
	       if(cur!=null)cur.close();
	          
          return null;
		}

		
	}
	
public ContactsMainBean readContacts2(String id){
    ContentResolver cr = getContentResolver();
 
    ContactsMainBean cmb = new ContactsMainBean();
       	
       
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
               if(emailCur!=null)emailCur.close();
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
    }}
               if(addrCur!=null)addrCur.close();
               cmb.setAddressbean(addresslist);


               String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
               String[] orgWhereParams = new String[]{id,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
               Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,null, orgWhere, orgWhereParams, null);
               if (orgCur.moveToFirst()) {
                   String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                   String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
               }
               orgCur.close();
               
               
           
           return cmb;

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
			 Toast.makeText(RenameCountryCode.this, "Error Occured :" + exception, Toast.LENGTH_LONG).show();
		 }
		 
		// dupNamesList.clear();
		new InsertContactsSelected().execute();
	}

	@Override
	protected void onPreExecute() {
		deleteconatactemailidss = 0;
		progress = 0;
    	progressBar = new ProgressDialog(RenameCountryCode.this);
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
		 
		 Toast.makeText(RenameCountryCode.this, "D:one !!! ", Toast.LENGTH_LONG).show();
		
	}

	@Override
	protected void onPreExecute() {
		deleteconatactemailidss = 0;
		progress = 0;
    	progressBar = new ProgressDialog(RenameCountryCode.this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Cleaning Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
	}
	
	@Override
	protected Void doInBackground(Void... param) {
		
		
		progressBar.setMax(datalist.size());
		
		for (Iterator<ContactsMainBean> iterator = datalist.iterator(); iterator.hasNext();) {
			
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
			 
			 
			List <PhoneBean> objList =  appendcountrycode(contactsMainBean.getPhonelist());
			 
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
			 
			 List <EmailBean> objList2 =  contactsMainBean.getEmaillist();
			 for (Iterator<EmailBean> iterator2 = objList2.iterator(); iterator2.hasNext();) {
				 EmailBean emailobj = (EmailBean) iterator2.next();
			     ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			         .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
			         .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailobj.getEmail())
			         .withValue(ContactsContract.CommonDataKinds.Email.TYPE,  emailobj.getEmailType())
			         .build());
			 
			 }
			 
			
			 List <AddressBean> objList3 =  contactsMainBean.getAddressbean();
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
				     Toast.makeText(RenameCountryCode.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				 } 
		}

		
		
		
		
		
        
		return null;
	}

	private List<PhoneBean> appendcountrycode(List<PhoneBean> phonelist) {
		
		
		List<PhoneBean> phonebeanList = new ArrayList<PhoneBean>();
		try {
			
			for (Iterator<PhoneBean> iterator = phonelist.iterator(); iterator.hasNext();) {
				PhoneBean phoneBean = (PhoneBean) iterator.next();
				String tempnumber = phoneBean.getNumber();
				if(tempnumber!=null && tempnumber.length()==10)
	    	    {
	    	    	tempnumber = et.getText().toString()+tempnumber;
	    	    	
	    	    }
				phoneBean.setNumber(tempnumber);
			
					phonebeanList.add(phoneBean);
				
				  
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return phonebeanList;
		
		

	}
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rename_country_code, menu);
		return true;
	}

}
