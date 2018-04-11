package com.makelifesimple.duplicatedetector;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class RestoreContacts extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restore_contacts);
		
		
	}
	
	public void openBackedUpFile(View view) throws FileNotFoundException
	{
		
		File f = new File(Environment.getExternalStorageDirectory() + "/ContactsBackup/", "Contacts.vcf");
		
		//File f = new File(Environment.getExternalStorageDirectory() + "/ContactsBackup/Contacts.vcf");
		
		if(f.exists())
		{
		     String path=Environment.getExternalStorageDirectory() + "/ContactsBackup/Contacts.vcf";
             Intent intent = new Intent();
             intent.setAction(android.content.Intent.ACTION_VIEW);
             File file = new File(path);
            
             MimeTypeMap mime = MimeTypeMap.getSingleton();
             String ext=file.getName().substring(file.getName().indexOf(".")+1);
             String type = mime.getMimeTypeFromExtension(ext);
             intent.setDataAndType(Uri.fromFile(file),type);
             startActivity(intent);
		  
             Toast.makeText(this, "Please select Contacts to open this file", Toast.LENGTH_LONG).show();
		
		}else{
				Toast.makeText(this, "No Backup File Found", Toast.LENGTH_LONG).show();
		}
		
		
		
		Toast.makeText(this, "working", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restore_contacts, menu);
		return true;
	}

}
