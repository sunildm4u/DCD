package com.makelifesimple.duplicatedetector;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.simplelife.beans.Contactbean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Exporter extends ListActivity {
	
	List<String> coloumns = new ArrayList<String>();
	ListView lv;
	 List<Model> list = new ArrayList<Model>();
	 RadioGroup rg;
	 RadioButton checkedRadioButton;
	 File fl;
	 boolean nameSelected = false;
	 boolean numberselected = false;
	 boolean emailselected = false;
	 int contactcount;
		ArrayList<Contactbean> contactsList = new ArrayList<Contactbean>(); 
	 
	 ProgressDialog progressBar;
	 String countrycode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exporter);
		
		rg = (RadioGroup)findViewById(R.id.radioGroup1);

	    checkedRadioButton = (RadioButton)rg.findViewById(rg.getCheckedRadioButtonId());
		
		Bundle extras = getIntent().getExtras();
		countrycode = extras.getString("contryCode");
		
		ArrayAdapter<Model> adapter = new InteractiveArrayAdapter(this,
		        getModel());
		    setListAdapter(adapter);	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exporter, menu);
		return true;
	}
	
	public void exportContacts(View view)
	{
	for (Iterator iterator = list.iterator(); iterator.hasNext();) {
		Model type = (Model) iterator.next();
		
		if (type.getName().equals("Name") && type.isSelected())
		{
			nameSelected = true;
		}
		if (type.getName().equals("Number") && type.isSelected()){
			numberselected = true;
		}
		if (type.getName().equals("Email") && type.isSelected()){
			emailselected = true;
		}
	}
	
	progressBar = new ProgressDialog(this);
	progressBar.setCanceledOnTouchOutside(false);
	progressBar.setCancelable(false);
	progressBar.setMessage("Verifying Contacts ...");
	progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	progressBar.setProgress(0);
	progressBar.show();
	
	new BackgroundAsyncTask().execute();
	
	
	
	}
	
	  private List<Model> getModel() {
		    //List<Model> list = new ArrayList<Model>();
		    list.add(get("Name"));
		    list.add(get("Number"));
		    list.add(get("Email"));
//		    list.add(get("Mobile Number"));
//		    list.add(get("Office Number"));
//		    list.add(get("Address"));
	    // Initially select one of the items
		    list.get(0).setSelected(true);
		    list.get(1).setSelected(true);
		    list.get(2).setSelected(true);
		    return list;
		  }

		  private Model get(String s) {
		    return new Model(s);
		  }
		  
			
		  public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void> {
		  		
		  		int myProgress;

		  		@Override
		  		protected void onPostExecute(Void result) {
		  			
		  			
		  			
		  			
		  			 fl = new File(Environment.getExternalStorageDirectory() + "/ExportFiles/");
	  				    
	  				    if(!fl.isDirectory() || !fl.exists())
	  				    {
	  				    	fl.mkdir();
	  				    }
	  			          
	  			 	   checkedRadioButton = (RadioButton)rg.findViewById(rg.getCheckedRadioButtonId());
	  				    
	  				    if( checkedRadioButton.getText().toString().trim().equals("TXT"))
	  					   {
	  				    	 try {
	  							 progressBar.dismiss();
	  							 progressBar = null;
	  						    } catch (Exception e) {
	  						        // nothing
	  						    }
	  				    	String filepath   = fl.getPath(); 
	  					    File f = new File(filepath+"/Contacts.txt");
	  					    try {
								FileWriter fw = new FileWriter(f);
								for (Iterator<Contactbean> iterator = contactsList.iterator(); iterator.hasNext();) {
									Contactbean type =  iterator.next();
									if(nameSelected)
									fw.write(type.getContactName());
									if(numberselected)
									fw.write(","+type.getContactNumber());
									if(emailselected)
										fw.write(","+type.getEmail());
									
									fw.write("\n");
								}
								fw.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	  				    	
	  					   }else if( checkedRadioButton.getText().toString().trim().equals("CSV"))
	  					   {
	  						 try {
	  							 progressBar.dismiss();
	  							 progressBar = null;
	  						    } catch (Exception e) {
	  						        // nothing
	  						    }
	  						   String filepath   = fl.getPath() ;
	  				   
	  				    File f = new File(filepath+"/Contacts.csv");
	  				    
	  				    if(!f.exists()){
	  				    	try {
								f.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
	  				    }
	  				    try {
							FileWriter fw = new FileWriter(f);
							for (Iterator<Contactbean> iterator = contactsList.iterator(); iterator.hasNext();) {
								Contactbean type =  iterator.next();
								if(nameSelected)
									fw.write(type.getContactName());
									if(numberselected)
									fw.write(","+type.getContactNumber());
									if(emailselected)
										fw.write(","+type.getEmail());
									
									fw.write("\n");
								//fw.write(type.getContactName() +","+type.getContactNumber() +","+type.getEmail());
							}
								fw.close();
						} catch (IOException e) {
							
							e.printStackTrace();
						}
	  					 
	  				   }else{
	  					   
	  					   
	  					   String filepath   = fl.getPath() +"/Contacts.pdf";
	  					   progressBar.setTitle("Creating PDF... Pls Wait..");
	  					   Toast.makeText(Exporter.this, "PDF Creation will take a min.. Pls be patient", Toast.LENGTH_LONG).show();
	  					   createPDF(filepath, contactsList);
	  					 try {
	  						 progressBar.dismiss();
	  						 progressBar = null;
	  					    } catch (Exception e) {
	  					        // nothing
	  					    }
	  				   }
	  				    
	  				    
	  				    /** Copied Till Here **/
		  			
		  			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Exporter.this);
		  	 		alertDialogBuilder.setTitle("File Created");
		  	 		alertDialogBuilder
		  					.setMessage("File Saved in Location "+fl.getPath()+"/Contacts."+checkedRadioButton.getText().toString().toLowerCase().trim())
		  					.setCancelable(false)
		  					.setPositiveButton("Open File",new DialogInterface.OnClickListener() {
		  						public void onClick(DialogInterface dialog,int id) {
		  						try {
		  								Intent intent = new Intent();
		  								intent.setAction(android.content.Intent.ACTION_VIEW);
		  								File file = new File(fl.getPath() +"/Contacts" + "."+checkedRadioButton.getText().toString().toLowerCase().trim());
		  								if(checkedRadioButton.getText().toString().toLowerCase().trim().equalsIgnoreCase("pdf")){
		  									intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		  								    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		  								}else{
		  									intent.setDataAndType(Uri.fromFile(file), "text/*");
		  								}
		  		                    try {
		  				                        startActivity(intent);
		  			                    }catch (ActivityNotFoundException e) {
		  				                        Toast.makeText(Exporter.this, 
		  				                            "No Application Available to View PDF", 
		  				                            Toast.LENGTH_SHORT).show();
		  				                    }
		  							} catch (Exception e) {
		  								e.printStackTrace();
		  							}
		  						Exporter.this.finish();
		  						}
		  					  })
		  					.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
		  						public void onClick(DialogInterface dialog,int id) {
		  							dialog.cancel();
		  							Exporter.this.finish();
		  						}
		  					});
		  	 				AlertDialog alertDialog = alertDialogBuilder.create();
		  	 				alertDialog.show();
		  			
		  			
		  		}

		  		@Override
		  		protected void onPreExecute() {
		  			myProgress = 0;
		  		}

		  		@Override
		  		protected Void doInBackground(Void... params) {
		  			  
		  			
		  					ContentResolver cr = getContentResolver();
		  			    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null, null, null);
		  			    	
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
		  			             	   
		  			             	   
		  			             	    Cursor pCur = cr.query(Phone.CONTENT_URI, null,Phone.CONTACT_ID + " = " + id , null, null);
		  			                    StringBuffer sbr = new StringBuffer();
		  			 	 	           if(pCur!=null && pCur.getCount()>0){
		  			                    while (pCur.moveToNext()) {
		  			 	               
		  			 	 	            String contactNumber=null;
		  			 	 	           
		  			 	 	            if(pCur.getString(pCur.getColumnIndex(Phone.TYPE))!=null && pCur.getString(pCur.getColumnIndex(Phone.TYPE)).equalsIgnoreCase("2"))
		  			 	 	            {
		  			 	 	            contactNumber = pCur.getString(pCur.getColumnIndex(Phone.NUMBER));	
		  			 	 	            System.out.println("1 "+contactNumber);
		  			 	 	            }
		  			 	 	            
		  			 	 	            
		  			 	 	            
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
		  			              	           if(sbr.length()!=0)
		  			              	           {
		  			              	        	 sbr.append("/"+contactNumber);
		  			              	           }else{
		  			              	        	 sbr.append(contactNumber);
		  			              	           }
		  			              	           
		  			                          
		  			                           String contactname = pCur.getString(pCur.getColumnIndex(Phone.DISPLAY_NAME));
		  			                           //System.out.println(contactname +" : " + contactNumber);
		  			                           if(contactname==null || contactname.equalsIgnoreCase("null"))
		  			                           {
		  			                        	   contactname = "Empty Contact";
		  			                           }
		  			                           cb.setContactName(contactname.toLowerCase());
		  			                   }
		  			                 }
		  			              	   if(pCur!=null)pCur.close();
		  			              	System.out.println("2 "+sbr.toString());
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
		  			            }
		  			          }
		  			        
		  		}
		  			          if(cur!=null)cur.close();
		  			          
		  			       
		  			          
		  		
		  			  		return null;
		  				
		  		}

		  		
		  	}
		  	
		  
		  public void createPDF( String file, ArrayList<Contactbean> smsList2)
		  {
		  	
		  try {
		  	Document document = new Document();
		      PdfWriter.getInstance(document, new FileOutputStream(new File(file)));
		      document.open();
		      addMetaData(document);
		      addTitlePage(document);
		      createTable(document);
		      document.close();
		  } catch (Exception e) {
		  	// TODO: handle exception
		  }

		  }

		  private static void addMetaData(Document document) {
		      document.addTitle("Contacts  PDF");
		     
		    }

		  private static void addEmptyLine(Paragraph paragraph, int number) {
		      for (int i = 0; i < number; i++) {
		        paragraph.add(new Paragraph(" "));
		      }
		    }

		    private static void addTitlePage(Document document) throws DocumentException {
		      Paragraph preface = new Paragraph();
		      // We add one empty line
		      addEmptyLine(preface, 1);
		      // Lets write a big header
		      preface.add(new Paragraph("SMS Conversation Document"));

		      addEmptyLine(preface, 1);
		      // Will create: Report generated by: _name, _date
		      preface.add(new Paragraph("Report generated by: " + "Android App "+ ", " + new Date()));
		      addEmptyLine(preface, 3);
		      preface.add(new Paragraph("This document describes Conversation Between Two People and it is System Created"));

		      addEmptyLine(preface, 8);

		      //preface.add(new Paragraph("This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-)."));

		      document.add(preface);
		      // Start a new page
		    
		    }

		    //private static void addContent(Document document) throws DocumentException {}

		  	  private  void createTable(Document subCatPart)
		  	      {
		  	   
		  		try {
		  			PdfPTable table = new PdfPTable(3);
		  			PdfPCell c1 = new PdfPCell(new Phrase("Person"));
		  			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		  			table.addCell(c1);

		  			c1 = new PdfPCell(new Phrase("Date / Time"));
		  			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		  			table.addCell(c1);

		  			c1 = new PdfPCell(new Phrase("Message"));
		  			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		  			table.addCell(c1);
		  			
		  		  		
		  		
	  					
		  			
		  			
		  			for (Iterator<Contactbean> iterator = contactsList.iterator(); iterator.hasNext();) {
		  				Contactbean type = iterator.next();
		  				
		  				if(nameSelected)
		  				{
		  					table.addCell(type.getContactName());
		  				}else{
		  					table.addCell("");
		  				}
		  					
							if(numberselected){
								table.addCell(type.getContactNumber());
							}else{
								table.addCell("");
							}
							if(emailselected){
								table.addCell(type.getEmail());
							}else{
								table.addCell("");
							}
							
							
		  				
		  			}

		  			

		  			subCatPart.add(table);
		  		} catch (DocumentException e) {
		  			// TODO Auto-generated catch block
		  			e.printStackTrace();
		  		}

		  	  }
		  	@Override
			  public void onStart() {
			    super.onStart();
		//	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
			  }

			  @Override
			  public void onStop() {
			    super.onStop();
			 //   EasyTracker.getInstance(this).activityStop(this);  // Add this method.
			  }

}
