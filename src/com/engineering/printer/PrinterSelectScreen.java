package com.engineering.printer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.trilead.ssh2.Connection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PrinterSelectScreen extends Activity{
	
	public static String printer;

	public static boolean duplex;
	boolean dTemp;
	public static Integer number;
	public static final String PRINTER_PREF = "SEASPrintingFavorite";
	public static final String PRINTER_KEY = "printerpreference";
	public static String mFavored;
	private ToggleButton mTogglebutton;
	private Spinner mSpinner;
	private Button mPrintbutton;
	private NumberPicker mNumberPicker;
	private ArrayAdapter<CharSequence> mAdapter;
	
	//public static Integer pps;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	       SharedPreferences settings = getSharedPreferences(PRINTER_PREF, 0);
	       mFavored = settings.getString(PRINTER_KEY, null);
	       if (mFavored == null) {
	           mFavored = "169";
	       }
	 }
	 
	 public void onStop() {
	     super.onStop();

	 }
	 
	 public void onResume()
	 {
		 super.onResume();
		 setContentView(R.layout.printers);
		 
		 
	        
		   mSpinner = (Spinner) findViewById(R.id.printer_spinner);
	        String printers[] = null;
	        boolean has_favored = false;
	        try {
	            //InputStream is = getContentResolver().openInputStream(getIntent().getData());
	        	Log.d("Connection", "Start Connecting");
	            PrintCaller pc = new PrintCaller(new CommandConnection(EngineeringPrinter.connect));
	            List<String> ps = pc.getPrinters();
	            printers = new String[ps.size()];
	            // yes I know this is stupid and could be done much easier but ps.toArray was trippin ballz
	            int i = 0;
	            for(Iterator<String> iter = ps.iterator(); iter.hasNext(); i++) {
	            	printers[i] = iter.next();
	            }
	            has_favored = ps.contains(mFavored);
	        }
	        catch (IOException ioe){
	            new AlertDialog.Builder(getApplicationContext()).setMessage("Could not connect to server! Verify login information and network status.").create().show();
	            Log.d("Connection", "Failed to connect or send");
	        }
	        mAdapter = new ArrayAdapter(
	                this, android.R.layout.simple_spinner_item, printers);
	        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        mSpinner.setAdapter(mAdapter);
	           if (has_favored) {
	                int pos = mAdapter.getPosition(mFavored);
	                mSpinner.setSelection(pos);
	            }
	        mSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	        
	        mTogglebutton = (ToggleButton) findViewById(R.id.duplex_togglebutton);
	        mTogglebutton.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on clicks
	                if (mTogglebutton.isChecked()) {
	                    dTemp=true;
	                } else {
	                    dTemp=false;
	                }
	            }
	        });
	        
	        mNumberPicker= (NumberPicker) findViewById(R.id.number_picker);
	        TextView t1 = (TextView) findViewById(R.id.duplex_label);
	        TextView t2 = (TextView) findViewById(R.id.number_label);
	        
	        //final NumberPicker ppspicker= (NumberPicker) findViewById(R.id.pps_picker);
	        
	        if (EngineeringPrinter.Microsoft) {
	            t1.setVisibility(View.GONE);
	            t2.setVisibility(View.GONE);
	            mNumberPicker.setVisibility(View.GONE);
	            mTogglebutton.setVisibility(View.GONE);
	        }
	        
	        mPrintbutton = (Button) findViewById(R.id.print_button);
	        mPrintbutton.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 number=mNumberPicker.value;
	            	// pps=ppspicker.value;
	            	 duplex=dTemp;
	            	 
	                 SharedPreferences settings = getSharedPreferences(PRINTER_PREF, 0);
	                 SharedPreferences.Editor ed = settings.edit();
	                 ed.putString(PRINTER_KEY, mFavored);
	            	 
	            	 //PRINT
	            	 Intent myIntent = new Intent(v.getContext(), LoadingStatusScreen.class);
                  startActivityForResult(myIntent, 0);
	             }
	         });
		 
	 }
	 
	 
	 public static class MyOnItemSelectedListener implements OnItemSelectedListener {
		 public static String printer;
		 	
		    public void onItemSelected(AdapterView<?> parent,
		        View view, int pos, long id) {
		    	//PRINTER WAS SELECTED
		    	printer=parent.getItemAtPosition(pos).toString();
		    	mFavored = printer;
		    }

		    public void onNothingSelected(AdapterView parent) {
		      // Do nothing.
		    }
		}
}
