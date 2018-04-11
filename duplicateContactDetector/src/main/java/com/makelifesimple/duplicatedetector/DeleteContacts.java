package com.makelifesimple.duplicatedetector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeleteContacts extends BaseActivity {
	ProgressDialog progressBar;
	Map<String,String> aMap = new LinkedHashMap<String, String>();
	List<String> temIdsList = new ArrayList<String>();
	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_contacts);
		tv = (TextView) findViewById(R.id.textViewforconcount);
		
		new GetData().execute();
	}

		
	public void deleteallcontacts(View view)
	{
	  new DeleteAllContacts().execute();	
	}
	
	public void removeselected(View view)
	{
		temIdsList.clear();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Contacts to Delete");
		builder.setMultiChoiceItems(aMap.values().toArray(new String[aMap.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int i, boolean isChecked) {
	            if (isChecked) {
        		  temIdsList.add(aMap.keySet().toArray(new String[aMap.size()])[i]);
	                
	            }else{
                  	temIdsList.remove(aMap.keySet().toArray(new String[aMap.size()])[i]);
                  } 
	        }
	    })
	   .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int whichButton) {
                                	new DeleteContactsSelected().execute();
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
		tv.setText(String.valueOf(aMap.size()));
		}

		@Override
		protected void onPreExecute() {
			myProgress = 0;
			progressBar = new ProgressDialog(DeleteContacts.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Verifying Contacts ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
		}
		
		



		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				Uri uri = ContactsContract.Contacts.CONTENT_URI;
		        String[] projection = new String[] {
		                ContactsContract.Contacts._ID,
		                ContactsContract.Contacts.DISPLAY_NAME
		        };
		        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1" ;
		        String[] selectionArgs = null;
		        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;

		        //return managedQuery(, projection, selection, selectionArgs, sortOrder);
		        ContentResolver cr = getContentResolver();
		        Cursor datacur = cr.query(uri, projection, selection, selectionArgs, sortOrder);
				progressBar.setMax(datacur.getCount());
if(datacur!=null && datacur.getCount()>0){
				while (datacur.moveToNext())
				{
					myProgress++;
					progressBar.setProgress(myProgress);		    	
					  String id = datacur.getString(datacur.getColumnIndex(ContactsContract.Contacts._ID));
					  String  contactName = datacur.getString(datacur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					  System.out.println(contactName);
				      if(contactName==null||contactName.equalsIgnoreCase("null"))
				   	   contactName="Empty Contact";
				      
				      aMap.put(id, contactName);
				     // emailList.add(email);
				}
			}
				  if(datacur!=null)datacur.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	             return null;
		}

		
	}
	
	public class DeleteContactsSelected extends AsyncTask<Void, Integer, Void> {
		
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
			
			
			Toast.makeText(DeleteContacts.this,deleteconatactemailidss+ "  contacts Deleted..", Toast.LENGTH_LONG).show();
			
		}

		@Override
		protected void onPreExecute() {
			deleteconatactemailidss = 0;
			progress = 0;
	    	progressBar = new ProgressDialog(DeleteContacts.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Deleting Contacts ...");
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
	public class DeleteAllContacts extends AsyncTask<Void, Integer, Void> {
		
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
			Toast.makeText(DeleteContacts.this,deleteconatactemailidss+ "  contacts Deleted..", Toast.LENGTH_LONG).show();
			
		}

		@Override
		protected void onPreExecute() {
			deleteconatactemailidss = 0;
			progress = 0;
	    	progressBar = new ProgressDialog(DeleteContacts.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Deleting Contacts ...");
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
		        progressBar.setProgress(deleteconatactemailidss);
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
	   // EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
}
