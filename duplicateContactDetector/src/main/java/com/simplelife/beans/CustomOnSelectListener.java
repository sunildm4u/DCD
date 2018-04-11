package com.simplelife.beans;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;


public class CustomOnSelectListener implements OnItemSelectedListener {

	
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,long id) {
		
//		if(parent.getItemAtPosition(pos).toString()=="Others")
//		{
//			Dialog obj = new Dialog(parent.getContext());
//			obj.setContentView();
//			obj.setTitle("Custom Dialog");
//			obj.show();
//		}
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
