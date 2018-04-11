package com.makelifesimple.duplicatedetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.simplelife.beans.CSVReader;
import com.simplelife.beans.Contactbean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactsImporter extends Activity {
	


	private static final int REQUEST_CODE = 6384; // onActivityResult request code
	EditText et;
	Spinner sp1;
	Spinner sp2;
	Spinner sp3;
	List<String> headers = new ArrayList<String>();
	List<Contactbean> cbList = new ArrayList<Contactbean>();
	File file;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_contacts_importer);
		et = (EditText) findViewById(R.id.editText1);
		sp1 = (Spinner) findViewById(R.id.spinner1);
		sp2 = (Spinner) findViewById(R.id.spinner2);
		sp3 = (Spinner) findViewById(R.id.spinner3);
		
		//sp1.setOnItemSelectedListener(listener)
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void load(View v)
	{
		
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContactsImporter.this);
  	 		alertDialogBuilder.setTitle("File Created");
  	 		alertDialogBuilder
  	 				.setIcon(R.drawable.warning)
  	 				.setTitle("Warning")
  					.setMessage("Make sure the file you choose is either CSV or XLS Only. If xls please save it (don't rename) as CSV and then load. And none of the coloumn data is having  \",\"(Comma), Else may lead to improper contacts creation")
  					.setCancelable(false)
  					.setPositiveButton("OK - Select File",new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog,int id) {
  						try {
  							showChooser();
  							dialog.cancel();
  						} catch (Exception e) {
  								e.printStackTrace();
  							}
  						
  						}
  					  })
  					.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog,int id) {
  							dialog.cancel();
  							
  						}
  					});
  	 				AlertDialog alertDialog = alertDialogBuilder.create();
  	 				alertDialog.show();
		
		
		
		
		
	}
	
	public void importcontact(View v)
	{
		try {
			
			if(headers.size()==0)
			{
				Toast.makeText(ContactsImporter.this,"Please Load the File with Contacts", Toast.LENGTH_LONG).show();
				return;
			}
			
			cbList.clear();
			
			new ImportContacts(sp1.getSelectedItemPosition(),sp2.getSelectedItemPosition(),sp3.getSelectedItemPosition()).execute();
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
		
	}
	
	private void showChooser() {
		// Use the GET_CONTENT intent from the utility class
		Intent target = FileUtils.createGetContentIntent();
		// Create the chooser Intent
		Intent intent = Intent.createChooser(
				target, "Select File");
		try {
			startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			// The reason for the existence of aFileChooser
		}				
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:	
			// If the file selection was successful

			
			if (resultCode == RESULT_OK) {		

				
				if (data != null) {
					// Get the URI of the selected file
					final Uri uri = data.getData();
					headers.clear();
					try {
						// Create a file instance from the URI
						file = FileUtils.getFile(uri);
						if(file.getName().toString().toLowerCase().endsWith(".csv")||file.getName().toString().toLowerCase().endsWith(".xls"))
						{
							
							if(file.getName().toString().toLowerCase().endsWith(".xls"))
							{
								Toast.makeText(ContactsImporter.this,"Please open  the xls and save as csv, and then load the file ", Toast.LENGTH_LONG).show();
							}
							
							Toast.makeText(ContactsImporter.this,"File Selected: "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
						}else{
							
							Toast.makeText(ContactsImporter.this,"Invalid File, Please select either csv or xls: ", Toast.LENGTH_LONG).show();
							return;
							
						}
						et.setText(file.getName());
						CSVReader reader = new CSVReader(new FileReader(file.getAbsoluteFile()));
						String [] nextLine;
						while ((nextLine = reader.readNext()) != null) {
							for (int i = 0; i < nextLine.length; i++) {
								headers.add(nextLine[i]);
								
							}
							break;
						}
						
						
						ArrayAdapter dataAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, headers);
					    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					    sp1.setAdapter(dataAdapter);
					    sp2.setAdapter(dataAdapter);
					    sp3.setAdapter(dataAdapter);

					} catch (Exception e) {
						Log.e("FileSelectorTestActiviy",e.getMessage());
					}
				}
			} 
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	public class ImportContacts extends AsyncTask<Void, Integer, Void> {
		
		  int deletecontactNumbers ;
		  ProgressDialog progressBar;
 		  private String exception = "";
		  int itemPos1;
		int itemPos2;
		int itemPos3;

         public ImportContacts(int itemPos1,int itemPos2,int itemPos3)
		 {
			 this.itemPos1 = itemPos1;
			 this.itemPos2 = itemPos1;
			 this.itemPos3 = itemPos1;
		 }



		@Override
		protected void onPostExecute(Void result) {
			progressBar.dismiss();
			
			Toast.makeText(ContactsImporter.this,"All Contacts are imported to contacts", Toast.LENGTH_LONG).show();
			//startActivity(new Intent(ContactsImporter.this,LandingPage.class));
		}

		@Override
		protected void onPreExecute() {
	        
		    progressBar = new ProgressDialog(ContactsImporter.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			
			progressBar.setMessage("Creating  Contacts ... Please be patient");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
			deletecontactNumbers = 0;
		}
		
		@Override
		protected Void doInBackground(Void... param) {
			
			try {
				
				CSVReader reader = new CSVReader(new FileReader(file.getAbsoluteFile()));
				String [] nextLine;
				while ((nextLine = reader.readNext()) != null) {
					Contactbean cb = new Contactbean();
					for (int i = 0; i < nextLine.length; i++) {
						
						cb.setContactName(nextLine[itemPos1]);
						cb.setContactNumber(nextLine[itemPos2]);
						cb.setEmail(nextLine[itemPos3]);
						
					}
					cbList.add(cb);
				}
				
				progressBar.setMax(cbList.size());
				
				for (int i = 1; i < cbList.size(); i++) {
					
					Contactbean typebean = (Contactbean) cbList.get(i);
					progressBar.setProgress(i);

					ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();

					 ops.add(ContentProviderOperation.newInsert(
					 ContactsContract.RawContacts.CONTENT_URI)
					     .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
					     .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
					     .build());

					 //------------------------------------------------------ Names
					 if (typebean.getContactName() != null) {
					     ops.add(ContentProviderOperation.newInsert(
					     ContactsContract.Data.CONTENT_URI)
					         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					         .withValue(ContactsContract.Data.MIMETYPE,
					     ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					         .withValue(
					     ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
					     typebean.getContactName()).build());
					 }

					 //------------------------------------------------------ Mobile Number                     
					 if (typebean.getContactNumber() != null) {
					     ops.add(ContentProviderOperation.
					     newInsert(ContactsContract.Data.CONTENT_URI)
					         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					         .withValue(ContactsContract.Data.MIMETYPE,
					     ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					         .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, typebean.getContactNumber())
					         .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
					     ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					         .build());
					 }


					 //------------------------------------------------------ Email
					 if (typebean.getEmail() != null) {
					     ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					         .withValue(ContactsContract.Data.MIMETYPE,
					     ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					         .withValue(ContactsContract.CommonDataKinds.Email.DATA, typebean.getEmail())
					         .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
					         .build());
					 }

					
					 // Asking the Contact provider to create a new contact                 
					 try {
					     getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
					 } catch (Exception e) {
					     e.printStackTrace();
					     Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
					 } 
					
							 
							 
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			return null;
		}
		
	}
	


	
}
