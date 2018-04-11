package com.makelifesimple.duplicatedetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
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
import java.util.Map.Entry;

//import com.google.analytics.tracking.android.EasyTracker;

public class NameBasedDupRemoaval extends BaseActivity {
	
	int contactcount;
	List<String> groupList;
    List<String> childList;
	String countrycode;
	TextView tempView;
	Map<String,List<Contactbean>> aMap = new HashMap<String,List<Contactbean>>();
	Map<String,List<Contactbean>> tempMap = new HashMap<String,List<Contactbean>>();
	Map<String, List<String>> laptopCollection;
	List<Contactbean> contactBeanList;

	ExpandableListView expListView;
	ProgressDialog progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove_dup_numbers);
		
		tempView = (TextView)findViewById(R.id.labelview);
		
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
		        	    expListView = (ExpandableListView) findViewById(R.id.androidlist2);
						final ExpandableListAdapter2 expListAdapter = new ExpandableListAdapter2(
						NameBasedDupRemoaval.this, groupList, laptopCollection);
						expListView.setAdapter(expListAdapter);
						
						Toast.makeText(NameBasedDupRemoaval.this, "Please retain only one contact Number for a name ", Toast.LENGTH_LONG).show();
		        }else{
		        	tempView.setText("You Dont have Name Based duplicate contacts(Same Name havnig having more that one Number)");
		        	tempView.setVisibility(TextView.VISIBLE);
		        	
		        }
		      
		      
	
		}

		@Override
		protected void onPreExecute() {
			myProgress = 0;
		}
		
		
	


		@Override
		protected Void doInBackground(Void... params) {
			//String[] projection2 = new String[]{Phone.NUMBER,Phone.LOOKUP_KEY,Phone.TYPE,Phone.DISPLAY_NAME};
			ContentResolver cr = getContentResolver();
			//String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER};
	    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
			cur.moveToFirst();
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
	                  
	                           
	                           System.out.println(contactname +" : " + contactNumber);
	                           if(contactname==null || contactname.equalsIgnoreCase("null"))
	                           {
	                        	   contactname = "Empty Contact";
	                           }
	                           cb.setContactName(contactname.toLowerCase());
	                           
	              	         
		               	       if(aMap.get(contactname)!=null && contactNumber.length()>2)
		               	       {
		              	    	  List<Contactbean> list = aMap.get(contactname);
		              	    	  list.add(cb);
		               	       }else{
		              	    	  List<Contactbean> list = new ArrayList<Contactbean>();
		              	    	  list.add(cb);
		              	    	if(contactname!=null && list!=null)
		              	    	  aMap.put(contactname,list);
		              	    	}
	                   }
	                   }
	                   
	              	   if(pCur!=null)pCur.close();
	              	   
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
	            	 if(name!=null && cdl!=null)
	            	 tempMap.put(name, cdl);
	             }
			
			
		}
         System.out.println(aMap.size());
         System.out.println(tempMap.size());
         
         
         for(Iterator<Entry<String, List<Contactbean>>> it = tempMap.entrySet().iterator(); it.hasNext(); )
         {
		        Entry<String, List<Contactbean>> entry = it.next();
		        System.out.println("Contact Name :"+entry.getKey());
		       for (Iterator<Contactbean> iterator = entry.getValue().iterator(); iterator.hasNext();) 
		       {
		    	   Contactbean type = (Contactbean) iterator.next();
		    	   System.out.println("id  "+type.getContactid());
		       }
		  }
         
         StringBuffer sb1 = new StringBuffer();

  		return null;
		}

		
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
								
								List<String> child = laptopCollections.get(laptops.get(groupPosition));
								System.out.println(groupPosition + ":" +childPosition);
								List<Contactbean> Cbl	 = tempMap.get(laptops.get(groupPosition));
								new RemoveCnotactTask().execute(String.valueOf(Cbl.get(childPosition).getContactid()));
								child.remove(childPosition);
								notifyDataSetChanged();
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
		String contactid;

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(NameBasedDupRemoaval.this, "Contact  is Removed", Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onPreExecute() {
			myProgress = 0;
		}
		
		
	


		@Override
		protected Void doInBackground(String... param) {
	
			try {
				
				//System.out.println(param[0]+":"+param[1]);
				
				contactid = param[0];
			     
				Uri contactUri = PhoneLookup.CONTENT_FILTER_URI;
			    Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			    try {
			        if (cur!=null && cur.getCount()>0 && cur.moveToFirst()) {
			            do {
			                if (cur.getString(cur.getColumnIndex(PhoneLookup._ID)).equalsIgnoreCase(param[0])) {
			                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
			                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
			      		      String where = ContactsContract.Data._ID + " = ? ";
						      String[] params2 = new String[] {param[0]};
			                    getContentResolver().delete(uri, where, params2);
			                   
			                }

			            } while (cur.moveToNext());
			        }

			    } catch (Exception e) {
			        System.out.println(e.getStackTrace());
			    }
			          
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
  		return null;
		}

	

		
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
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.remove_dup_numbers, menu);
//		return true;
//	}
	 public void onBackPressed() {
	  	   Log.d("CDA", "onBackPressed Called");
	  	   Intent i = new Intent(NameBasedDupRemoaval.this,MainActivity2.class);
	  	 i.putExtra("contryCode", countrycode);
	  	   startActivity(i);
	  	}
}
