package com.makelifesimple.duplicatedetector;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.simplelife.beans.Contactbean;

public class CustomListAdapter extends ArrayAdapter<Contactbean> {

    private ArrayList<Contactbean> items;
    private Context context;

    public CustomListAdapter(Context context, int textViewResourceId, ArrayList<Contactbean> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_cutom_list_objectvew, null);
        }

        Contactbean item = items.get(position);
        if (item!= null) {
            TextView itemView = (TextView) view.findViewById(R.id.textViewcustom );
           
            	itemView.setTextColor(Color.parseColor("#3f9fe0"));
            	//itemView.setTextColor(Color.BLUE);
            	itemView.setText("  " + items.get(position).toString());
            	itemView.setBackgroundColor(Color.WHITE); 
                
         }

        return view;
    }
}