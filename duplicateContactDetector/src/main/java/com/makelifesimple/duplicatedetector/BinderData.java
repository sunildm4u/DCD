package com.makelifesimple.duplicatedetector;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BinderData extends BaseAdapter {

	// XML node keys
    static final String KEY_TAG = "weatherdata"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_CITY = "city";
    static final String KEY_TEMP_C = "tempc";
    static final String KEY_TEMP_F = "tempf";
    static final String KEY_CONDN = "condition";
    static final String KEY_SPEED = "windspeed";
    static final String KEY_ICON = "icon";
	
	LayoutInflater inflater;
	ImageView thumb_image;
	List<HashMap<String,String>> weatherDataCollection;
	ViewHolder holder;
	public BinderData() {
		// TODO Auto-generated constructor stub
	}
	
	public BinderData(Activity act, List<HashMap<String,String>> map) {
		
		this.weatherDataCollection = map;
		
		inflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	public int getCount() {
		// TODO Auto-generated method stub
//		return idlist.size();
		return weatherDataCollection.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		 
		View vi=convertView;
	    if(convertView==null){
	     
	      vi = inflater.inflate(R.layout.list_row, null);
	      holder = new ViewHolder();
	     
	      holder.tvCity = (TextView)vi.findViewById(R.id.App_Name); // city name
	      holder.tvWeather = (TextView)vi.findViewById(R.id.App_Desc); // city weather overview
	      holder.tvTemperature =  (ImageView)vi.findViewById(R.id.Price); // city temperature
	      holder.tvWeatherImage =(ImageView)vi.findViewById(R.id.list_image); // thumb image
	 
	      vi.setTag(holder);
	    }
	    else{
	    	
	    	holder = (ViewHolder)vi.getTag();
	    }

	      // Setting all values in listview
	      
	      holder.tvCity.setText(weatherDataCollection.get(position).get("App_Name"));
	      holder.tvWeather.setText(weatherDataCollection.get(position).get("App_Desc"));
	      String uri = "";
	      if(weatherDataCollection.get(position).get("Price").equalsIgnoreCase("pro"))
	      {
	          uri = "drawable/label";
		     
	      }else{
	    	  uri = "drawable/free";
	      }
	      int imageResource = vi.getContext().getApplicationContext().getResources().getIdentifier(uri, null, vi.getContext().getApplicationContext().getPackageName());
	      Drawable image = vi.getContext().getResources().getDrawable(imageResource);
	      holder.tvTemperature.setImageDrawable(image);
	      //holder.tvTemperature.setText(weatherDataCollection.get(position).get("Price"));
	      
	      //Setting an image
	      String uri2 = "drawable/"+ weatherDataCollection.get(position).get("image_url");
	      int imageResource2 = vi.getContext().getApplicationContext().getResources().getIdentifier(uri2, null, vi.getContext().getApplicationContext().getPackageName());
	      Drawable image2 = vi.getContext().getResources().getDrawable(imageResource2);
	      holder.tvWeatherImage.setImageDrawable(image2);
	      
	      return vi;
	}
	
	/*
	 * 
	 * */
	static class ViewHolder{
		
		TextView tvCity;
		ImageView tvTemperature;
		TextView tvWeather;
		ImageView tvWeatherImage;
	}
	
}
