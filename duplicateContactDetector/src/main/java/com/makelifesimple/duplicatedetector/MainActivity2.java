package com.makelifesimple.duplicatedetector;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.makelifesimple.duplicatedetector.util.IabBroadcastReceiver;
import com.makelifesimple.duplicatedetector.util.IabHelper;
import com.makelifesimple.duplicatedetector.util.IabResult;
import com.makelifesimple.duplicatedetector.util.Inventory;
import com.makelifesimple.duplicatedetector.util.Purchase;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity2 extends BaseActivity implements IabBroadcastReceiver.IabBroadcastListener {

	private FirebaseAnalytics mFirebaseAnalytics;
	ArrayList<HashMap<String, String>> songsList = null;
	String countrycode;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    //static final String SKU_PREMIUM = "premium";
    static final String SKU_PREMIUM = "android.test.purchased";
    IabHelper mHelper;
    boolean mIsPremium = false;
	static final String TAG = "remover";
	static final int RC_REQUEST = 10001;
	Inventory inv;
	//Tracker mTracker ;
	SharedPreferences settings ;
	BinderData bindingData = null;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 95;
	IabBroadcastReceiver mBroadcastReceiver;

   // boolean cosumetemp = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Obtain the FirebaseAnalytics instance.
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sharedPref = getSharedPreferences("mypref", 0);
		editor = sharedPref.edit();
		AnalticsApplication application = (AnalticsApplication) getApplication();
		//mTracker = application.getDefaultTracker();
		setContentView(R.layout.activity_main_landing);


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if( checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(android.Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			{
				requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 95);

			} else
			{
				continueApplication();
			}
		}else{
			continueApplication();
		}


	}


	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 95: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED &&  grantResults[1] == PackageManager.PERMISSION_GRANTED &&  grantResults[2] == PackageManager.PERMISSION_GRANTED) {

					continueApplication();
					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity2.this);
					alertDialogBuilder.setTitle("Permissions Required !!");
					alertDialogBuilder.setIcon(R.drawable.warning);
					alertDialogBuilder.setMessage("Dear user \n\n  Ability to Read , Write Contacts and Save in SD Card is basic permission we need for this app. How else can I clean your contacts ?  Please provide permission.")
							.setCancelable(false)
							.setPositiveButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									Toast.makeText(MainActivity2.this, "Sorry Cant Continue without permission !! ", Toast.LENGTH_LONG).show();
									dialog.cancel();
								}
							})
							.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
										requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 95);
									}
									dialog.cancel();
								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	private void continueApplication() {


		AnalticsApplication application = (AnalticsApplication) getApplication();
		//mTracker = application.getDefaultTracker();

		Intent intent = this.getIntent();
		Uri uri = intent.getData();

		if (!new File(Environment.getExternalStorageDirectory() + "/ContactsBackup/Contacts.vcf").exists() && !sharedPref.getBoolean("Ignored", false)) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity2.this);
			alertDialogBuilder.setTitle("Backup Contacts");
			alertDialogBuilder.setIcon(R.drawable.saveicon);
			alertDialogBuilder.setMessage("It is strongly advised that you back up your existing contacts before modifying them, Would you like to create back up now ?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							new ExportContacts().execute();
							dialog.cancel();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							editor.putBoolean("Ignored", true);
							editor.commit();
							dialog.cancel();
						}
					});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}


		Bundle extras = getIntent().getExtras();
		countrycode = extras.getString("contryCode");
		ListView list;
		songsList = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("App_Name", "Duplicate Contact's Detector");
		map.put("App_Desc", "Find Duplicates in  swift..");
		map.put("Price", "free");
		map.put("image_url", "dupcheck");
		songsList.add(map);

		HashMap<String, String> map8 = new HashMap<String, String>();
		map8.put("App_Name", "Duplicate Remover - Name Based");
		map8.put("App_Desc", "Removes Duplicates - Same Name Different Numbers..");
		map8.put("Price", "free");
		map8.put("image_url", "test");
		songsList.add(map8);

		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("App_Name", "Duplicate Remover - Number Based");
		map2.put("App_Desc", "Removes Duplicates - Same Number Different Names..");
		map2.put("Price", "free");
		map2.put("image_url", "remcon");
		songsList.add(map2);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("App_Name", "Duplicate Remover - Exact Number Duplicates");
		map1.put("App_Desc", "Removes Duplicates - Exact Matches ..");
		map1.put("Price", "free");
		map1.put("image_url", "duprem");
		songsList.add(map1);

		HashMap<String, String> map5 = new HashMap<String, String>();
		map5.put("App_Name", "Remove Unused E-Mail Contacts");
		map5.put("App_Desc", "Removes E-mail not assocaited to any contact");
		map5.put("Price", "free");
		map5.put("image_url", "rememail");
		songsList.add(map5);

		HashMap<String, String> map21 = new HashMap<String, String>();
		map21.put("App_Name", "Duplicate Remover - Exact E-Mail Duplicates");
		map21.put("App_Desc", "Removes email contacts Exact Matches ..");
		map21.put("Price", "free");
		map21.put("image_url", "duprememail");
		songsList.add(map21);

		HashMap<String, String> map55 = new HashMap<String, String>();
		map55.put("App_Name", "Remove Duplicates within Contacts");
		map55.put("App_Desc", "Removed Internal Duplicates with in a Contact");
		map55.put("Price", "free");
		map55.put("image_url", "internaldupcon");
		songsList.add(map55);

		HashMap<String, String> map66 = new HashMap<String, String>();
		map66.put("App_Name", "Merge Contacts - Name Based");
		map66.put("App_Desc", "Merges Contacts with Same Name Different Number");
		map66.put("Price", "pro");
		map66.put("image_url", "mergeicon");
		songsList.add(map66);

		HashMap<String, String> map77 = new HashMap<String, String>();
		map77.put("App_Name", "Remove Empty/Invisible Contacts");
		map77.put("App_Desc", "Remove Empty/Invisible with no data associated..");
		map77.put("Price", "free");
		map77.put("image_url", "invisiblecon");
		songsList.add(map77);


		HashMap<String, String> map9 = new HashMap<String, String>();
		map9.put("App_Name", "Contact's Importer");
		map9.put("App_Desc", "Import Contacts from csv,Excel");
		map9.put("Price", "free");
		map9.put("image_url", "importicon");
		songsList.add(map9);


		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("App_Name", "Contact's Exporter");
		map3.put("App_Desc", "Export to pdf,csv.Excel..");
		map3.put("Price", "free");
		map3.put("image_url", "exporticon");
		songsList.add(map3);

		HashMap<String, String> map31 = new HashMap<String, String>();
		map31.put("App_Name", "Contact's Remover");
		map31.put("App_Desc", "Remove All/Selected Contacts in a swift");
		map31.put("Price", "free");
		map31.put("image_url", "delcon");
		songsList.add(map31);

		HashMap<String, String> map33 = new HashMap<String, String>();
		map33.put("App_Name", "Add Country Code");
		map33.put("App_Desc", "Adds Country Code to your Numbers");
		map33.put("Price", "free");
		map33.put("image_url", "countrycode");
		songsList.add(map33);

		HashMap<String, String> map32 = new HashMap<String, String>();
		map32.put("App_Name", "Restore Contacts");
		map32.put("App_Desc", "Restore Contacts from BackUp");
		map32.put("Price", "free");
		map32.put("image_url", "restore");
		songsList.add(map32);

		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put("App_Name", "What feature you want ?");
		map4.put("App_Desc", "What new contact feature you want ?");
		map4.put("Price", "free");
		map4.put("image_url", "q");
		songsList.add(map4);

		HashMap<String, String> map7 = new HashMap<String, String>();
		map7.put("App_Name", "Suggestion/Feedback");
		map7.put("Price", "free");
		map7.put("image_url", "rate");
		songsList.add(map7);

		bindingData = new BinderData(this, songsList);
		list = (ListView) findViewById(R.id.list);

		list.setAdapter(bindingData);


		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = null;

				switch (position + 1) {
					case 1:
						i = new Intent(MainActivity2.this, Detector.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 2:
						//if(ispremiumuser() || mIsPremium)
						//{
						i = new Intent(MainActivity2.this, NameBasedDupRemoaval.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						//}else{
						// purchaseApplication();
						//}
						break;

					case 3:

						i = new Intent(MainActivity2.this, NumberBasedDupRemoval.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 4:
						//if (ispremiumuser() || mIsPremium) {
							i = new Intent(MainActivity2.this, Remover1.class);
							i.putExtra("contryCode", countrycode);
							startActivity(i);
						//} else {
						//	purchaseApplication();
						//}

						break;

					case 5:

						i = new Intent(MainActivity2.this, EmailRemover.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 6:

						i = new Intent(MainActivity2.this, EmailExactDuplicates.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 7:

						i = new Intent(MainActivity2.this, InternalDuplicateRemove.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 8:
						if (ispremiumuser() || mIsPremium) {
							i = new Intent(MainActivity2.this, MergeContacts.class);
							i.putExtra("contryCode", countrycode);
							startActivity(i);
							break;
						} else {
							purchaseApplication();
						}
						break;
					case 9:

						i = new Intent(MainActivity2.this, InvisibleContacts.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 10:

						i = new Intent(MainActivity2.this, ContactsImporter.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 11:

						i = new Intent(MainActivity2.this, Exporter.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 12:

						i = new Intent(MainActivity2.this, DeleteContacts.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 13:

						i = new Intent(MainActivity2.this, RenameCountryCode.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 14:

						i = new Intent(MainActivity2.this, RestoreContacts.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 15:

						i = new Intent(MainActivity2.this, OnDemand.class);
						i.putExtra("contryCode", countrycode);
						startActivity(i);
						break;

					case 16:
						try {
							//Purchase premiumPurchase = inv.getPurchase(SKU_PREMIUM);
							//Toast.makeText(MainActivity2.this, "Trying to Consume : " + premiumPurchase.getItemType(), Toast.LENGTH_SHORT).show();
							//mHelper.consumeAsync(premiumPurchase, mConsumeFinishedListener);
							i = new Intent(MainActivity2.this, Ratethisapp.class);
							i.putExtra("contryCode", countrycode);
							startActivity(i);
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}


					default:
						break;
				}
			}
		});

		if (ispremiumuser()) {
			songsList.get(3).put("Price", "free");
			songsList.get(7).put("Price", "free");
			bindingData.notifyDataSetChanged();
		}

	}

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null) return;

			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in our
				// game world's logic, which in our case means filling the gas tank a bit
				Log.d(TAG, "Consumption successful. Provisioning.");
				Toast.makeText(MainActivity2.this, "Succesfully Consumed", Toast.LENGTH_SHORT).show();
			}
			else {
				complain("Error while consuming: " + result);
			}
//			updateUi();
			setWaitScreen(false);
			Log.d(TAG, "End consumption flow.");
		}
	};

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                //showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


	public void purchaseApplication() 
	{
         //cosumetemp = true;
		 String payload = ""; 
		 setWaitScreen(true);
		 //if (mHelper != null) mHelper.flagEndAsync();
		 try {
			// if (!mHelper.asisAsyncInProgress()) {

				 mHelper.launchPurchaseFlow(MainActivity2.this, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener, payload);
			 //}

		} catch (Exception e) {
			Toast.makeText(MainActivity2.this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
		}
	}


    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        	inv = inventory;
			try {
				//Purchase premiumPurchase = inv.getPurchase(SKU_PREMIUM);
				//mHelper.consumeAsync(premiumPurchase, mConsumeFinishedListener);
			}catch (Exception e)
			{
				e.printStackTrace();
			}
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
            	for (int i=0; i < 2; i++)
            	{
            		 Toast.makeText(MainActivity2.this, "Failed to query your purchase status,  please make sure you are connected to Internet " , Toast.LENGTH_LONG).show();
            	}
               
                return;
            }
            Log.d(TAG, "Query inventory was successful.");
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            if(mIsPremium)
            {
            	savethepurchase();
            }
       }
    };







    
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) 
            {
            	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
				builder.setMessage("Error Purchasing : " + result);
				builder.setCancelable(false);
				builder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								 dialog.cancel();
								 startActivity(new Intent(MainActivity2.this,MainActivity2.class).putExtra("contryCode", countrycode));
							}
						});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
                return;
            }
            if (purchase.getSku().equals(SKU_PREMIUM)) {
                 mIsPremium = true;
                 savethepurchase();
                 setWaitScreen(false);

				/*Product product = new Product()
						.setName("premium")
						.setPrice(2.00d);

				ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
						.setTransactionId(purchase.getOrderId());

// Add the transaction data to the event.
				HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
						.setCategory("Premium User")
						.setAction("Purchase")
						.addProduct(product)
						.setProductAction(productAction);

// Send the transaction data with the event.
				mTracker.send(builder.build());*/

				// Send purchase INFO
                 /*EasyTracker easyTracker = EasyTracker.getInstance(MainActivity2.this);

           	  easyTracker.send(MapBuilder
           	      .createTransaction(purchase.getOrderId(),       // (String) Transaction ID
           	                         "In-app Store",   // (String) Affiliation
           	                         2.00d,            // (Double) Order revenue
           	                         0.30d,            // (Double) Tax
           	                         0.0d,             // (Double) Shipping
           	                         "USD")            // (String) Currency code
           	      .build()
           	  );

           	  easyTracker.send(MapBuilder
           	      .createItem(purchase.getOrderId(),               // (String) Transaction ID
           	                  "Duplicate Detector Premium",      // (String) Product name
           	                  SKU_PREMIUM,                  // (String) Product SKU
           	                  "PRO uSER",        // (String) Product category
           	                  1.99d,                    // (Double) Product price
           	                  1L,                       // (Long) Product quantity
           	                  "USD")                    // (String) Currency code
           	      .build()
           	  );*/

				songsList.get(3).put("Price", "free");
				songsList.get(7).put("Price", "free");
				bindingData.notifyDataSetChanged();


                 Toast.makeText(MainActivity2.this, "Congratulations you are a PRO user,", Toast.LENGTH_LONG).show();      
            }
        }
    };
    
    
    public void savethepurchase() 
    {
      	try {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("premiumuser", true);
    	    editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
 	}
    
    public boolean ispremiumuser()
    {
    	try {
    	    return settings.getBoolean("premiumuser", false);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    
    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }
    
    
    public void onBackPressed() {
	  	   Log.d("CDA", "onBackPressed Called");
	  	 Intent intent = new Intent(Intent.ACTION_MAIN);
		 intent.addCategory(Intent.CATEGORY_HOME);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 startActivity(intent);
	  	}
	 
	 @Override
	  public void onStart() {
	    super.onStart();
	    //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
		 //mTracker.setScreenName("MainScreen");
		 //mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	    if(!ispremiumuser())

	    {
	    	
	    String s1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm0TwaiiyZcPKur6/9BGsqRUynH2/bM6OhQt";
		
		String s2 = "+iZiUMaAR/8isNoCOe2oVGi061oDlYkZxxjiSzM6/5Mul/skXrIpBnQ3AsBC/YInKXVP1qMOh1GPlSyNwMk6t5S9+9+09M2+BTQtgonNLSNhZeyUyrtbq+6q7UN6S6hYFA7zx3HIpj6yRPSemB9oOJQJ0uSJ1QJgxYX6QFACqO6F9IrEXk3rS6gP1ZLa3WQXhUt576jd8S1IplEM9G6aNyOFzEqmUMk5oVZe29kpDBm462mDNsIYYS7vwoKcc68lmrjUTETDB+n2C3h/si6Oh80DpBrvqiD+mESFxgy0dxZ8kWCYROwIDAQAB";

		String base64EncodedPublicKey = s1+s2;
		
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		
		mHelper.enableDebugLogging(true);
		

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				public void onIabSetupFinished(IabResult result) {
					Log.d(TAG, "Setup finished.");

					if (!result.isSuccess()) {
						// Oh noes, there was a problem.
						complain("Problem setting up in-app billing: " + result);
						return;
					}

					// Have we been disposed of in the meantime? If so, quit.
					if (mHelper == null) return;

  				    mBroadcastReceiver = new IabBroadcastReceiver(MainActivity2.this);
					IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
					registerReceiver(mBroadcastReceiver, broadcastFilter);

					// IAB is fully set up. Now, let's get an inventory of stuff we own.
					Log.d(TAG, "Setup successful. Querying inventory.");
					try {
						List<String> skulist = new ArrayList<String>();
						skulist.add(SKU_PREMIUM);
						mHelper.queryInventoryAsync(true,skulist,null,mGotInventoryListener);
						//mHelper.queryInventoryAsync(mGotInventoryListener);
					} catch (IabHelper.IabAsyncInProgressException e) {
						complain("Error querying inventory. Another async operation in progress.");
					}
				}
			});
	    }
	  }

	@Override
	public void receivedBroadcast() {
		// Received a broadcast notification that the inventory of items has changed
		Log.d(TAG, "Received broadcast notification. Querying inventory.");
		try {
			mHelper.queryInventoryAsync(mGotInventoryListener);
		} catch (IabHelper.IabAsyncInProgressException e) {
			complain("Error querying inventory. Another async operation in progress.");
		}
	}

	void complain(String message) {
		Log.e(TAG, "**** TrivialDrive Error: " + message);
		Toast.makeText(this, "\"Error: \" + message", Toast.LENGTH_SHORT).show();

	}
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
	        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
	            super.onActivityResult(requestCode, resultCode, data);
	        }
	        else {
	            Log.d(TAG, "onActivityResult handled by IABUtil.");
	        }
	    }

	  @Override
	  public void onStop() {
	    super.onStop();
	   // EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	   
	  }
	  
	   void setWaitScreen(boolean set) {
	        findViewById(R.id.list).setVisibility(set ? View.GONE : View.VISIBLE);
	        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
	    }
	   
	   

	public class ExportContacts extends AsyncTask<Void, Integer, Void> {
		
		int deleteconatactemailidss ;
		ProgressDialog progressBar;

		@Override
		protected void onPostExecute(Void result) {
			
			 try {
				 progressBar.dismiss();
				 progressBar = null;
			    } catch (Exception e) {
			        // nothing
			    }
			Toast.makeText(MainActivity2.this, "All your Conatcts are Succesfully Exported to "+Environment.getExternalStorageDirectory().toString() + File.separator +"/Contacts Backup/Contacts.vcf", Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onPreExecute() {
			progressBar = new ProgressDialog(MainActivity2.this);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.setCancelable(false);
			progressBar.setMessage("Exporting Contacts to /Contacts Backup/Contacts.vcf ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.show();
			deleteconatactemailidss = 0;
			
		}
		
		@Override
		protected Void doInBackground(Void... param) {
			
			 final String vfile = "Contacts.vcf";
			 new File(Environment.getExternalStorageDirectory().toString() + File.separator +"/ContactsBackup/").mkdir();



			 Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
		        if(phones!=null)phones.moveToFirst();
		        progressBar.setMax(phones.getCount());
		        for (int i = 0; i < phones.getCount(); i++) {
		        	progressBar.setProgress(i);
		               String lookupKey = phones.getString(phones
		                       .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
		               Uri uri = Uri.withAppendedPath(
		               ContactsContract.Contacts.CONTENT_VCARD_URI,
		                                             lookupKey);
		               AssetFileDescriptor fd;
		               try {
		                       fd = getContentResolver().openAssetFileDescriptor(uri, "r");
		                       FileDescriptor fdd = fd.getFileDescriptor();
		                       InputStream in = new FileInputStream(fdd);
		                       //FileInputStream fis = fd.createInputStream();
		                       byte[] buf = new byte[(int) fd.getDeclaredLength()];
		                       in.read(buf);
		                       String VCard = new String(buf);
		                       String path = Environment.getExternalStorageDirectory().toString() + File.separator +"/ContactsBackup/" +vfile;
		                       FileOutputStream mFileOutputStream = new FileOutputStream(path,true);
		                       mFileOutputStream.write(VCard.toString().getBytes());
		                       mFileOutputStream.close();
		                       in.close();
		                       phones.moveToNext();
		                       Log.d("Vcard", VCard);
		               } catch (Exception e1) {
		                       // TODO Auto-generated catch block
		                       e1.printStackTrace();
		               }
		        }
			
			return null;
		}
		
	}
	
}
