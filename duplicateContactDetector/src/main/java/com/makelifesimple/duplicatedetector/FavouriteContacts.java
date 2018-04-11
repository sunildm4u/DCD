package com.makelifesimple.duplicatedetector;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FavouriteContacts extends BaseActivity {

	
	ProgressDialog progressBar;
	int contactcount;

	List<Uri> photoUriList = new ArrayList<Uri>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favourite_contacts);
		
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
		progressBar.dismiss();
		
		//List<String> url = new ArrayList<String>();
	Uri[] bmparray = new Uri[photoUriList.size()];

		for (int i = 0; i < photoUriList.size(); i++) {
		   /* I fetch image with param String URL and return as Bitmap */
			System.out.println(photoUriList.get(i));
			bmparray[i] = photoUriList.get(i);
			
		}
		
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
		//Bitmap[] OBJ = (Bitmap[]) photoUriList.toArray();
	    gridview.setAdapter(new ImageAdapter(FavouriteContacts.this,bmparray));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(FavouriteContacts.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	}

	@Override
	protected void onPreExecute() {
		myProgress = 0;
	}
	
	



	@Override
	protected Void doInBackground(Void... params) {

		try {
			ContentResolver cr = getContentResolver();
			
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null, null, null);
			progressBar.setMax(cur.getCount());
			contactcount = cur.getCount();
			int tmp = 0;
			cur.moveToFirst();
	        do {
	        	progressBar.setProgress(tmp++);
	        	Long id = cur.getLong((cur.getColumnIndex(ContactsContract.Contacts._ID)));
	        	if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
                 {
        		 
        		 Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id);
        		 InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactUri);
        		 if (input!= null) {
        			 Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        			 InputStream input2 = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactUri);
        			 System.out.println(2);
        			 photoUriList.add(photoUri);   
        		 }
        	    
                 
                 }
	        	
	        } while (cur.moveToNext());
	        
		   cur.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}

	
}



	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private Uri[] mPics;


	    public ImageAdapter(Context c, Uri[] pics) {
	        mContext = c;
	        mPics = pics;
	    }

	    public int getCount() {
	        return mPics.length;
	    }

	    public Object getItem(int position) {
	        return mPics[position];
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        //File f = new File(((Uri)getItem(position)).getPath());
	       // if(f.exists())
	        imageView.setImageURI((Uri) getItem(position));
	        return imageView;
	    }
	    // references to our images
	    private Integer[] mThumbIds = {
	           
	    };
	}
	@Override
	  public void onStart() {
	    super.onStart();
	   // EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	   // EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
}
