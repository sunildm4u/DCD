package com.makelifesimple.duplicatedetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplelife.beans.Contactbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import com.google.analytics.tracking.android.EasyTracker;

public class NumberBasedDupRemoval extends BaseActivity {

	int contactcount;
	List<String> groupList;
    List<String> childList;
    Map<String, List<String>> laptopCollection;
	ArrayList<String> tempContactNumberList = new ArrayList<String>();
	//Map nameBasedMap = new HashMap();
	Map<String,List<Contactbean>> aMap = new HashMap<String,List<Contactbean>>();
	Map<String,List<Contactbean>> tempMap = new HashMap<String,List<Contactbean>>();

	ExpandableListView expListView;
	ProgressDialog progressBar;
	String countrycode;
	TextView tempView;
	ExpandableListAdapter2 expListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove15);
		
		tempView = (TextView)findViewById(R.id.labelview);
		
		Bundle extras = getIntent().getExtras();
		countrycode = extras.getString("contryCode");
		readContacts();
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
				tmpList.add(contactbean.getContactName());
        	}
        	loadChild(tmpList.toArray(new String[tmpList.size()]));
        	laptopCollection.put(groupList.get(i), childList);
        }
    }
 
    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }
 
    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
 
        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }
 
    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
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

List contactNumberlList;
List contactBeanList;

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
		            	//tempView.setVisibility();
		        	
		        	    expListView = (ExpandableListView) findViewById(R.id.androidlist);
						expListAdapter = new ExpandableListAdapter2(
						NumberBasedDupRemoval.this, groupList, laptopCollection);
						expListView.setAdapter(expListAdapter);
						
						Toast.makeText(NumberBasedDupRemoval.this, "Please retain only one contact name for a number ", Toast.LENGTH_LONG).show();
		        }else{
		        	tempView.setText("You Dont have duplicate contacts  - Same Number Having more that one display name");
		        	tempView.setVisibility(TextView.VISIBLE);
		        	
		        }
		      
		      
	
		}

		@Override
		protected void onPreExecute() {
			myProgress = 0;
		}
		
		
	


		@Override
		protected Void doInBackground(Void... params) {
	
			
			ContentResolver cr = getContentResolver();
			String[] projection = new String[] {
	                ContactsContract.Contacts._ID,
	                ContactsContract.Contacts.DISPLAY_NAME,
	                ContactsContract.Contacts.HAS_PHONE_NUMBER
	        };
	        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1";
	        String[] selectionArgs = null;
	        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,projection,selection, selectionArgs, sortOrder);
		
			
			
//			ContentResolver cr = getContentResolver();
//	    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
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
	              	           sbr.append(contactNumber);
	              	          
	                        
	                           
	                           String contactname = pCur.getString(pCur.getColumnIndex(Phone.DISPLAY_NAME));
	                           System.out.println(contactname +" : " + contactNumber);
	                           if(contactname==null || contactname.equalsIgnoreCase("null"))
	                           {
	                        	   contactname = "Empty Contact";
	                           }
	                           cb.setContactName(contactname.toLowerCase());
	                           
	              	         
		               	       if(aMap.get(contactNumber)!=null &&contactNumber!=null&&contactNumber.length()>2){
		              	    	  List<Contactbean> list = aMap.get(contactNumber);
		              	    	  list.add(cb);
		              	    	  }else{
		              	    	  List<Contactbean> list = new ArrayList<Contactbean>();
		              	    	  list.add(cb);
		              	    	  aMap.put(contactNumber,list);
		              	    	}
	                   }
	                   }
	                   
	              	   pCur.close();
	                   cb.setContactNumber(sbr.toString());
	                   progressBar.setProgress(myProgress);
	            
	          }
	        }
	        	}
        if(cur!=null) cur.close();
         
         for (Iterator iterator = aMap.keySet().iterator(); iterator.hasNext();) {
        	 	String number = (String) iterator.next();
			    List<Contactbean> cdl =  (List<Contactbean>) aMap.get(number);
	             if(cdl.size()>=2 && isValid(cdl))
	             {
	            	 if(number!=null && cdl!=null)
	            	 tempMap.put(number, cdl);
	             }
			
			
		}
         
         System.out.println(aMap);
         System.out.println(tempMap);

  		return null;
		}

		
	}
	List<String> child = null;


public class ExpandableListAdapter2 extends BaseExpandableListAdapter {

	private Activity context;
	private  Map<String, List<String>> laptopCollections;
	private List<String> laptops;

	public ExpandableListAdapter2(Activity context, List<String> laptops,
			Map<String, List<String>> laptopCollections) {
		this.context = context;
		this.laptopCollections = laptopCollections;
		this.laptops = laptops;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return laptopCollections.get(laptops.get(groupPosition)).get(
				childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String laptop = (String) getChild(groupPosition, childPosition);
		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.child_item, null);
		}

		TextView item = (TextView) convertView.findViewById(R.id.laptop);
		TextView item0 = (TextView) convertView.findViewById(R.id.contactCount);
		ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Do you want to remove?");
				builder.setCancelable(false);
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								child = laptopCollections.get(laptops.get(groupPosition));
								String[] tmpArray = new String[]{String.valueOf(childPosition),child.get(childPosition)};
								new RemoveCnotactTask().execute(tmpArray);
								//child.remove(childPosition);
								//notifyDataSetChanged();
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
		});
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


public class RemoveCnotactTask extends AsyncTask<String, Integer, Void> {
		
		int myProgress;
		String contactname;

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(NumberBasedDupRemoval.this, "Contact "+contactname+" is Removed", Toast.LENGTH_LONG).show();
			child.remove(tmpposition);
			expListAdapter.notifyDataSetChanged();
		}


	    private int tmpposition;

		@Override
		protected void onPreExecute() {

			myProgress = 0;
			tmpposition = 0;
		}
		
		
	


		@Override
		protected Void doInBackground(String... param) {

	
			
			try {



				tmpposition = Integer.valueOf(param[0]);
				contactname = param[1];

				Uri contactUri = PhoneLookup.CONTENT_FILTER_URI;
			    Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			    try {
			    	ArrayList<ContentProviderOperation> ops =  new ArrayList<ContentProviderOperation>();
			        if (cur!=null && cur.getCount()>0 && cur.moveToFirst()) {
			            do {
			                if (cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(param[0])) {
			                	
			                	String where = ContactsContract.Data.CONTACT_ID + " = ? ";
			        	    	String[] params = new String[] {String.valueOf(cur.getString(cur.getColumnIndex(PhoneLookup._ID)))};
			        	       	ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(where, params).build());
			        								
			        		   		    try {
			        						getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			        						
			        					} catch (RemoteException e) {
			        						//exception = e.getMessage()+"\n"+e.getLocalizedMessage()+"\n"+Arrays.toString(e.getStackTrace()) + " : Pls mail this error to Makinglifeeasy4u@gmail.com";
			        						e.printStackTrace();
			        					} catch (OperationApplicationException e) {
			        				
			        						//exception = e.getMessage()+"\n"+e.getLocalizedMessage()+"\n"+Arrays.toString(e.getStackTrace()) + " : Pls mail this error to Makinglifeeasy4u@gmail.com";
			        						e.printStackTrace();
			        					}
				        		}
			              
			            } while (cur.moveToNext());
			        }

			    } catch (Exception e) {
			        System.out.println(e.getStackTrace());
			    }
			          
				cur.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
  		return null;
		
			
			
		}

	

		
	}


public boolean isValid(List<Contactbean> cdl) {
	
	try {
		
		List<String>  obj = new ArrayList<String>();
		
		for (Iterator iterator = cdl.iterator(); iterator.hasNext();) {
			Contactbean contactbean = (Contactbean) iterator.next();
			if(!obj.contains(contactbean.getContactName())){
				obj.add(contactbean.getContactName());	
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
