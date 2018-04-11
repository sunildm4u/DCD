package com.makelifesimple.duplicatedetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplelife.beans.AddressBean;
import com.simplelife.beans.Contactbean;
import com.simplelife.beans.ContactsMainBean;
import com.simplelife.beans.EmailBean;
import com.simplelife.beans.PhoneBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import com.google.analytics.tracking.android.EasyTracker;

public class MergeContacts extends Activity {
	
	int contactcount;
	List<String> groupList;
    List<String> childList;
	String countrycode;
	TextView tempView;
	Map<String,List<Contactbean>> aMap = new HashMap<String,List<Contactbean>>();
	Map<String,List<Contactbean>> tempMap = new HashMap<String,List<Contactbean>>();
	Map<String, List<String>> laptopCollection;
	List<Contactbean> contactBeanList;
	List<String> idsList = new ArrayList<String>();
Button b1;
	ExpandableListView expListView;
	ProgressDialog progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merge_contacts);
		
		
		
		tempView = (TextView)findViewById(R.id.labelview);
		b1 = (Button) findViewById(R.id.button1);
		Bundle extras = getIntent().getExtras();
		countrycode = extras.getString("contryCode");
		readContacts();
	}

	public void readContacts(){
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
		        
		        createGroupList();
				 
		        createCollection();
		        
		        
		        if(groupList.size()>0){
		        	
		        		tempView.setVisibility(TextView.INVISIBLE);
		        	    expListView = (ExpandableListView) findViewById(R.id.androidlist3);
						final ExpandableListAdapter2 expListAdapter = new ExpandableListAdapter2(
						MergeContacts.this, groupList, laptopCollection);
						expListView.setAdapter(expListAdapter);
						
						
		        }else{
		        	tempView.setText("You Dont have any Name Based duplicate contacts(Same Name having more that one Number)");
		        	tempView.setVisibility(TextView.VISIBLE);
		        	b1.setVisibility(Button.INVISIBLE);
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
	        if (cur!=null && cur.getCount()>0){
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
	              	         
	              	         switch(pCur.getInt(pCur.getColumnIndex(Phone.TYPE)))
	              	         {
	              	       case 1:sbr.append("Home   :");break;
	              	       case 2:sbr.append("Mobile :");break;
	              	       case 3:sbr.append("Work   :");break;
			               default:break;
	              	         }
	              	         
	              	         
	              	           sbr.append(contactNumber + "\n");
	              	          
	                        
	                           
	                           String contactname = pCur.getString(pCur.getColumnIndex(Phone.DISPLAY_NAME));
	                  
	                           
	                           
	                           if(contactname==null || contactname.equalsIgnoreCase("null"))
	                           {
	                        	   contactname = "Empty Contact";
	                           }
	                           cb.setContactName(contactname.toLowerCase());
	                           
	              	         
		               	       if(aMap.get(contactname)!=null && contactNumber!=null && contactNumber.length()>2)
		               	       {
		              	    	  List<Contactbean> list = aMap.get(contactname);
		              	    	  list.add(cb);
		               	       }else{
		              	    	  List<Contactbean> list = new ArrayList<Contactbean>();
		              	    	  list.add(cb);
		              	    	  aMap.put(contactname,list);
		              	    	}
	                   }
	 	 	           }
	              	  if(pCur!=null) pCur.close();
	              	   
	              	   if(sbr.toString().length()>2){
	              		 cb.setContactNumber(sbr.toString().substring(0,sbr.length()-1));   
	              	   }
	              	   
	                   
	                   progressBar.setProgress(myProgress);
	            
	          }
	        }
	        }
            
         if(cur!=null)cur.close();
         
         for (Iterator iterator = aMap.keySet().iterator(); iterator.hasNext();) {
        	 	String name = (String) iterator.next();
			    List<Contactbean> cdl =  (List<Contactbean>) aMap.get(name);
	             if(cdl.size()>=2 && isValid(cdl))
	             {
	            	 tempMap.put(name, cdl);
	             }
			
			
		}
         System.out.println(aMap.size());
         System.out.println(tempMap.size());
         
         
         for(Iterator<Entry<String, List<Contactbean>>> it = tempMap.entrySet().iterator(); it.hasNext(); ) {
		        Entry<String, List<Contactbean>> entry = it.next();
		        System.out.println("Contact Name :"+entry.getKey());
		       for (Iterator<Contactbean> iterator = entry.getValue().iterator(); iterator.hasNext();) {
		    	   Contactbean type = (Contactbean) iterator.next();
		    	   System.out.println("id  "+type.getContactid());
		       	}
		      }
         
         StringBuffer sb1 = new StringBuffer();

  		return null;
		}

		
	}


private void createGroupList() {
    groupList = new ArrayList<String>();
    groupList = new ArrayList<String>(tempMap.keySet());
}

private void createCollection() {


    laptopCollection = new LinkedHashMap<String, List<String>>();
    
    contactBeanList = new ArrayList(tempMap.values());
    
    for (int i = 0; i < contactBeanList.size(); i++) { 
    	List<Contactbean>  cblist= (List<Contactbean>) contactBeanList.get(i);
    	List<String> tmpList = new ArrayList<String>();
    	for (Iterator iterator = cblist.iterator(); iterator.hasNext();) {
			Contactbean contactbean = (Contactbean) iterator.next();
			//tmpList.add(contactbean.getContactNumber() +"( Added :" +contactbean.getCreateddate()+")");
			tmpList.add(contactbean.getContactNumber());
    	}
    	loadChild(tmpList.toArray(new String[tmpList.size()]));
    	laptopCollection.put(groupList.get(i), childList);
    }
}

private void loadChild(String[] laptopModels) {
    childList = new ArrayList<String>();
    
    for (int i = 0; i < laptopModels.length; i++) {
    	// childList.add("Conatct "+i+ " : " +laptopModels[i]);
    	 childList.add(laptopModels[i]);
	}
    
   // for (String model : laptopModels)
       
}

public boolean isValid(List<Contactbean> cdl) {
	
	try {
		
		List obj = new ArrayList<String>();
		String tmpNumber ;
		for (Iterator iterator = cdl.iterator(); iterator.hasNext();) {
			Contactbean contactbean = (Contactbean) iterator.next();
			if(!obj.contains(contactbean.getContactNumber()))
			{
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
	
	
public class ExpandableListAdapter2 extends BaseExpandableListAdapter {

	private Activity context;
	private Map<String, List<String>> laptopCollections;
	private List<String> laptops;

	public ExpandableListAdapter2(Activity context, List<String> laptops,
			Map<String, List<String>> laptopCollections) {
		this.context = context;
		this.laptopCollections = laptopCollections;
		this.laptops = laptops;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return laptopCollections.get(laptops.get(groupPosition)).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String laptop = (String) getChild(groupPosition, childPosition);
		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.child_item_merge, null);
		}

		TextView item = (TextView) convertView.findViewById(R.id.laptop);
		TextView item0 = (TextView) convertView.findViewById(R.id.contactCount);
		item0.setText("Contact "  +String.valueOf(childPosition));
		item.setText(laptop);
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return laptopCollections.get(laptops.get(groupPosition)).size();
	}

	public Object getGroup(int groupPosition) {
		return laptops.get(groupPosition);
	}

	public int getGroupCount() {
		return laptops.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String laptopName = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.group_item, null);
		}
		TextView item = (TextView) convertView.findViewById(R.id.laptop);
		item.setTypeface(null, Typeface.BOLD);
		item.setText(laptopName);
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	
}

public void mergecontacts(View view)
{
	try {
		
		new RTL().execute();
		
	} catch (Exception e) {
		e.printStackTrace();// TODO: handle exception
	}
	
}

	
		
public class RTL extends AsyncTask<Void, Integer, Void> {
	
	int myProgress;

	@Override
	protected void onPostExecute(Void result) {
		
		progressBar.dismiss();
		//System.out.println(idsList +":"+insertionList);
		new DeleteContactsSelected().execute();
		
	}

	@Override
	protected void onPreExecute() {
		myProgress = 0;
		progressBar = new ProgressDialog(MergeContacts.this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Merging Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
		
	}
	
	



	@Override
	protected Void doInBackground(Void... params) {
		
		try {
			
			progressBar.setMax(tempMap.size());
			
			 for(Iterator<Entry<String, List<Contactbean>>> it = tempMap.entrySet().iterator(); it.hasNext(); ) {
			        Entry<String, List<Contactbean>> entry = it.next();
			        
			        ArrayList<Contactbean> objlist = new ArrayList<Contactbean>(entry.getValue());
			        myProgress++;
					progressBar.setProgress(myProgress);
			       
					List<ContactsMainBean> tmpList = new ArrayList<ContactsMainBean>(); 
					
					for (Iterator<Contactbean> iterator = objlist.iterator(); iterator.hasNext();) {
						
						Contactbean obj = (Contactbean) iterator.next();
						tmpList.add(readContacts(String.valueOf(obj.getContactid())));
						idsList.add(String.valueOf(obj.getContactid()));
					}
					
					ContactsMainBean contactsMainBean = new ContactsMainBean();
					
					for (Iterator<ContactsMainBean> iterator = tmpList.iterator(); iterator.hasNext();) {
						ContactsMainBean objMainBean = (ContactsMainBean) iterator.next();
						contactsMainBean.setName(objMainBean.getName());
						contactsMainBean.setPhonelist(objMainBean.getPhonelist());
						contactsMainBean.setEmaillist(objMainBean.getEmaillist());
						contactsMainBean.setAddressbean(objMainBean.getAddressbean());
						contactsMainBean.setNotesList(objMainBean.getNotesList());
						}
					
					ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();
					
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
					 
					 
					List <PhoneBean> objList =  contactsMainBean.getPhonelist();
					 
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
						     Toast.makeText(MergeContacts.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
               }}
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
              if(noteCur!=null) noteCur.close();
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
			 Toast.makeText(MergeContacts.this, "Error Occured :" + exception, Toast.LENGTH_LONG).show();
		 }
		 
		 //groupList.clear();
		 //childList.clear();
		 
//		expListView.setVisibility(ExpandableListView.INVISIBLE);
//		tempView.setText("You Dont have Name Based duplicate contacts(Same Name having more that one Number)");
//		tempView.setVisibility(TextView.VISIBLE);
//		b1.setVisibility(Button.INVISIBLE);
		 
		 
		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MergeContacts.this);
		 
			// set title
			alertDialogBuilder.setTitle("Merge Contacts");
			alertDialogBuilder.setIcon(R.drawable.rate2);
			alertDialogBuilder.setMessage("Contacts With Same Name and Different Number are succesfully Merged :)")

			    .setCancelable(false)
				.setPositiveButton("ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						Intent i = new Intent(MergeContacts.this,MainActivity2.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						dialog.cancel();
					}
				  
				});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
	}

	@Override
	protected void onPreExecute() {
		deleteconatactemailidss = 0;
		progress = 0;
    	progressBar = new ProgressDialog(MergeContacts.this);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(false);
		progressBar.setMessage("Deleting old  Contacts ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.show();
	}
	
	@Override
	protected Void doInBackground(Void... param) 
	{
		
		try{
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
        	  exception = e.getMessage();

        	  e.printStackTrace();
          }
        	
        }
		}catch (Exception e) {
			  exception = e.getMessage();

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
 // EasyTracker.getInstance(this).activityStop(this);  // Add this method.
}


public void onBackPressed() {
	   Log.d("CDA", "onBackPressed Called");
	   Intent i = new Intent(MergeContacts.this,MainActivity2.class);
	   i.putExtra("contryCode", countrycode);
	   startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.merge_contacts, menu);
		return true;
	}

}
